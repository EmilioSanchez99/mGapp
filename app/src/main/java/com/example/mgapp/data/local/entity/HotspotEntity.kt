package com.example.mgapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hotspots")
data class HotspotEntity(
    @PrimaryKey val id: Long,
    val x: Float,
    val y: Float,
    val name: String,
    val description: String?

)

