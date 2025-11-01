package com.example.mgapp.ui.hotspot

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mgapp.data.local.entity.HotspotEntity
import com.example.mgapp.data.local.serializer.HotspotJsonHelper
import com.example.mgapp.data.repository.HotspotRepository
import com.example.mgapp.domain.HotspotChange
import com.example.mgapp.domain.inverse
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

@HiltViewModel
class HotspotViewModel @Inject constructor(
    private val repository: HotspotRepository
) : ViewModel() {

    // --- Estados de Undo/Redo ---
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

    // --- Flujo principal de hotspots ---
    val hotspots = repository.getAllHotspots()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // ===========================================================
    // 🔁 UNDO / REDO
    // ===========================================================

    fun applyChange(change: HotspotChange) = viewModelScope.launch {
        when (change) {
            is HotspotChange.Create -> repository.saveHotspot(change.snapshot)
            is HotspotChange.Update -> repository.updateHotspot(change.after)
            is HotspotChange.Delete -> repository.deleteHotspot(change.snapshot)
            is HotspotChange.Bulk -> applyBulk(change.changes)
        }
        pushToUndo(change)

        val message = when (change) {
            is HotspotChange.Create -> "Hotspot creado"
            is HotspotChange.Update -> "Hotspot actualizado"
            is HotspotChange.Delete -> "Hotspot eliminado"
            is HotspotChange.Bulk -> "Cambios aplicados"
        }
        emitUiMessage(message)
    }
    private suspend fun applyBulk(changes: List<HotspotChange>) {
        for (c in changes) applyChange(c)
    }

    private fun pushToUndo(change: HotspotChange) {
        undoStack.addLast(change)
        if (undoStack.size > maxDepth) undoStack.removeFirst()
        redoStack.clear()
        updateUndoRedoState()
    }

    fun undo() = viewModelScope.launch {
        val change = undoStack.removeLastOrNull() ?: return@launch
        val inverse = change.inverse()
        when (inverse) {
            is HotspotChange.Create -> repository.saveHotspot(inverse.snapshot)
            is HotspotChange.Update -> repository.updateHotspot(inverse.after)
            is HotspotChange.Delete -> repository.deleteHotspot(inverse.snapshot)
            is HotspotChange.Bulk   -> inverse.changes.forEach {
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
        emitUiMessage("Undone")
    }

    fun redo() = viewModelScope.launch {
        val change = redoStack.removeLastOrNull() ?: return@launch
        applyChange(change)
        updateUndoRedoState()
        emitUiMessage("Redone")
    }

    private fun updateUndoRedoState() {
        _canUndo.value = undoStack.isNotEmpty()
        _canRedo.value = redoStack.isNotEmpty()
    }

    // ===========================================================
    // 💾 CRUD DIRECTO
    // ===========================================================

    fun saveHotspot(h: HotspotEntity) = viewModelScope.launch {
        repository.saveHotspot(h)
    }

    fun deleteHotspot(h: HotspotEntity) = viewModelScope.launch {
        repository.deleteHotspot(h)
    }

    fun clearAll() = viewModelScope.launch {
        repository.clearAll()
    }

    // ===========================================================
    // 📤 EXPORTAR / 📥 IMPORTAR JSON
    // ===========================================================

    fun exportHotspots(context: Context) = viewModelScope.launch {
        val current = hotspots.value
        HotspotJsonHelper.exportToJson(context, current)
        emitUiMessage("Exported successfully")
    }

    fun importHotspots(context: Context) = viewModelScope.launch {
        val imported = HotspotJsonHelper.importFromJson(context)
        imported.forEach { repository.saveHotspot(it) }
        emitUiMessage("Imported successfully")
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
        emitUiMessage("Imported from file")
    }
}
