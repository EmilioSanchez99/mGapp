package com.example.mgapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.example.mgapp.domain.model.Hotspot
import kotlin.math.roundToInt

@Composable
fun SvgViewer(
    svgPath: String,
    hotspots: List<Hotspot>,
    onTap: (Offset) -> Unit,
    onHotspotClick: (Hotspot) -> Unit
) {
    var scale by remember { mutableStateOf(1f) }
    var translation by remember { mutableStateOf(Offset.Zero) }
    var containerSize by remember { mutableStateOf(androidx.compose.ui.geometry.Size.Zero) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            // Detectar tamaño del contenedor (pantalla)
            .onGloballyPositioned { coordinates ->
                val size = coordinates.size
                containerSize = androidx.compose.ui.geometry.Size(
                    width = size.width.toFloat(),
                    height = size.height.toFloat()
                )
            }
            // Detectar zoom/pan
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale = (scale * zoom).coerceIn(0.5f, 5f)
                    translation += pan
                }
            }
            // Detectar taps (coordenadas corregidas)
            .pointerInput(Unit) {
                detectTapGestures { tap ->
                    val center = Offset(containerSize.width / 2, containerSize.height / 2)
                    val corrected = Offset(
                        (tap.x - center.x - translation.x) / scale + center.x,
                        (tap.y - center.y - translation.y) / scale + center.y
                    )
                    onTap(corrected)
                }
            },
        contentAlignment = Alignment.Center
    ) {
        // Lienzo compartido (imagen + puntos)
        Box(
            modifier = Modifier
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = translation.x,
                    translationY = translation.y
                )
                .fillMaxSize()
        ) {
            // Imagen SVG
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(svgPath)
                    .decoderFactory(SvgDecoder.Factory())
                    .build(),
                contentDescription = "SVG",
                modifier = Modifier.fillMaxSize()
            )

            // Hotspots
            hotspots.forEach { h ->
                Box(
                    modifier = Modifier
                        .offset {
                            IntOffset(
                                h.x.roundToInt() - 10,
                                h.y.roundToInt() - 10
                            )
                        }
                        .size(20.dp)
                        .background(Color.Red, CircleShape)
                        .clickable { onHotspotClick(h) }
                )
            }
        }
    }
}
