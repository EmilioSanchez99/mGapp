package com.example.mgapp.data.local.dao

import androidx.room.*
import com.example.mgapp.data.local.entity.HotspotEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HotspotDao {
    @Query("SELECT * FROM hotspots")
    fun getAll(): Flow<List<HotspotEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(hotspot: HotspotEntity)

    @Delete
    suspend fun delete(hotspot: HotspotEntity)

    @Query("DELETE FROM hotspots")
    suspend fun deleteAll()
}
