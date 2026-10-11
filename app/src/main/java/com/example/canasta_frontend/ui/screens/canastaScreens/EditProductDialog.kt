package com.example.canasta_frontend.ui.screens.canastaScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun EditProductDialog(
    initial: ProductoModel,
    saving: Boolean,
    error: String?,
    onDismiss: () -> Unit,
    onSave: (nombre: String, descripcion: String, prioridad: String, cantidad: Double, precio: Int, codigo: String) -> Unit
) {
    var nombre by remember { mutableStateOf(initial.nombre) }
    var descripcion by remember { mutableStateOf(initial.descripcion ?: "") }
    var prioridad by remember { mutableStateOf(initial.prioridad) }
    var cantidad by remember { mutableStateOf(initial.cantidad.toString()) }
    var precio by remember { mutableStateOf(initial.precioUnitario.toString()) }
    var codigo by remember { mutableStateOf(initial.codigoProducto ?: "") }
    var localError by remember { mutableStateOf<String?>(null) }
    val cs = MaterialTheme.colorScheme

    AlertDialog(
        onDismissRequest = { if (!saving) onDismiss() },
        title = { Text("Editar producto") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(error ?: localError ?: "", color = cs.error, style = MaterialTheme.typography.bodySmall)
                OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                OutlinedTextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text("Descripción") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                Text("Prioridad *", style = MaterialTheme.typography.bodySmall, color = cs.onSurfaceVariant)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("esencial" to "Esencial", "prioritario" to "Primario", "secundario" to "Secundario").forEach { (key, label) ->
                        val isSelected = prioridad == key
                        val color = when (key) {
                            "esencial" -> cs.primary
                            "prioritario" -> cs.secondary
                            else -> cs.tertiary
                        }
                        Box(
                            modifier = Modifier.weight(1f).clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) color.copy(alpha = 0.25f) else cs.surfaceVariant)
                                .border(1.dp, if (isSelected) color else cs.outline, RoundedCornerShape(10.dp))
                                .clickable { prioridad = key }.padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(label, color = if (isSelected) color else cs.onSurfaceVariant, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = cantidad, onValueChange = { cantidad = it.filter { c -> c.isDigit() || c == '.' } }, label = { Text("Cant.") }, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp))
                    OutlinedTextField(value = precio, onValueChange = { precio = it.filter { c -> c.isDigit() } }, label = { Text("Precio") }, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp))
                }
                OutlinedTextField(value = codigo, onValueChange = { codigo = it }, label = { Text("Código (opcional)") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (nombre.isBlank()) { localError = "Nombre requerido"; return@Button }
                    onSave(nombre.trim(), descripcion.trim(), prioridad, cantidad.toDoubleOrNull() ?: initial.cantidad, precio.toIntOrNull() ?: initial.precioUnitario, codigo.trim())
                },
                enabled = !saving
            ) { Text(if (saving) "Guardando..." else "Guardar") }
        },
        dismissButton = { TextButton(onClick = onDismiss, enabled = !saving) { Text("Cancelar") } }
    )
}

@Composable
fun EditListDialog(
    initialName: String,
    initialDesc: String,
    saving: Boolean,
    error: String?,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var nombre by remember { mutableStateOf(initialName) }
    var desc by remember { mutableStateOf(initialDesc) }
    val cs = MaterialTheme.colorScheme
    AlertDialog(
        onDismissRequest = { if (!saving) onDismiss() },
        title = { Text("Editar lista") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (error != null) Text(error, color = cs.error, style = MaterialTheme.typography.bodySmall)
                OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Descripción") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
            }
        },
        confirmButton = {
            Button(onClick = { onSave(nombre.trim(), desc.trim()) }, enabled = !saving && nombre.isNotBlank()) {
                Text(if (saving) "Guardando..." else "Guardar")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss, enabled = !saving) { Text("Cancelar") } }
    )
}
