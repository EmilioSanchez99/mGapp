package com.example.mgapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
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
    onDelete: ((Hotspot) -> Unit)? = null, // ✅ nuevo callback opcional
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
                text = if (hotspot.name.isNullOrEmpty()) "Nuevo Hotspot" else "Editar Hotspot",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(Modifier.height(16.dp))

            // 🔹 Campo nombre
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            // 🔹 Campo descripción
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(24.dp))

            // 🔹 Botones principales
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismiss) {
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

            // 🔹 Botón eliminar solo si el hotspot ya existe (tiene nombre o descripción)
            if (!hotspot.name.isNullOrEmpty() || !hotspot.description.isNullOrEmpty()) {
                Spacer(Modifier.height(16.dp))
                OutlinedButton(
                    onClick = {
                        onDelete?.invoke(hotspot)
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Eliminar este hotspot")
                }
            }
        }
    }
}
