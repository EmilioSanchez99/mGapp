package com.example.mgapp.domain.model


data class Hotspot(
    val id: Long,
    val x: Float,
    val y: Float,
    val name: String = "",
    val description: String? = null
)
