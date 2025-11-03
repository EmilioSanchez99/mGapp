package com.example.mgapp.ui.main

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mgapp.R
import com.example.mgapp.data.local.entity.HotspotEntity
import com.example.mgapp.domain.HotspotChange
import com.example.mgapp.domain.model.Hotspot
import com.example.mgapp.ui.components.HotspotBottomSheet
import com.example.mgapp.ui.components.SvgViewer
import com.example.mgapp.ui.hotspot.HotspotViewModel
import com.example.mgapp.ui.screens.HotspotListScreen

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
    var selectedTab by rememberSaveable { mutableStateOf(0) } // 0 = Mapa, 1 = Lista

    val snackbarHostState = remember { SnackbarHostState() }

    // Escuchar mensajes del ViewModel (Saved, Deleted, etc.)
    LaunchedEffect(Unit) {
        viewModel.uiMessage.collect { msg ->
            snackbarHostState.showSnackbar(message = msg)
        }
    }

    // 🔹 Launcher para importar JSON
    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri: Uri? ->
            if (uri != null) {
                viewModel.importFromUri(context, uri)
                Toast.makeText(context, context.getString(R.string.toast_import_done), Toast.LENGTH_SHORT).show()
            }
        }
    )

    // 🔹 Snackbar personalizado
    // 🔹 Snackbar personalizado accesible
    @Composable
    fun CustomSnackbar(data: SnackbarData) {
        val message = data.visuals.message.lowercase()
        val (icon, tint, cd) = when {
            "eliminado" in message -> Triple(Icons.Default.Delete, MaterialTheme.colorScheme.error, stringResource(R.string.cd_snackbar_deleted))
            "saved" in message -> Triple(Icons.Default.Check, MaterialTheme.colorScheme.primary, stringResource(R.string.cd_snackbar_saved))
            "undo" in message -> Triple(Icons.AutoMirrored.Filled.Undo, MaterialTheme.colorScheme.tertiary, stringResource(R.string.cd_snackbar_undo))
            "redo" in message -> Triple(Icons.AutoMirrored.Filled.Redo, MaterialTheme.colorScheme.secondary, stringResource(R.string.cd_snackbar_redo))
            else -> Triple(Icons.Default.Info, MaterialTheme.colorScheme.outline, stringResource(R.string.cd_snackbar_info))
        }

        Surface(
            shape = RoundedCornerShape(50),
            tonalElevation = 3.dp,
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 3.dp,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = cd,
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


    // ===========================================================
    // 🔹 Estructura principal con pestañas
    // ===========================================================
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                CustomSnackbar(data)
            }
        },
        topBar = {
            // ===========================================================
// 🔹 Toolbar con contentDescription accesibles
// ===========================================================
            TopAppBar(
                title = { Text(stringResource(R.string.title_main_screen)) },
                actions = {
                    IconButton(
                        onClick = { viewModel.exportHotspots(context) }
                    ) {
                        Icon(
                            Icons.Filled.Download,
                            contentDescription = stringResource(R.string.cd_export_json)
                        )
                    }
                    IconButton(
                        onClick = { importLauncher.launch(arrayOf("application/json")) }
                    ) {
                        Icon(
                            Icons.Filled.Upload,
                            contentDescription = stringResource(R.string.cd_import_json)
                        )
                    }
                    IconButton(onClick = { showConfirmDialog = true }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = stringResource(R.string.cd_delete_all)
                        )
                    }
                    IconButton(
                        onClick = { viewModel.undo(context) },
                        enabled = canUndo
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Undo,
                            contentDescription = stringResource(R.string.cd_undo)
                        )
                    }
                    IconButton(
                        onClick = { viewModel.redo(context) },
                        enabled = canRedo
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Redo,
                            contentDescription = stringResource(R.string.cd_redo)
                        )
                    }
                }
            )

        }
    ) { padding ->

        Column(modifier = Modifier.padding(padding)) {
            // 🔸 Pestañas superiores
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text(stringResource(R.string.tab_svg_map)) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text(stringResource(R.string.tab_hotspot_list)) }
                )
            }

            when (selectedTab) {
                0 -> {
                    // 🗺️ SVG Viewer
                    SvgViewer(
                        svgPath = "file:///android_asset/dekra.svg",
                        hotspots = hotspots,
                        modifier = Modifier.weight(1f),
                        onTap = { offset ->
                            pendingHotspot = Hotspot(
                                id = System.currentTimeMillis(),
                                x = offset.x,
                                y = offset.y
                            )
                        },
                        onHotspotClick = { selectedHotspot = it }
                    )
                }

                1 -> {
                    // 📋 Lista general
                    HotspotListScreen(
                        viewModel = viewModel,
                        onEditHotspot = { hotspotEntity ->
                            selectedHotspot = Hotspot(
                                id = hotspotEntity.id,
                                x = hotspotEntity.x,
                                y = hotspotEntity.y,
                                name = hotspotEntity.name,
                                description = hotspotEntity.description
                            )
                        }
                    )
                }
            }
        }

        // ===========================================================
        // 🔹 BottomSheets
        // ===========================================================
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
                        ),
                        context
                    )
                    pendingHotspot = null
                },
                onDismiss = { pendingHotspot = null }
            )
        }

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
                        ),
                        context
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
                        ),
                        context
                    )
                    selectedHotspot = null
                },
                onDismiss = { selectedHotspot = null }
            )
        }

        // ===========================================================
        // 🔹 Diálogo de confirmación (borrar todo)
        // ===========================================================
        if (showConfirmDialog) {
            if (hotspots.isEmpty()) {
                Toast.makeText(context, stringResource(R.string.toast_no_hotspots), Toast.LENGTH_SHORT).show()
                showConfirmDialog = false
            } else {
                AlertDialog(
                    onDismissRequest = { showConfirmDialog = false },
                    shape = RoundedCornerShape(20.dp),
                    tonalElevation = 8.dp,
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = stringResource(R.string.cd_dialog_delete_icon),
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                    ,
                    title = { Text(stringResource(R.string.dialog_title_confirm_delete), style = MaterialTheme.typography.titleLarge) },
                    text = {
                        Text(
                            stringResource(R.string.dialog_message_confirm_delete),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                viewModel.clearAll()
                                showConfirmDialog = false
                                Toast.makeText(context, context.getString(R.string.toast_all_deleted), Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text(stringResource(R.string.dialog_confirm_button), color = MaterialTheme.colorScheme.onError)
                        }
                    },
                    dismissButton = {
                        OutlinedButton(onClick = { showConfirmDialog = false }) {
                            Text(stringResource(R.string.dialog_cancel_button))
                        }
                    }
                )
            }
        }
    }
}
