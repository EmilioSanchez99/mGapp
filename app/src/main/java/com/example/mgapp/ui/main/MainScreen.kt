@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.mgapp.ui.main

import Hotspot
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import com.example.mgapp.ui.components.SvgViewer
@Composable
fun MainScreen() {
    var hotspots by remember { mutableStateOf<List<Hotspot>>(emptyList()) }
    var selected by remember { mutableStateOf<Hotspot?>(null) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("mGapp SVG Editor") }) }
    ) { padding ->
        Box(Modifier.padding(padding)) {
            SvgViewer(
                svgPath = "file:///android_asset/dekra.svg",
                hotspots = hotspots,
                onTap = { offset ->
                    val newHotspot = Hotspot(
                        id = System.currentTimeMillis(),
                        x = offset.x,
                        y = offset.y
                    )
                    hotspots = hotspots + newHotspot
                    selected = newHotspot
                },
                onHotspotClick = { selected = it }
            )

            selected?.let { hotspot ->
                AlertDialog(
                    onDismissRequest = { selected = null },
                    confirmButton = {
                        TextButton(onClick = { selected = null }) { Text("Cerrar") }
                    },
                    title = { Text("Hotspot creado") },
                    text = { Text("x=${hotspot.x}, y=${hotspot.y}") }
                )
            }
        }
    }
}


