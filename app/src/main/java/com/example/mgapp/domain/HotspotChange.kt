package com.example.mgapp.domain

import com.example.mgapp.data.local.entity.HotspotEntity

sealed class HotspotChange {
    data class Create(val snapshot: HotspotEntity) : HotspotChange()
    data class Update(val before: HotspotEntity, val after: HotspotEntity) : HotspotChange()
    data class Delete(val snapshot: HotspotEntity) : HotspotChange()
    data class Bulk(val changes: List<HotspotChange>) : HotspotChange()
}
