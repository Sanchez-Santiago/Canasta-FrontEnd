package com.example.canasta_frontend.ui.screens.historialScreens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun EditMovementDialog(
    initial: MovimientoModel,
    saving: Boolean,
    error: String?,
    onDismiss: () -> Unit,
    onSave: (nombre: String, descripcion: String, monto: Int, categoria: String) -> Unit
) {
    var nombre by remember { mutableStateOf(initial.nombre) }
    var descripcion by remember { mutableStateOf(initial.descripcion) }
    var monto by remember { mutableStateOf(initial.monto.toString()) }
    var categoria by remember { mutableStateOf(initial.categoria) }
    val cs = MaterialTheme.colorScheme
    AlertDialog(
        onDismissRequest = { if (!saving) onDismiss() },
        title = { Text("Editar movimiento") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (error != null) Text(error, color = cs.error, style = MaterialTheme.typography.bodySmall)
                OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                OutlinedTextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text("Descripción") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = monto, onValueChange = { monto = it.filter { c -> c.isDigit() } }, label = { Text("Monto") }, placeholder = { Text("0") }, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp))
                    OutlinedTextField(value = categoria, onValueChange = { categoria = it }, label = { Text("Categoría") }, singleLine = true, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp))
                }
                Text("Tipo ${initial.tipo} y fecha no editables.", style = MaterialTheme.typography.bodySmall, color = cs.onSurfaceVariant)
            }
        },
        confirmButton = {
            Button(onClick = { onSave(nombre.trim(), descripcion.trim(), monto.toIntOrNull() ?: initial.monto, categoria.trim().ifBlank { initial.categoria }) }, enabled = !saving && nombre.isNotBlank()) {
                Text(if (saving) "Guardando..." else "Guardar")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss, enabled = !saving) { Text("Cancelar") } }
    )
}
