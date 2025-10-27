package com.example.mgapp.ui.hotspot

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mgapp.data.local.entity.HotspotEntity
import com.example.mgapp.data.local.serializer.HotspotJsonHelper
import com.example.mgapp.data.repository.HotspotRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HotspotViewModel @Inject constructor(
    private val repository: HotspotRepository
) : ViewModel() {

    val hotspots = repository.getAllHotspots()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun saveHotspot(h: HotspotEntity) = viewModelScope.launch {
        repository.saveHotspot(h)
    }

    fun deleteHotspot(h: HotspotEntity) = viewModelScope.launch {
        repository.deleteHotspot(h)
    }

    fun clearAll() = viewModelScope.launch {
        repository.clearAll()
    }

    // 🔹 Exportar a JSON
    fun exportHotspots(context: Context) = viewModelScope.launch {
        val current = hotspots.value
        HotspotJsonHelper.exportToJson(context, current)
    }

    // 🔹 Importar desde JSON
    fun importHotspots(context: Context) = viewModelScope.launch {
        val imported = HotspotJsonHelper.importFromJson(context)
        imported.forEach { repository.saveHotspot(it) }
    }

    fun importFromUri(context: Context, uri: Uri) = viewModelScope.launch {
        val contentResolver = context.contentResolver
        val inputStream = contentResolver.openInputStream(uri)
        inputStream?.use { stream ->
            val jsonText = stream.bufferedReader().use { it.readText() }
            val list = kotlinx.serialization.json.Json.decodeFromString<
                    List<com.example.mgapp.data.local.serializer.HotspotJsonHelper.HotspotSerializable>
                    >(jsonText)
            list.forEach {
                repository.saveHotspot(
                    HotspotEntity(it.id, it.x, it.y, it.name, it.description)
                )
            }
        }
    }

}
