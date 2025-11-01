package com.example.mgapp

import com.example.mgapp.data.local.entity.HotspotEntity
import com.example.mgapp.domain.CompletionState
import com.example.mgapp.domain.getCompletionState
import org.junit.Assert.*
import org.junit.Test

class CompletionStateTest {

    @Test
    fun emptyHotspotDetected() {
        val hotspot = HotspotEntity(1, 0f, 0f, "", null)
        assertEquals(CompletionState.EMPTY, hotspot.getCompletionState())
    }

    @Test
    fun completeHotspotDetected() {
        val hotspot = HotspotEntity(1, 0f, 0f, "A", "B")
        assertEquals(CompletionState.COMPLETE, hotspot.getCompletionState())
    }
}
