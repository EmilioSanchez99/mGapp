package com.example.mgapp.domain

import com.example.mgapp.data.local.entity.HotspotEntity

enum class CompletionState { COMPLETE, PARTIAL, EMPTY }

fun HotspotEntity.getCompletionState(): CompletionState {
    val hasName = !name.isNullOrBlank()
    val hasDescription = !description.isNullOrBlank()

    return when {
        hasName && hasDescription -> CompletionState.COMPLETE
        hasName || hasDescription -> CompletionState.PARTIAL
        else -> CompletionState.EMPTY
    }
}
