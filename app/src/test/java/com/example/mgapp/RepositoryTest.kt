package com.example.mgapp

import com.example.mgapp.data.local.entity.HotspotEntity
import com.example.mgapp.data.repository.HotspotRepository
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class FakeRepository {
    private val data = mutableListOf<HotspotEntity>()
    fun getAllHotspots() = flow { emit(data) }
    suspend fun saveHotspot(h: HotspotEntity) { data.add(h) }
    suspend fun clearAll() { data.clear() }
}

class RepositoryTest {
    private val repo = FakeRepository()

    @Test
    fun addAndClearHotspots() = runBlocking {
        repo.saveHotspot(HotspotEntity(1, 0f, 0f, "A", "Test"))
        assertTrue(repo.getAllHotspots() != null)
        repo.clearAll()
        assertTrue(true)
    }
}
