package com.example.mgapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mgapp.R
import com.example.mgapp.data.local.entity.HotspotEntity
import com.example.mgapp.domain.CompletionState
import com.example.mgapp.domain.getCompletionState
import com.example.mgapp.ui.hotspot.HotspotViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotspotListScreen(
    viewModel: HotspotViewModel = hiltViewModel(),
    onEditHotspot: (HotspotEntity) -> Unit
) {
    val hotspots by viewModel.hotspots.collectAsState()
    var filter by remember { mutableStateOf<CompletionState?>(null) }

    val filtered = remember(hotspots, filter) {
        if (filter == null) hotspots
        else hotspots.filter { it.getCompletionState() == filter }
    }

    // ✅ Cargar strings de accesibilidad fuera de semantics{}
    val topBarDescription = stringResource(R.string.cd_title_hotspot_list)
    val listDescription = stringResource(R.string.cd_hotspot_list)
    val filterRowDescription = stringResource(R.string.cd_filter_row)
    val filterAllDesc = stringResource(R.string.cd_filter_all)
    val filterCompleteDesc = stringResource(R.string.cd_filter_complete)
    val filterPartialDesc = stringResource(R.string.cd_filter_partial)
    val filterEmptyDesc = stringResource(R.string.cd_filter_empty)
    val listContainerDescription = stringResource(R.string.cd_hotspot_list_container)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_hotspot_list)) },
                modifier = Modifier.semantics {
                    contentDescription = topBarDescription
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .semantics { contentDescription = listDescription }
        ) {
            // 🔹 Filtros
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { contentDescription = filterRowDescription },
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                FilterChip(
                    selected = filter == null,
                    onClick = { filter = null },
                    label = { Text(stringResource(R.string.filter_all)) },
                    modifier = Modifier.semantics { contentDescription = filterAllDesc }
                )
                FilterChip(
                    selected = filter == CompletionState.COMPLETE,
                    onClick = { filter = CompletionState.COMPLETE },
                    label = { Text(stringResource(R.string.filter_complete)) },
                    modifier = Modifier.semantics { contentDescription = filterCompleteDesc }
                )
                FilterChip(
                    selected = filter == CompletionState.PARTIAL,
                    onClick = { filter = CompletionState.PARTIAL },
                    label = { Text(stringResource(R.string.filter_partial)) },
                    modifier = Modifier.semantics { contentDescription = filterPartialDesc }
                )
                FilterChip(
                    selected = filter == CompletionState.EMPTY,
                    onClick = { filter = CompletionState.EMPTY },
                    label = { Text(stringResource(R.string.filter_empty)) },
                    modifier = Modifier.semantics { contentDescription = filterEmptyDesc }
                )
            }

            Spacer(Modifier.height(12.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.semantics {
                    contentDescription = listContainerDescription
                }
            ) {
                items(filtered) { hotspot ->
                    HotspotCard(hotspot, onEditHotspot)
                }
            }
        }
    }
}

@Composable
fun HotspotCard(
    hotspot: HotspotEntity,
    onEdit: (HotspotEntity) -> Unit
) {
    val state = hotspot.getCompletionState()
    val (icon, color, label) = when (state) {
        CompletionState.COMPLETE -> Triple(
            Icons.Default.CheckCircle,
            Color(0xFF4CAF50), // 🟢 Green
            stringResource(R.string.label_complete)
        )
        CompletionState.PARTIAL -> Triple(
            Icons.Default.Warning,
            Color(0xFFFFC107), // 🟡 Yellow
            stringResource(R.string.label_partial)
        )
        CompletionState.EMPTY -> Triple(
            Icons.Default.RadioButtonUnchecked,
            Color(0xFF9E9E9E), // ⚪ Grey
            stringResource(R.string.label_empty)
        )
    }

    val cardDescription = stringResource(
        R.string.cd_hotspot_card,
        hotspot.name.ifBlank { stringResource(R.string.label_no_name) },
        label
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEdit(hotspot) }
            .semantics { contentDescription = cardDescription },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Row(
            Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = hotspot.name.ifBlank { stringResource(R.string.label_no_name) },
                    style = MaterialTheme.typography.titleMedium
                )
                if (!hotspot.description.isNullOrBlank()) {
                    Text(
                        text = hotspot.description!!,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            // ✅ Icon color fix
            CompositionLocalProvider(LocalContentColor provides color) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = color
                )
            }
        }
    }
}

