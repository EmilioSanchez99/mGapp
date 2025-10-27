package com.example.mgapp.ui.components

import Hotspot
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import kotlin.math.roundToInt

@Composable
fun SvgViewer(
    svgPath: String,
    hotspots: List<Hotspot>,
    onTap: (Offset) -> Unit,
    onHotspotClick: (Hotspot) -> Unit
) {
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val density = LocalDensity.current
    val radiusPx = with(density) { 10.dp.toPx() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            // Zoom y pan
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale = (scale * zoom).coerceIn(1f, 5f)
                    offset += pan
                }
            }
            // Tap para crear hotspot
            .pointerInput(Unit) {
                detectTapGestures { tap ->
                    val correctedTap = Offset(
                        (tap.x - offset.x) / scale,
                        (tap.y - offset.y) / scale
                    )
                    onTap(correctedTap)
                }
            }
    ) {
        // Renderizar el SVG
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(svgPath)
                .decoderFactory(SvgDecoder.Factory())
                .build(),
            contentDescription = null,
            modifier = Modifier.graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                translationX = offset.x,
                translationY = offset.y
            )
        )

        // Dibujar hotspots
        hotspots.forEach { h ->
            Box(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            ((h.x * scale) + offset.x - radiusPx).roundToInt(),
                            ((h.y * scale) + offset.y - radiusPx).roundToInt()
                        )
                    }
                    .size(20.dp)
                    .background(Color.Red, shape = CircleShape)
                    .clickable { onHotspotClick(h) }
            )
        }
    }
}
