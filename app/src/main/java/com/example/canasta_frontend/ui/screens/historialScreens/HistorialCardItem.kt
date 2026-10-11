package com.example.canasta_frontend.ui.screens.historialScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.util.Locale

@Composable
fun HistorialCardItem(
    movimiento: MovimientoModel,
    expanded: Boolean = false,
    onToggleExpand: () -> Unit = {},
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    val cs = MaterialTheme.colorScheme
    val isIngreso = movimiento.tipo == "ingreso"
    var showMenu by remember { mutableStateOf(false) }
    var confirmDelete by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, cs.outline, RoundedCornerShape(22.dp))
            .clickable { onToggleExpand() },
        colors = CardDefaults.cardColors(containerColor = cs.surface),
        shape = RoundedCornerShape(22.dp)
    ) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(if (isIngreso) cs.primary.copy(alpha = 0.15f) else cs.error.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                            .border(1.dp, if (isIngreso) cs.primary.copy(alpha = 0.4f) else cs.error.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isIngreso) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                            contentDescription = null,
                            tint = if (isIngreso) cs.primary else cs.error,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(text = movimiento.nombre, color = cs.onBackground, style = MaterialTheme.typography.titleSmall, maxLines = 1)
                        Text(text = "${movimiento.categoria} · ${movimiento.fecha}", color = cs.onSurfaceVariant, style = MaterialTheme.typography.bodySmall, maxLines = 1)
                        if (!movimiento.descripcion.isNullOrBlank()) {
                            Text(text = movimiento.descripcion, color = cs.onSurfaceVariant, style = MaterialTheme.typography.bodySmall, maxLines = if (expanded) Int.MAX_VALUE else 1)
                        }
                    }
                }
                Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = (if (isIngreso) "+" else "-") + "$${movimiento.monto.toLocaleString()}",
                        color = if (isIngreso) cs.primary else cs.onBackground,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Surface(color = (if (isIngreso) cs.primary else cs.error).copy(alpha = 0.15f), shape = RoundedCornerShape(6.dp)) {
                        Text(movimiento.tipo, style = MaterialTheme.typography.labelSmall, color = if (isIngreso) cs.primary else cs.error, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                }
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Por ${movimiento.usuario}", style = MaterialTheme.typography.bodySmall, color = cs.onSurfaceVariant)
                Box {
                    IconButton(onClick = { showMenu = true }, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.MoreVert, "Acciones", tint = cs.onSurfaceVariant, modifier = Modifier.size(18.dp))
                    }
                    DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                        DropdownMenuItem(text = { Text("Editar") }, leadingIcon = { Icon(Icons.Default.Edit, null) }, onClick = { showMenu = false; onEdit() })
                        DropdownMenuItem(text = { Text("Eliminar", color = cs.error) }, leadingIcon = { Icon(Icons.Default.Delete, null, tint = cs.error) }, onClick = { showMenu = false; confirmDelete = true })
                    }
                }
            }
        }
    }

    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text("Eliminar movimiento") },
            text = { Text("Se elimina '${movimiento.nombre}'. Solo creador o admin.") },
            confirmButton = { TextButton(onClick = { confirmDelete = false; onDelete() }) { Text("Eliminar", color = cs.error) } },
            dismissButton = { TextButton(onClick = { confirmDelete = false }) { Text("Cancelar") } }
        )
    }
}

private fun Int.toLocaleString(): String {
    return String.format(Locale.getDefault(), "%,d", this).replace(',', '.')
}
