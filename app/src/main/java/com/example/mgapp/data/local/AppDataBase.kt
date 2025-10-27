package com.example.mgapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.mgapp.data.local.dao.HotspotDao
import com.example.mgapp.data.local.entity.HotspotEntity

@Database(entities = [HotspotEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun hotspotDao(): HotspotDao
}
