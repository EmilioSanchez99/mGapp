package com.example.mgapp

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.mgapp.data.local.entity.HotspotEntity
import com.example.mgapp.data.local.serializer.HotspotJsonHelper
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class JsonHelperTest {

    private val context = ApplicationProvider.getApplicationContext<Context>()

    @Test
    fun exportAndImportHotspots() = runBlocking {
        val list = listOf(HotspotEntity(1, 10f, 10f, "Point", "Test"))
        HotspotJsonHelper.exportToJson(context, list)
        val imported = HotspotJsonHelper.importFromJson(context)
        assertEquals(list.first().name, imported.first().name)
    }
}
