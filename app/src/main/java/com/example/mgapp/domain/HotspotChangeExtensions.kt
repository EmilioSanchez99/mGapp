package com.example.mgapp.domain

class HotspotChangeExtensions {
    fun HotspotChange.inverse(): HotspotChange = when (this) {
        is HotspotChange.Create -> HotspotChange.Delete(snapshot)
        is HotspotChange.Update -> HotspotChange.Update(after, before)
        is HotspotChange.Delete -> HotspotChange.Create(snapshot)
        is HotspotChange.Bulk   -> HotspotChange.Bulk(changes.asReversed().map { it.inverse() })
    }

}