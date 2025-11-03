package com.example.mgapp.data.local.serializer

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import com.example.mgapp.data.local.entity.HotspotEntity
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File

object HotspotJsonHelper {

    private const val FILE_NAME = "hotspots.json"

    @Serializable
    data class HotspotSerializable(
        val id: Long,
        val x: Float,
        val y: Float,
        val name: String,
        val description: String?
    )

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    // 🔹 Exportar todos los hotspots a un archivo JSON
    fun exportToJson(context: Context, hotspots: List<HotspotEntity>) {
        val list = hotspots.map {
            HotspotSerializable(it.id, it.x, it.y, it.name, it.description)
        }
        val jsonText = json.encodeToString(list)

        val values = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, "hotspots.json")
            put(MediaStore.Downloads.MIME_TYPE, "application/json")
            put(MediaStore.Downloads.IS_PENDING, 1)
        }

        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)

        uri?.let {
            resolver.openOutputStream(it)?.use { output ->
                output.write(jsonText.toByteArray())
            }

            values.clear()
            values.put(MediaStore.Downloads.IS_PENDING, 0)
            resolver.update(uri, values, null, null)

            Toast.makeText(context, "Exported to Downloads ✅", Toast.LENGTH_SHORT).show()
        } ?: Toast.makeText(context, "Export failed ❌", Toast.LENGTH_SHORT).show()
    }


    // 🔹 Importar hotspots desde JSON
    fun importFromJson(context: Context): List<HotspotEntity> {
        val file = File(context.getExternalFilesDir(null), FILE_NAME)
        if (!file.exists()) return emptyList()

        val jsonText = file.readText()
        val list = json.decodeFromString<List<HotspotSerializable>>(jsonText)
        return list.map {
            HotspotEntity(it.id, it.x, it.y, it.name, it.description)
        }
    }


}
