package com.example.canasta_frontend.ui.screens.canastaScreens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp

data class CanastaFilters(
    val query: String = "",
    val estado: String = "todas",
    val prioridad: String = "todas"
) {
    val hasActive: Boolean get() = query.isNotBlank() || estado != "todas" || prioridad != "todas"
}

fun List<ListaModel>.applyCanastaFilters(f: CanastaFilters): List<ListaModel> {
    val q = f.query.trim().lowercase()
    val est = f.estado.lowercase().trim()
    val prio = f.prioridad.lowercase().trim()
    return filter { lista ->
        val okEstado = est == "todas" || lista.estado.lowercase().trim() == est
        if (!okEstado) return@filter false
        if (q.isBlank() && prio == "todas") return@filter true
        val listaMatch = q.isBlank() || lista.nombre.lowercase().contains(q) || (lista.descripcion ?: "").lowercase().contains(q)
        val prods = lista.productos.filter { p ->
            val okP = prio == "todas" || p.prioridad.lowercase().trim() == prio
            val okQ = q.isBlank() || p.nombre.lowercase().contains(q) || (p.descripcion ?: "").lowercase().contains(q)
            okP && okQ
        }
        if (q.isBlank() && prio != "todas") prods.isNotEmpty() || listaMatch
        else listaMatch || prods.isNotEmpty()
    }.map { lista ->
        if ((q.isBlank() && prio == "todas")) lista
        else {
            val prods = lista.productos.filter { p ->
                val okP = prio == "todas" || p.prioridad.lowercase().trim() == prio
                val okQ = q.isBlank() || p.nombre.lowercase().contains(q) || (p.descripcion ?: "").lowercase().contains(q)
                okP && okQ
            }
            val listaMatch = q.isNotBlank() && (lista.nombre.lowercase().contains(q) || (lista.descripcion ?: "").lowercase().contains(q))
            if (listaMatch && prods.isEmpty()) lista else lista.copy(productos = prods)
        }
    }
}

@Composable
fun CanastaFilterBar(
    filters: CanastaFilters,
    onFiltersChange: (CanastaFilters) -> Unit,
    resultCount: Int,
    totalCount: Int
) {
    val cs = MaterialTheme.colorScheme
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
            value = filters.query,
            onValueChange = { onFiltersChange(filters.copy(query = it)) },
            label = { Text("Buscar por nombre") },
            placeholder = { Text("Lista o producto...") },
            leadingIcon = { Icon(Icons.Default.Search, "Buscar") },
            trailingIcon = {
                if (filters.query.isNotBlank()) {
                    IconButton(onClick = { onFiltersChange(filters.copy(query = "")) }) {
                        Icon(Icons.Default.Clear, "Limpiar búsqueda")
                    }
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        )
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf("todas" to "Todas", "activa" to "Activas", "completada" to "Completadas", "archivada" to "Archivadas").forEach { (id, label) ->
                FilterChip(
                    selected = filters.estado == id,
                    onClick = { onFiltersChange(filters.copy(estado = id)) },
                    label = { Text(label, style = MaterialTheme.typography.bodySmall) },
                    shape = RoundedCornerShape(10.dp)
                )
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            FilterChip(selected = filters.prioridad == "todas", onClick = { onFiltersChange(filters.copy(prioridad = "todas")) }, label = { Text("Toda prioridad", style = MaterialTheme.typography.bodySmall) }, shape = RoundedCornerShape(10.dp))
            listOf("esencial" to "Esencial", "prioritario" to "Primario", "secundario" to "Secundario").forEach { (id, label) ->
                val c = when (id) { "esencial" -> cs.primary; "prioritario" -> cs.secondary; else -> cs.tertiary }
                FilterChip(
                    selected = filters.prioridad == id,
                    onClick = { onFiltersChange(filters.copy(prioridad = if (filters.prioridad == id) "todas" else id)) },
                    label = { Text(label, style = MaterialTheme.typography.bodySmall, color = if (filters.prioridad == id) c else cs.onSurfaceVariant) },
                    shape = RoundedCornerShape(10.dp),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = if (filters.prioridad == id) c else cs.outline,
                        selectedBorderColor = c, enabled = true, selected = filters.prioridad == id
                    )
                )
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("$resultCount de $totalCount listas", style = MaterialTheme.typography.bodySmall, color = cs.onSurfaceVariant)
            if (filters.hasActive) {
                TextButton(onClick = { onFiltersChange(CanastaFilters()) }) { Text("Limpiar filtros") }
            }
        }
    }
}
