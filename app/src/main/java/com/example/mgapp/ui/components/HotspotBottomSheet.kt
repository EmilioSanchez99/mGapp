package com.example.mgapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mgapp.R
import com.example.mgapp.domain.model.Hotspot
import com.example.mgapp.ui.hotspot.HotspotViewModel
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotspotBottomSheet(
    hotspot: Hotspot,
    onSave: (Hotspot) -> Unit,
    onDelete: ((Hotspot) -> Unit)? = null,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    val viewModel: HotspotViewModel = hiltViewModel()

    var name by remember { mutableStateOf(hotspot.name) }
    var description by remember { mutableStateOf(hotspot.description ?: "") }

    // ⚙️ Inicializa el formulario una vez al abrir
    LaunchedEffect(Unit) {
        viewModel.initializeForm()
        // Actualiza valores iniciales si el hotspot ya tiene datos
        if (hotspot.name.isNotEmpty()) {
            viewModel.onFieldChange("name", hotspot.name)
        }
        if (!hotspot.description.isNullOrEmpty()) {
            viewModel.onFieldChange("description", hotspot.description)
        }
    }

    val formState = viewModel.formState.value

    ModalBottomSheet(
        onDismissRequest = { onDismiss() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                text = if (hotspot.name.isEmpty())
                    stringResource(R.string.new_hotspot)
                else
                    stringResource(R.string.edit_hotspot),
                style = MaterialTheme.typography.titleLarge
            )


            Spacer(Modifier.height(16.dp))

            // 🔹 Campo nombre
            OutlinedTextField(
                value = name,
                singleLine = true,
                maxLines = 1,
                onValueChange = {
                    name = it
                    viewModel.onFieldChange("name", it)
                },
                label = { Text(stringResource(R.string.label_name)) },
                isError = formState.errors["name"]?.isNotEmpty() == true,
                supportingText = {
                    val err = formState.errors["name"]?.firstOrNull()
                    if (err != null) Text(stringResource(err.messageRes))
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            // 🔹 Campo descripción
            OutlinedTextField(
                value = description,
                singleLine = true,
                maxLines = 1,
                onValueChange = {
                    description = it
                    viewModel.onFieldChange("description", it)
                },
                label = { Text(stringResource(R.string.label_description)) },
                isError = formState.errors["description"]?.isNotEmpty() == true,
                supportingText = {
                    val err = formState.errors["description"]?.firstOrNull()
                    if (err != null) Text(stringResource(err.messageRes))
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(24.dp))

            // 🔹 Botones principales (Delete / Cancel / Save)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 🔸 Botón eliminar (solo icono)
                if (hotspot.name.isNotEmpty() || !hotspot.description.isNullOrEmpty()) {
                    IconButton(
                        onClick = {
                            onDelete?.invoke(hotspot)
                            onDismiss()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(R.string.delete_hotspot),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                } else {
                    // mantiene alineación aunque no haya icono
                    Spacer(Modifier.size(48.dp))
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(R.string.cancel))
                    }

                    Spacer(Modifier.width(8.dp))

                    Button(
                        onClick = {
                            viewModel.onSaveHotspot(context)
                            onSave(
                                hotspot.copy(
                                    name = name,
                                    description = description.ifBlank { null }
                                )
                            )
                            onDismiss()
                        },
                        enabled = formState.isValid
                    ) {
                        Text(stringResource(R.string.save))
                    }
                }
            }


        }
    }
}
@Composable
fun HotspotBottomSheetPreviewable(
    hotspotName: String = "Test Name",
    hotspotDescription: String = "Test Description",
    onDismiss: () -> Unit = {},
    onSave: () -> Unit = {}
) {
    Surface {
        Column {
            Text("Name")
            Text(hotspotName)
            Text("Description")
            Text(hotspotDescription)
            Button(onClick = onSave) { Text("Save") }
            Button(onClick = onDismiss) { Text("Cancel") }
        }
    }
}


