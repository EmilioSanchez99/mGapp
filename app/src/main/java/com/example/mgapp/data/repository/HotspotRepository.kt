package com.example.mgapp.data.repository

import com.example.mgapp.data.local.dao.HotspotDao
import com.example.mgapp.data.local.entity.HotspotEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HotspotRepository @Inject constructor(
    private val dao: HotspotDao
) {
    fun getAllHotspots(): Flow<List<HotspotEntity>> = dao.getAll()
    suspend fun saveHotspot(hotspot: HotspotEntity) = dao.insert(hotspot)
    suspend fun deleteHotspot(hotspot: HotspotEntity) = dao.delete(hotspot)
    suspend fun clearAll() = dao.deleteAll()
}
