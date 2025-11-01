package com.example.mgapp.ui.main

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mgapp.data.local.entity.HotspotEntity
import com.example.mgapp.domain.HotspotChange
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
    val canUndo by viewModel.canUndo.collectAsState()
    val canRedo by viewModel.canRedo.collectAsState()

    var pendingHotspot by remember { mutableStateOf<Hotspot?>(null) }
    var selectedHotspot by remember { mutableStateOf<Hotspot?>(null) }
    var showConfirmDialog by remember { mutableStateOf(false) }

    // ✅ Snackbar para mensajes tipo “Saved”, “Undone”, etc.
    val snackbarHostState = remember { SnackbarHostState() }

    // Escuchar el flujo de mensajes del ViewModel
    LaunchedEffect(Unit) {
        viewModel.uiMessage.collect { msg ->
            snackbarHostState.showSnackbar(message = msg)
        }
    }

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
    @Composable
    fun CustomSnackbar(data: SnackbarData) {
        val message = data.visuals.message.lowercase() // ✅ ahora sí está definida

        val (icon, tint) = when {
            "eliminado" in message -> Icons.Default.Delete to MaterialTheme.colorScheme.error
            "saved" in message -> Icons.Default.Check to MaterialTheme.colorScheme.primary
            "undo" in message -> Icons.AutoMirrored.Filled.Undo to MaterialTheme.colorScheme.tertiary
            "redo" in message -> Icons.AutoMirrored.Filled.Redo to MaterialTheme.colorScheme.secondary
            else -> Icons.Default.Info to MaterialTheme.colorScheme.outline
        }

        Surface(
            shape = RoundedCornerShape(50),
            tonalElevation = 3.dp,
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 3.dp,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = tint,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = data.visuals.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }


    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                CustomSnackbar(data)
            }
        },
        topBar = {
            TopAppBar(
                title = { Text("mGapp SVG Editor") },
                actions = {
                    IconButton(onClick = { viewModel.exportHotspots(context) }) {
                        Icon(Icons.Filled.Download, contentDescription = "Exportar JSON")
                    }
                    IconButton(onClick = {
                        importLauncher.launch(arrayOf("application/json"))
                    }) {
                        Icon(Icons.Filled.Upload, contentDescription = "Importar JSON")
                    }
                    IconButton(onClick = { showConfirmDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Borrar todo")
                    }
                    IconButton(onClick = { viewModel.undo() }, enabled = canUndo) {
                        Icon(Icons.AutoMirrored.Filled.Undo, contentDescription = "Undo")
                    }
                    IconButton(onClick = { viewModel.redo() }, enabled = canRedo) {
                        Icon(Icons.AutoMirrored.Filled.Redo, contentDescription = "Redo")
                    }
                }
            )
        }
    ) { padding ->
        // 🔹 Contenido SVG
        SvgViewer(
            svgPath = "file:///android_asset/dekra.svg",
            hotspots = hotspots,
            modifier = Modifier.padding(padding),

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
                    viewModel.applyChange(
                        HotspotChange.Create(
                            HotspotEntity(
                                id = updated.id,
                                x = updated.x,
                                y = updated.y,
                                name = updated.name,
                                description = updated.description
                            )
                        )
                    )
                    pendingHotspot = null
                }

                ,
                onDismiss = { pendingHotspot = null }
            )
        }

        // 🔹 Editar hotspot existente
        selectedHotspot?.let { hotspot ->
            HotspotBottomSheet(
                hotspot = hotspot,
                onSave = { updated ->
                    viewModel.applyChange(
                        HotspotChange.Update(
                            before = HotspotEntity(
                                id = hotspot.id,
                                x = hotspot.x,
                                y = hotspot.y,
                                name = hotspot.name,
                                description = hotspot.description
                            ),
                            after = HotspotEntity(
                                id = updated.id,
                                x = updated.x,
                                y = updated.y,
                                name = updated.name,
                                description = updated.description
                            )
                        )
                    )
                    selectedHotspot = null
                },
                onDelete = { toDelete ->
                    viewModel.applyChange(
                        HotspotChange.Delete(
                            HotspotEntity(
                                id = toDelete.id,
                                x = toDelete.x,
                                y = toDelete.y,
                                name = toDelete.name,
                                description = toDelete.description
                            )
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
