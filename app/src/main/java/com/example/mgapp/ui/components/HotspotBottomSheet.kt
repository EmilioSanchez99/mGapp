package com.example.mgapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mgapp.domain.model.Hotspot

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotspotBottomSheet(
    hotspot: Hotspot,
    onSave: (Hotspot) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(hotspot.name) }
    var description by remember { mutableStateOf(hotspot.description ?: "") }

    ModalBottomSheet(
        onDismissRequest = { onDismiss() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                text = "Editar Hotspot",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = { onDismiss() }) {
                    Text("Cancelar")
                }
                Spacer(Modifier.width(8.dp))
                Button(
                    onClick = {
                        onSave(hotspot.copy(name = name, description = description))
                        onDismiss()
                    }
                ) {
                    Text("Guardar")
                }
            }
        }
    }
}
