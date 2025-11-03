package com.example.mgapp.ui.hotspot

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mgapp.R
import com.example.mgapp.data.local.entity.HotspotEntity
import com.example.mgapp.data.local.serializer.HotspotJsonHelper
import com.example.mgapp.data.model.FieldSchema
import com.example.mgapp.data.model.FieldType
import com.example.mgapp.data.model.HotspotSchema
import com.example.mgapp.data.model.ValidationRule
import com.example.mgapp.data.repository.HotspotRepository
import com.example.mgapp.domain.HotspotChange
import com.example.mgapp.domain.ValidationError
import com.example.mgapp.domain.validateForm
import com.example.mgapp.domain.inverse
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

@HiltViewModel
class HotspotViewModel @Inject constructor(
    private val repository: HotspotRepository
) : ViewModel() {

   // UNDO / REDO

    private val undoStack = ArrayDeque<HotspotChange>()
    private val redoStack = ArrayDeque<HotspotChange>()
    private val maxDepth = 30

    private val _canUndo = MutableStateFlow(false)
    val canUndo: StateFlow<Boolean> = _canUndo

    private val _canRedo = MutableStateFlow(false)
    val canRedo: StateFlow<Boolean> = _canRedo

    private val _uiMessage = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val uiMessage: SharedFlow<String> = _uiMessage

    private fun emitUiMessage(msg: String) = _uiMessage.tryEmit(msg)



    val hotspots = repository.getAllHotspots()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // changes management


    fun applyChange(change: HotspotChange, context: Context) = viewModelScope.launch {
        when (change) {
            is HotspotChange.Create -> repository.saveHotspot(change.snapshot)
            is HotspotChange.Update -> repository.updateHotspot(change.after)
            is HotspotChange.Delete -> repository.deleteHotspot(change.snapshot)
            is HotspotChange.Bulk -> applyBulk(change.changes, context)
        }
        pushToUndo(change)

        val message = when (change) {
            is HotspotChange.Create -> context.getString(R.string.hotspot_created)
            is HotspotChange.Update -> context.getString(R.string.hotspot_updated)
            is HotspotChange.Delete -> context.getString(R.string.hotspot_deleted)
            is HotspotChange.Bulk -> context.getString(R.string.changes_applied)
        }
        emitUiMessage(message)
    }

    private suspend fun applyBulk(changes: List<HotspotChange>, context: Context) {
        for (c in changes) applyChange(c, context)
    }

    private fun pushToUndo(change: HotspotChange) {
        undoStack.addLast(change)
        if (undoStack.size > maxDepth) undoStack.removeFirst()
        redoStack.clear()
        updateUndoRedoState()
    }

    fun undo(context: Context) = viewModelScope.launch {
        val change = undoStack.removeLastOrNull() ?: return@launch
        val inverse = change.inverse()
        when (inverse) {
            is HotspotChange.Create -> repository.saveHotspot(inverse.snapshot)
            is HotspotChange.Update -> repository.updateHotspot(inverse.after)
            is HotspotChange.Delete -> repository.deleteHotspot(inverse.snapshot)
            is HotspotChange.Bulk -> inverse.changes.forEach {
                when (it) {
                    is HotspotChange.Create -> repository.saveHotspot(it.snapshot)
                    is HotspotChange.Update -> repository.updateHotspot(it.after)
                    is HotspotChange.Delete -> repository.deleteHotspot(it.snapshot)
                    is HotspotChange.Bulk -> error("Nested bulk not expected")
                }
            }
        }
        redoStack.addLast(change)
        updateUndoRedoState()
        emitUiMessage(context.getString(R.string.undone))
    }

    fun redo(context: Context) = viewModelScope.launch {
        val change = redoStack.removeLastOrNull() ?: return@launch
        applyChange(change, context)
        updateUndoRedoState()
        emitUiMessage(context.getString(R.string.redone))
    }

    private fun updateUndoRedoState() {
        _canUndo.value = undoStack.isNotEmpty()
        _canRedo.value = redoStack.isNotEmpty()
    }


    // CRUD DIRECTO


    fun saveHotspot(h: HotspotEntity) = viewModelScope.launch {
        repository.saveHotspot(h)
    }

    fun deleteHotspot(h: HotspotEntity) = viewModelScope.launch {
        repository.deleteHotspot(h)
    }

    fun clearAll() = viewModelScope.launch {
        repository.clearAll()
    }

    // export - import JSON

    fun exportHotspots(context: Context) = viewModelScope.launch {
        val current = hotspots.value
        HotspotJsonHelper.exportToJson(context, current)
        emitUiMessage(context.getString(R.string.export_success))
    }

    fun importHotspots(context: Context) = viewModelScope.launch {
        val imported = HotspotJsonHelper.importFromJson(context)
        imported.forEach { repository.saveHotspot(it) }
        emitUiMessage(context.getString(R.string.import_success))
    }

    fun importFromUri(context: Context, uri: Uri) = viewModelScope.launch {
        val inputStream = context.contentResolver.openInputStream(uri)
        inputStream?.use { stream ->
            val jsonText = stream.bufferedReader().use { it.readText() }
            val list = Json.decodeFromString<
                    List<com.example.mgapp.data.local.serializer.HotspotJsonHelper.HotspotSerializable>
                    >(jsonText)
            list.forEach {
                repository.saveHotspot(
                    HotspotEntity(it.id, it.x, it.y, it.name, it.description)
                )
            }
        }
        emitUiMessage(context.getString(R.string.import_from_file))
    }

    // Form validation

    private val _formState: MutableState<FormUiState> = mutableStateOf(FormUiState())
    val formState: MutableState<FormUiState> = _formState


    private val currentSchema = HotspotSchema(
        hotspotId = "default",
        fields = listOf(
            FieldSchema(
                id = "name",
                labelRes = R.string.label_name,
                type = FieldType.TEXT,
                validation = ValidationRule(required = true, min = 3.0)
            ),
            FieldSchema(
                id = "description",
                labelRes = R.string.label_description,
                type = FieldType.TEXT,
                validation = ValidationRule(required = false)
            )
        )
    )

    fun initializeForm() {
        val initialValues = currentSchema.fields.associate { it.id to "" }
        val validation = validateForm(initialValues, currentSchema)
        val valid = validation.values.all { it.isEmpty() }

        _formState.value = FormUiState(
            values = initialValues,
            errors = validation,
            isValid = valid
        )
    }

    fun onFieldChange(fieldId: String, newValue: String?) {
        val newValues = _formState.value.values.toMutableMap().apply {
            put(fieldId, newValue)
        }

        val validation = validateForm(newValues, currentSchema)
        val valid = validation.values.all { it.isEmpty() }

        _formState.value = FormUiState(
            values = newValues,
            errors = validation,
            isValid = valid
        )
    }

    fun onSaveHotspot(context: Context) {
        val state = _formState.value
        val validation = validateForm(state.values, currentSchema)
        val valid = validation.values.all { it.isEmpty() }

        if (!valid) {
            _formState.value = state.copy(errors = validation, isValid = false)
            emitUiMessage(context.getString(R.string.not_saved_fix_fields))
            return
        }

        viewModelScope.launch {
            val name = state.values["name"].orEmpty()
            val description = state.values["description"].orEmpty()

            repository.saveHotspot(
                HotspotEntity(
                    id = 0,
                    x = 0f,
                    y = 0f,
                    name = name,
                    description = description
                )
            )
            emitUiMessage(context.getString(R.string.saved_success))
        }
    }

    data class FormUiState(
        val values: Map<String, String?> = emptyMap(),
        val errors: Map<String, List<ValidationError>> = emptyMap(),
        val isValid: Boolean = false
    )
}
