package com.example.mgapp.ui.main

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mgapp.data.local.entity.HotspotEntity
import com.example.mgapp.domain.model.Hotspot
import com.example.mgapp.ui.components.HotspotBottomSheet
import com.example.mgapp.ui.components.SvgViewer
import com.example.mgapp.ui.hotspot.HotspotViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: HotspotViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val hotspotsFlow = viewModel.hotspots.collectAsState()
    val hotspots = hotspotsFlow.value.map {
        Hotspot(it.id, it.x, it.y, it.name, it.description)
    }

    var pendingHotspot by remember { mutableStateOf<Hotspot?>(null) }
    var selectedHotspot by remember { mutableStateOf<Hotspot?>(null) }
    var showConfirmDialog by remember { mutableStateOf(false) }

    // 🔹 Launcher para elegir archivo JSON manualmente
    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri: Uri? ->
            if (uri != null) {
                viewModel.importFromUri(context, uri)
                Toast.makeText(context, "✅ Importación completada", Toast.LENGTH_SHORT).show()
            }
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("mGapp SVG Editor") },
                actions = {
                    // Exportar JSON
                    IconButton(onClick = { viewModel.exportHotspots(context) }) {
                        Icon(Icons.Filled.Download, contentDescription = "Exportar JSON")
                    }
                    // Importar JSON
                    IconButton(onClick = {
                        importLauncher.launch(arrayOf("application/json"))
                    }) {
                        Icon(Icons.Filled.Upload, contentDescription = "Importar JSON")
                    }
                    // Borrar todo (muestra diálogo)
                    IconButton(onClick = { showConfirmDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Borrar todo")
                    }
                }
            )
        }
    ) { padding ->
        // 🔹 Mapa interactivo SVG
        SvgViewer(
            svgPath = "file:///android_asset/dekra.svg",
            hotspots = hotspots,
            onTap = { offset ->
                pendingHotspot = Hotspot(
                    id = System.currentTimeMillis(),
                    x = offset.x,
                    y = offset.y
                )
            },
            onHotspotClick = { selectedHotspot = it }
        )

        // 🔹 Crear nuevo hotspot (BottomSheet)
        pendingHotspot?.let { hotspot ->
            HotspotBottomSheet(
                hotspot = hotspot,
                onSave = { updated ->
                    viewModel.saveHotspot(
                        HotspotEntity(
                            id = updated.id,
                            x = updated.x,
                            y = updated.y,
                            name = updated.name,
                            description = updated.description
                        )
                    )
                    pendingHotspot = null
                },
                onDismiss = { pendingHotspot = null }
            )
        }

        // 🔹 Editar hotspot existente
        selectedHotspot?.let { hotspot ->
            HotspotBottomSheet(
                hotspot = hotspot,
                onSave = { updated ->
                    viewModel.saveHotspot(
                        HotspotEntity(
                            id = updated.id,
                            x = updated.x,
                            y = updated.y,
                            name = updated.name,
                            description = updated.description
                        )
                    )
                    selectedHotspot = null
                },
                onDismiss = { selectedHotspot = null }
            )
        }

        // 🔹 Diálogo de confirmación antes de borrar todo
        if (showConfirmDialog) {
            if (hotspots.isEmpty()) {
                Toast.makeText(context, "No hay hotspots para borrar", Toast.LENGTH_SHORT).show()
                showConfirmDialog = false
            } else {
                AlertDialog(
                    onDismissRequest = { showConfirmDialog = false },
                    shape = RoundedCornerShape(20.dp),
                    tonalElevation = 8.dp,
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(48.dp)
                        )
                    },
                    title = {
                        Text(
                            text = "Confirmar eliminación",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    text = {
                        Text(
                            text = "¿Seguro que quieres borrar todos los hotspots guardados? Esta acción no se puede deshacer.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                viewModel.clearAll()
                                showConfirmDialog = false
                                Toast.makeText(context, "🗑️ Todos los hotspots fueron eliminados", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text("Borrar todo", color = MaterialTheme.colorScheme.onError)
                        }
                    },
                    dismissButton = {
                        OutlinedButton(
                            onClick = { showConfirmDialog = false }
                        ) {
                            Text("Cancelar")
                        }
                    }
                )
            }
        }

    }
}
