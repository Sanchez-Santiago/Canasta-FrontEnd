package com.example.canasta_frontend.ui.screens.canastaScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import java.util.Locale

@Composable
fun CanastaCardItem(
    lista: ListaModel,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onToggleProduct: (Long, Long) -> Unit,
    onEditProduct: (Long, ProductoModel) -> Unit = { _, _ -> },
    onDeleteProduct: (Long, Long) -> Unit = { _, _ -> },
    onEditList: (Long) -> Unit = {},
    onDeleteList: (Long) -> Unit
) {
    val cs = MaterialTheme.colorScheme
    val totalPresupuesto = lista.productos.sumOf { (it.cantidad * it.precioUnitario).toInt() }
    val compradosCount = lista.productos.count { it.estado == "comprado" }
    val totalCount = lista.productos.size
    val progress = if (totalCount > 0) compradosCount.toFloat() / totalCount else 0f
    var showMenu by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, if (isExpanded) cs.primary.copy(alpha = 0.5f) else cs.outline, RoundedCornerShape(22.dp))
            .clickable { onToggleExpand() },
        colors = CardDefaults.cardColors(containerColor = cs.surface),
        shape = RoundedCornerShape(22.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(0.dp)) {
            // Cover compacto con gradiente
            Box(
                modifier = Modifier.fillMaxWidth()
                    .background(cs.primary.copy(alpha = 0.10f), RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp))
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(text = lista.nombre, color = cs.onBackground, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f, fill = false))
                        Surface(
                            color = if (lista.estado == "activa") cs.primary.copy(alpha = 0.15f) else cs.surfaceVariant,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "$compradosCount/$totalCount",
                                color = if (lista.estado == "activa") cs.primary else cs.onSurfaceVariant,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                    if (!lista.descripcion.isNullOrEmpty()) {
                        Text(text = lista.descripcion, color = cs.onSurfaceVariant, style = MaterialTheme.typography.bodySmall, maxLines = 2)
                    }
                    if (totalCount > 0) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            LinearProgressIndicator(progress = { progress }, modifier = Modifier.weight(1f).height(6.dp).clip(RoundedCornerShape(3.dp)), color = cs.primary, trackColor = cs.outline.copy(alpha = 0.4f))
                            Text("${(progress * 100).toInt()}%", style = MaterialTheme.typography.labelSmall, color = cs.onSurfaceVariant)
                        }
                    } else {
                        Text(lista.estado.replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.labelSmall, color = cs.onSurfaceVariant)
                    }
                }
                Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = "$${totalPresupuesto.toLocaleString()}", color = cs.primary, style = MaterialTheme.typography.titleLarge)
                    Text(text = "estimado", color = cs.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
                    Box {
                        IconButton(onClick = { showMenu = true }, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.MoreVert, "Opciones", tint = cs.onSurfaceVariant, modifier = Modifier.size(18.dp))
                        }
                        DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                            DropdownMenuItem(text = { Text("Editar lista") }, leadingIcon = { Icon(Icons.Default.Edit, null) }, onClick = { showMenu = false; onEditList(lista.id) })
                            DropdownMenuItem(text = { Text("Eliminar", color = cs.error) }, leadingIcon = { Icon(Icons.Default.Delete, null, tint = cs.error) }, onClick = { showMenu = false; showDeleteConfirm = true })
                        }
                    }
                }
            }
            }
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {

            if (isExpanded) {
                HorizontalDivider(color = cs.outline, thickness = 1.dp)
                if (lista.productos.isEmpty()) {
                    Text(text = "Sin productos. Usá (+) para añadir.", color = cs.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        lista.productos.forEach { prod ->
                            ProductoRow(
                                prod = prod,
                                onToggle = { onToggleProduct(lista.id, prod.id) },
                                onEdit = { onEditProduct(lista.id, prod) },
                                onDelete = { onDeleteProduct(lista.id, prod.id) }
                            )
                        }
                    }
                }
            }
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Eliminar lista") },
            text = { Text("Se elimina '${lista.nombre}' del servidor. Solo el creador o admin puede hacerlo.") },
            confirmButton = {
                TextButton(onClick = { showDeleteConfirm = false; onDeleteList(lista.id) }) { Text("Eliminar", color = cs.error) }
            },
            dismissButton = { TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancelar") } }
        )
    }
}

@Composable
private fun ProductoRow(
    prod: ProductoModel,
    onToggle: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val cs = MaterialTheme.colorScheme
    val isComprado = prod.estado == "comprado"
    var showMenu by remember { mutableStateOf(false) }
    var confirmDelete by remember { mutableStateOf(false) }
    val prioColor = when (prod.prioridad) {
        "esencial" -> cs.primary
        "prioritario" -> cs.secondary
        else -> cs.tertiary
    }
    val subtotal = (prod.cantidad * prod.precioUnitario).toInt()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(prioColor.copy(alpha = 0.12f))
            .border(1.dp, prioColor.copy(alpha = 0.45f), RoundedCornerShape(12.dp))
            .padding(start = 0.dp, end = 10.dp, top = 10.dp, bottom = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(0.dp), modifier = Modifier.weight(1f)) {
            Box(
                modifier = Modifier.width(5.dp).height(52.dp)
                    .background(prioColor, RoundedCornerShape(topEnd = 4.dp, bottomEnd = 4.dp))
            )
            Spacer(Modifier.width(8.dp))
            IconButton(onClick = onToggle, modifier = Modifier.size(28.dp)) {
                Icon(
                    imageVector = if (isComprado) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                    contentDescription = if (isComprado) "Marcar pendiente" else "Marcar comprado",
                    tint = if (isComprado) cs.primary else cs.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = prod.nombre,
                    color = if (isComprado) cs.onSurfaceVariant else cs.onBackground,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "${prod.cantidad} x $${prod.precioUnitario.toLocaleString()} = $${subtotal.toLocaleString()}",
                    color = cs.onBackground,
                    style = MaterialTheme.typography.labelLarge
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = prioColor.copy(alpha = 0.28f),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, prioColor)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                            Box(Modifier.size(8.dp).background(prioColor, androidx.compose.foundation.shape.CircleShape))
                            Text(
                                prod.prioridad.uppercase(),
                                color = prioColor,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                    if (!prod.descripcion.isNullOrBlank()) Text(prod.descripcion, color = cs.onSurfaceVariant, style = MaterialTheme.typography.bodySmall, maxLines = 1, modifier = Modifier.weight(1f, fill = false))
                    else if (!prod.codigoProducto.isNullOrBlank()) Text("#${prod.codigoProducto}", color = cs.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
        Box {
            IconButton(onClick = { showMenu = true }, modifier = Modifier.size(28.dp)) {
                Icon(Icons.Default.MoreVert, "Acciones", tint = cs.onSurfaceVariant, modifier = Modifier.size(18.dp))
            }
            DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                DropdownMenuItem(text = { Text(if (isComprado) "Marcar pendiente" else "Marcar comprado") }, onClick = { showMenu = false; onToggle() })
                DropdownMenuItem(text = { Text("Editar") }, leadingIcon = { Icon(Icons.Default.Edit, null) }, onClick = { showMenu = false; onEdit() })
                DropdownMenuItem(text = { Text("Eliminar", color = cs.error) }, leadingIcon = { Icon(Icons.Default.Delete, null, tint = cs.error) }, onClick = { showMenu = false; confirmDelete = true })
            }
        }
    }

    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text("Eliminar producto") },
            text = { Text("Se elimina '${prod.nombre}'. Solo creador o admin.") },
            confirmButton = { TextButton(onClick = { confirmDelete = false; onDelete() }) { Text("Eliminar", color = cs.error) } },
            dismissButton = { TextButton(onClick = { confirmDelete = false }) { Text("Cancelar") } }
        )
    }
}

private fun Int.toLocaleString(): String {
    return String.format(Locale.getDefault(), "%,d", this).replace(',', '.')
}
