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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.example.mgapp.R
import com.example.mgapp.domain.CompletionState
import com.example.mgapp.domain.model.Hotspot
import kotlin.math.roundToInt

@Composable
fun SvgViewer(
    svgPath: String,
    hotspots: List<Hotspot>,
    modifier: Modifier = Modifier,
    onTap: (Offset) -> Unit,
    onHotspotClick: (Hotspot) -> Unit
) {
    var scale by remember { mutableStateOf(1f) }
    var translation by remember { mutableStateOf(Offset.Zero) }
    var containerSize by remember { mutableStateOf(androidx.compose.ui.geometry.Size.Zero) }

    Box(
        modifier = modifier
            .fillMaxSize()
            // Detect container size
            .onGloballyPositioned { coordinates ->
                val size = coordinates.size
                containerSize = androidx.compose.ui.geometry.Size(
                    width = size.width.toFloat(),
                    height = size.height.toFloat()
                )
            }
            // Detect zoom & pan
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale = (scale * zoom).coerceIn(0.5f, 5f)
                    translation += pan
                }
            }
            // Detect taps safely (avoid phantom hotspot)
            .pointerInput(Unit) {
                detectTapGestures { tap ->
                    // Ensure layout is measured before allowing hotspot creation
                    if (containerSize.width > 0 && containerSize.height > 0) {
                        val center = Offset(containerSize.width / 2, containerSize.height / 2)
                        val corrected = Offset(
                            (tap.x - center.x - translation.x) / scale + center.x,
                            (tap.y - center.y - translation.y) / scale + center.y
                        )

                        //  Avoid creating hotspot near (0,0) or invalid coordinates
                        if (corrected.x > 10f && corrected.y > 10f) {
                            onTap(corrected)
                        }
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        // Shared layer (SVG + hotspots)
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
            // SVG image
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(svgPath)
                    .decoderFactory(SvgDecoder.Factory())
                    .build(),
                contentDescription = stringResource(R.string.svg_content_description),
                modifier = Modifier
                    .fillMaxSize()
                    .semantics {
                        contentDescription = "Interactive SVG map with tappable zones"
                    }
            )

            //  Dynamic Hotspots colored by completion state
            hotspots.forEach { h ->
                val color = when (h.getCompletionState()) {
                    CompletionState.COMPLETE -> Color(0xFF4CAF50) // 🟢 Green
                    CompletionState.PARTIAL -> Color(0xFFFFC107)  // 🟡 Yellow
                    CompletionState.EMPTY -> Color(0xFF9E9E9E)    // ⚪ Grey
                }

                // Only draw valid hotspots (avoid 0,0 coordinates)
                if (h.x > 0 && h.y > 0) {
                    Box(
                        modifier = Modifier
                            .offset {
                                IntOffset(
                                    h.x.roundToInt() - 10,
                                    h.y.roundToInt() - 10
                                )
                            }
                            .size(20.dp)
                            .background(color, CircleShape)
                            .clickable { onHotspotClick(h) }
                    )
                }
            }
        }
    }
}


fun Hotspot.getCompletionState(): CompletionState {
    val hasName = !name.isNullOrBlank()
    val hasDescription = !description.isNullOrBlank()

    return when {
        hasName && hasDescription -> CompletionState.COMPLETE
        hasName || hasDescription -> CompletionState.PARTIAL
        else -> CompletionState.EMPTY
    }
}
