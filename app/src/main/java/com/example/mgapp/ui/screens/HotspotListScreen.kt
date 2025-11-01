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
import androidx.compose.ui.res.stringResource
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_hotspot_list)) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // 🔹 Filtros
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                FilterChip(
                    selected = filter == null,
                    onClick = { filter = null },
                    label = { Text(stringResource(R.string.filter_all)) }
                )
                FilterChip(
                    selected = filter == CompletionState.COMPLETE,
                    onClick = { filter = CompletionState.COMPLETE },
                    label = { Text(stringResource(R.string.filter_complete)) }
                )
                FilterChip(
                    selected = filter == CompletionState.PARTIAL,
                    onClick = { filter = CompletionState.PARTIAL },
                    label = { Text(stringResource(R.string.filter_partial)) }
                )
                FilterChip(
                    selected = filter == CompletionState.EMPTY,
                    onClick = { filter = CompletionState.EMPTY },
                    label = { Text(stringResource(R.string.filter_empty)) }
                )
            }

            Spacer(Modifier.height(12.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
        CompletionState.COMPLETE -> Triple(Icons.Default.CheckCircle, MaterialTheme.colorScheme.primary, stringResource(R.string.label_complete))
        CompletionState.PARTIAL -> Triple(Icons.Default.Warning, MaterialTheme.colorScheme.tertiary, stringResource(R.string.label_partial))
        CompletionState.EMPTY -> Triple(Icons.Default.RadioButtonUnchecked, MaterialTheme.colorScheme.outline, stringResource(R.string.label_empty))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEdit(hotspot) },
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
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color
            )
        }
    }
}
