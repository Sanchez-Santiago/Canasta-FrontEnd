package com.example.canasta_frontend.ui.screens.historialScreens

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
fun AddMovementDialog(
    onDismiss: () -> Unit,
    onAddSuccess: (nombre: String, monto: Int, tipo: String, categoria: String, descripcion: String, fecha: String) -> Unit
) {
    var tipo by remember { mutableStateOf("gasto") } // "gasto" o "ingreso"
    var nombre by remember { mutableStateOf("") }
    var monto by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("Alimentos") }
    var descripcion by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf("2025-04-02") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val categoriasList = listOf("Alimentos", "Servicios", "Salud", "Limpieza", "Aporte", "Varios")

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(20.dp),
        title = {
            Text(
                text = "Registrar Movimiento Financiero",
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

                // Tipo selector (Gasto / Ingreso)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (tipo == "gasto") MaterialTheme.colorScheme.error.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surfaceVariant)
                            .border(1.dp, if (tipo == "gasto") MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline, RoundedCornerShape(10.dp))
                            .clickable { tipo = "gasto" }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🔴 Gasto (Egreso)",
                            color = if (tipo == "gasto") MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (tipo == "ingreso") MaterialTheme.colorScheme.primary.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surfaceVariant)
                            .border(1.dp, if (tipo == "ingreso") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline, RoundedCornerShape(10.dp))
                            .clickable { tipo = "ingreso" }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🟢 Ingreso",
                            color = if (tipo == "ingreso") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Concepto / Nombre *", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                    placeholder = { Text("Ej. Compra Supermercado Día", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                    singleLine = true,
                    colors = textFieldColors(),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = monto,
                    onValueChange = { monto = it },
                    label = { Text("Monto ($) *", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                    placeholder = { Text("Ej. 12500", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                    singleLine = true,
                    colors = textFieldColors(),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción (Opcional)", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                    singleLine = true,
                    colors = textFieldColors(),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = fecha,
                    onValueChange = { fecha = it },
                    label = { Text("Fecha (YYYY-MM-DD)", color = MaterialTheme.colorScheme.onSurfaceVariant) },
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
                    val parsedMonto = monto.toIntOrNull()
                    if (nombre.isBlank() || parsedMonto == null || parsedMonto <= 0) {
                        errorMessage = "Ingresa un concepto y un monto válido mayor a 0."
                        return@Button
                    }
                    onAddSuccess(nombre.trim(), parsedMonto, tipo, categoria, descripcion.trim(), fecha.trim())
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = if (tipo == "gasto") MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Guardar Movimiento", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
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
