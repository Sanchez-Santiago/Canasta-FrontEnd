package com.example.canasta_frontend.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.canasta_frontend.data.database.entity.GrupoEntity
import com.example.canasta_frontend.data.repository.GroupsRepository
import kotlinx.coroutines.launch

@Composable
fun GroupSelectorModal(
    currentGroupId: Long?,
    onGroupSelected: (GrupoEntity) -> Unit,
    onDismiss: () -> Unit,
    startInCreate: Boolean = false
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val repository = remember(context) { GroupsRepository(context) }
    val cs = MaterialTheme.colorScheme
    val focusManager = LocalFocusManager.current
    val nameFocus = remember { FocusRequester() }

    var grupos by remember { mutableStateOf<List<GrupoEntity>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var loadError by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var isCreating by remember(startInCreate) { mutableStateOf(startInCreate) }
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("Hogar") }
    var isSaving by remember { mutableStateOf(false) }

    fun reload() {
        coroutineScope.launch {
            isLoading = true
            loadError = null
            try {
                grupos = repository.getGrupos()
            } catch (e: Exception) {
                loadError = "No se pudieron cargar los grupos. Reintentá."
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) { reload() }

    LaunchedEffect(isCreating) {
        if (isCreating) {
            try { nameFocus.requestFocus() } catch (_: Exception) { }
        }
    }

    val gruposGrupales = grupos.filter { it.tipo == "grupal" }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(cs.scrim)
            .clickable { if (!isSaving) onDismiss() }
            .imePadding()
            .navigationBarsPadding(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .heightIn(max = 600.dp)
                .border(1.dp, cs.outline, RoundedCornerShape(22.dp))
                .clickable(enabled = false) {},
            colors = CardDefaults.cardColors(containerColor = cs.surface),
            shape = RoundedCornerShape(22.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(cs.primary.copy(alpha = 0.15f), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.Group, contentDescription = null, tint = cs.primary, modifier = Modifier.size(20.dp))
                        }
                        Column {
                            Text(
                                text = if (isCreating) "Crear grupo grupal" else "Grupos Grupales",
                                color = cs.onBackground,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = if (isCreating) "Completá los datos y guardá" else "Selecciona o crea un grupo",
                                color = cs.onSurfaceVariant,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Cerrar", tint = cs.onSurfaceVariant)
                    }
                }

                if (isLoading) {
                    com.example.canasta_frontend.ui.components.RowListSkeleton(count = 3)
                } else if (loadError != null && grupos.isEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(text = loadError!!, color = cs.onSurfaceVariant, fontSize = 13.sp, textAlign = TextAlign.Center)
                        OutlinedButton(onClick = { reload() }) { Text("Reintentar") }
                    }
                } else if (!isCreating) {
                    if (gruposGrupales.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "No tienes ningún grupo grupal activo todavía.",
                                color = cs.onSurfaceVariant,
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 280.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(gruposGrupales, key = { it.id_grupo }) { grupo ->
                                val isSelected = grupo.id_grupo == currentGroupId
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(if (isSelected) cs.primary.copy(alpha = 0.15f) else cs.surfaceVariant)
                                        .border(1.dp, if (isSelected) cs.primary else cs.outline, RoundedCornerShape(14.dp))
                                        .clickable {
                                            onGroupSelected(grupo)
                                            onDismiss()
                                        }
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = grupo.nombre ?: "Sin nombre", color = cs.onBackground, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        if (!grupo.descripcion.isNullOrEmpty()) {
                                            Text(text = grupo.descripcion, color = cs.onSurfaceVariant, fontSize = 11.sp, maxLines = 1)
                                        }
                                    }
                                    if (isSelected) {
                                        Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = cs.primary)
                                    }
                                }
                            }
                        }
                    }

                    OutlinedButton(
                        onClick = { isCreating = true; errorMessage = null },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = cs.primary),
                        border = BorderStroke(1.dp, cs.primary.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Text(text = "Crear Nuevo Grupo Grupal", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = nombre,
                            onValueChange = { nombre = it; errorMessage = null },
                            label = { Text("Nombre del grupo") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().focusRequester(nameFocus),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = cs.primary,
                                unfocusedBorderColor = cs.outline,
                                focusedLabelColor = cs.primary,
                                unfocusedLabelColor = cs.onSurfaceVariant,
                                focusedTextColor = cs.onBackground,
                                unfocusedTextColor = cs.onBackground,
                                cursorColor = cs.primary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = descripcion,
                            onValueChange = { descripcion = it; errorMessage = null },
                            label = { Text("Descripción") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = cs.primary,
                                unfocusedBorderColor = cs.outline,
                                focusedLabelColor = cs.primary,
                                unfocusedLabelColor = cs.onSurfaceVariant,
                                focusedTextColor = cs.onBackground,
                                unfocusedTextColor = cs.onBackground,
                                cursorColor = cs.primary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Text(
                            text = "Categoría",
                            color = cs.onSurfaceVariant,
                            style = MaterialTheme.typography.bodySmall
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("Hogar", "Trabajo", "Amigos", "Otro").forEach { cat ->
                                val selected = categoria == cat
                                FilterChip(
                                    selected = selected,
                                    onClick = { categoria = cat },
                                    label = { Text(cat, style = MaterialTheme.typography.bodySmall) },
                                    shape = RoundedCornerShape(10.dp)
                                )
                            }
                        }

                        if (errorMessage != null) {
                            Text(text = errorMessage!!, color = cs.error, fontSize = 12.sp)
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = {
                                    if (!isSaving) {
                                        if (startInCreate) onDismiss() else { isCreating = false; errorMessage = null }
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Volver", color = cs.onSurfaceVariant)
                            }
                            Button(
                                onClick = {
                                    if (nombre.isBlank()) {
                                        errorMessage = "Ingresa un nombre para el grupo"
                                        return@Button
                                    }
                                    coroutineScope.launch {
                                        isSaving = true
                                        errorMessage = null
                                        try {
                                            val created = repository.createGrupo(
                                                nombre = nombre.trim(),
                                                descripcion = descripcion.trim().ifBlank { null },
                                                categoria = categoria.trim().ifBlank { null }
                                            )
                                            if (created != null) {
                                                grupos = repository.getGrupos()
                                                onGroupSelected(created)
                                                isCreating = false
                                                nombre = ""
                                                descripcion = ""
                                                onDismiss()
                                            } else {
                                                errorMessage = "No se pudo crear el grupo. Verificá tu conexión e intentá de nuevo."
                                            }
                                        } catch (e: Exception) {
                                            errorMessage = "No se pudo crear el grupo: ${e.message ?: "error de red"}"
                                        } finally {
                                            isSaving = false
                                        }
                                    }
                                },
                                enabled = !isSaving,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = cs.primary, contentColor = cs.onPrimary),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                if (isSaving) {
                                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = cs.onPrimary)
                                } else {
                                    Text("Guardar", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
