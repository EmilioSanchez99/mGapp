package com.example.mgapp.data.local.serializer

import android.content.Context
import android.net.Uri
import android.os.Environment
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
    fun exportToJson(context: Context, hotspots: List<HotspotEntity>): File {
        val list = hotspots.map {
            HotspotSerializable(it.id, it.x, it.y, it.name, it.description)
        }
        val jsonText = json.encodeToString(list)
        val downloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(downloads, FILE_NAME)

        file.writeText(jsonText)
        return file
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
