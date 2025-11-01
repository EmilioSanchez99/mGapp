package com.example.mgapp.data.local.dao

import androidx.room.*
import com.example.mgapp.data.local.entity.HotspotEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HotspotDao {
    @Query("SELECT * FROM hotspots")
    fun getAll(): Flow<List<HotspotEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(h: HotspotEntity)

    @Update
    suspend fun update(h: HotspotEntity)

    @Delete
    suspend fun delete(h: HotspotEntity)

    @Query("SELECT * FROM hotspots WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): HotspotEntity?

    @Query("DELETE FROM hotspots")
    suspend fun deleteAll()

}
