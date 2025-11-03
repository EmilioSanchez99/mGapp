package com.example.mgapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.focusable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mgapp.R
import com.example.mgapp.domain.model.Hotspot
import com.example.mgapp.ui.hotspot.HotspotViewModel

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

    LaunchedEffect(Unit) {
        viewModel.initializeForm()
        if (hotspot.name.isNotEmpty()) viewModel.onFieldChange("name", hotspot.name)
        if (!hotspot.description.isNullOrEmpty()) viewModel.onFieldChange("description", hotspot.description)
    }

    val formState = viewModel.formState.value


    val sheetDescription = if (hotspot.name.isEmpty())
        stringResource(R.string.cd_bottomsheet_create)
    else
        stringResource(R.string.cd_bottomsheet_edit, hotspot.name)

    val titleDescription = if (hotspot.name.isEmpty())
        stringResource(R.string.cd_title_new_hotspot)
    else
        stringResource(R.string.cd_title_edit_hotspot, hotspot.name)

    val fieldNameDescription = stringResource(R.string.cd_field_name)
    val fieldDescriptionDescription = stringResource(R.string.cd_field_description)
    val deleteButtonDescription = stringResource(R.string.cd_button_delete_hotspot)
    val cancelButtonDescription = stringResource(R.string.cd_button_cancel)
    val saveButtonDescription = stringResource(R.string.cd_button_save_hotspot)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = Modifier.semantics {
            contentDescription = sheetDescription
        }
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
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .semantics { contentDescription = titleDescription }
                    .focusable()
            )

            Spacer(Modifier.height(16.dp))

            // name field
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
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { contentDescription = fieldNameDescription }
                    .focusable()
            )

            Spacer(Modifier.height(8.dp))

            // description field
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
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { contentDescription = fieldDescriptionDescription }
                    .focusable()
            )

            Spacer(Modifier.height(24.dp))

            // Buttons (Delete / Cancel / Save)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (hotspot.name.isNotEmpty() || !hotspot.description.isNullOrEmpty()) {
                    IconButton(
                        onClick = {
                            onDelete?.invoke(hotspot)
                            onDismiss()
                        },
                        modifier = Modifier
                            .semantics { contentDescription = deleteButtonDescription }
                            .focusable()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                } else {
                    Spacer(Modifier.size(48.dp))
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .semantics { contentDescription = cancelButtonDescription }
                            .focusable()
                    ) {
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
                        enabled = formState.isValid,
                        modifier = Modifier
                            .semantics { contentDescription = saveButtonDescription }
                            .focusable()
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
    val previewDescription = stringResource(R.string.cd_preview_hotspot)
    val savePreviewDescription = stringResource(R.string.cd_button_preview_save)
    val cancelPreviewDescription = stringResource(R.string.cd_button_preview_cancel)

    Surface {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .semantics { contentDescription = previewDescription }
        ) {
            Text(stringResource(R.string.label_name))
            Text(hotspotName)
            Text(stringResource(R.string.label_description))
            Text(hotspotDescription)

            Button(
                onClick = onSave,
                modifier = Modifier
                    .semantics { contentDescription = savePreviewDescription }
            ) { Text(stringResource(R.string.save)) }

            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .semantics { contentDescription = cancelPreviewDescription }
            ) { Text(stringResource(R.string.cancel)) }
        }
    }
}
