package com.example.canasta_frontend.ui.screens.canastaScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.canasta_frontend.ui.theme.*

@Composable
fun AddProductDialog(
    onDismiss: () -> Unit,
    onAddSuccess: (nombre: String, descripcion: String, prioridad: String, cantidad: Double, precioUnitario: Int, codigoProducto: String) -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var prioridad by remember { mutableStateOf("esencial") } // "esencial", "prioritario", "secundario"
    var cantidad by remember { mutableStateOf("1") }
    var precioUnitario by remember { mutableStateOf("1000") }
    var codigoProducto by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(20.dp),
        title = {
            Text(
                text = "Añadir Producto a Canasta",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (errorMessage != null) {
                    Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                }

                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre del producto *", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                    placeholder = { Text("Ej. Leche Entera 1L", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                    singleLine = true,
                    colors = textFieldColors(),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción (Opcional)", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                    placeholder = { Text("Ej. Marca La Serenísima", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                    singleLine = true,
                    colors = textFieldColors(),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                // Prioridad selector
                Text(text = "Prioridad del producto *", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("esencial" to "Esencial", "prioritario" to "Primario", "secundario" to "Secundario").forEach { (key, label) ->
                        val isSelected = prioridad == key
                        val color = when (key) {
                            "esencial" -> MaterialTheme.colorScheme.primary
                            "prioritario" -> MaterialTheme.colorScheme.secondary
                            else -> MaterialTheme.colorScheme.tertiary
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) color.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surfaceVariant)
                                .border(1.dp, if (isSelected) color else MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                                .clickable { prioridad = key }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) color else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = cantidad,
                        onValueChange = { cantidad = it },
                        label = { Text("Cantidad", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                        singleLine = true,
                        colors = textFieldColors(),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = precioUnitario,
                        onValueChange = { precioUnitario = it },
                        label = { Text("Precio est. ($)", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                        singleLine = true,
                        colors = textFieldColors(),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = codigoProducto,
                    onValueChange = { codigoProducto = it },
                    label = { Text("Código de barras (Opcional)", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                    singleLine = true,
                    colors = textFieldColors(),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (nombre.isBlank()) {
                        errorMessage = "Ingresa un nombre de producto."
                        return@Button
                    }
                    val qty = cantidad.toDoubleOrNull() ?: 1.0
                    val price = precioUnitario.toIntOrNull() ?: 0
                    onAddSuccess(nombre.trim(), descripcion.trim(), prioridad, qty, price, codigoProducto.trim())
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Añadir Producto", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    )
}

@Composable
private fun textFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = MaterialTheme.colorScheme.primary,
    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
    focusedLabelColor = MaterialTheme.colorScheme.primary,
    unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
    focusedTextColor = MaterialTheme.colorScheme.onBackground,
    cursorColor = MaterialTheme.colorScheme.primary
)
