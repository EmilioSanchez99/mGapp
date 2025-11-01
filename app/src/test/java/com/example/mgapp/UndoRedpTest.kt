package com.example.mgapp

import com.example.mgapp.data.local.entity.HotspotEntity
import com.example.mgapp.domain.HotspotChange
import com.example.mgapp.domain.inverse
import org.junit.Assert.*
import org.junit.Test

class UndoRedoTest {

    @Test
    fun inverseCreateBecomesDelete() {
        val entity = HotspotEntity(1, 0f, 0f, "A", "Test")
        val change = HotspotChange.Create(entity)
        val inverse = change.inverse()
        assertTrue(inverse is HotspotChange.Delete)
    }

    @Test
    fun inverseDeleteBecomesCreate() {
        val entity = HotspotEntity(1, 0f, 0f, "A", "Test")
        val change = HotspotChange.Delete(entity)
        val inverse = change.inverse()
        assertTrue(inverse is HotspotChange.Create)
    }
}
