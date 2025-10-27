package com.example.mgapp.ui.main

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import Hotspot
import com.example.mgapp.ui.components.HotspotBottomSheet
import com.example.mgapp.ui.components.SvgViewer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    var hotspots by remember { mutableStateOf<List<Hotspot>>(emptyList()) }
    var selectedHotspot by remember { mutableStateOf<Hotspot?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("mGapp SVG Editor") })
        }
    ) { padding ->
        SvgViewer(
            svgPath = "file:///android_asset/dekra.svg",
            hotspots = hotspots,
            onTap = { offset: Offset ->
                val newHotspot = Hotspot(
                    id = System.currentTimeMillis(),
                    x = offset.x,
                    y = offset.y
                )
                hotspots = hotspots + newHotspot
                selectedHotspot = newHotspot
            },
            onHotspotClick = { selectedHotspot = it }
        )

        selectedHotspot?.let { hotspot ->
            HotspotBottomSheet(
                hotspot = hotspot,
                onSave = { updated ->
                    hotspots = hotspots.map { if (it.id == updated.id) updated else it }
                },
                onDismiss = { selectedHotspot = null }
            )
        }
    }
}
