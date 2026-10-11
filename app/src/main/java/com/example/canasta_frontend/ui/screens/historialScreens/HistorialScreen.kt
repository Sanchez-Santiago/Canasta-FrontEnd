package com.example.canasta_frontend.ui.screens.historialScreens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.canasta_frontend.core.sync.SyncEngine
import com.example.canasta_frontend.core.sync.SyncUiState
import com.example.canasta_frontend.data.database.entity.MovimientoEntity
import com.example.canasta_frontend.data.network.RetrofitClient
import com.example.canasta_frontend.data.repository.MovementsRepository
import com.example.canasta_frontend.ui.theme.*
import kotlinx.coroutines.launch

// Cache por grupo
private var cachedMovimientosByGroup = mutableMapOf<Long, List<MovimientoModel>>()

@Composable
fun HistorialScreen(
    activeGroupId: Long?,
    onDataChanged: () -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val movementsRepo = remember { MovementsRepository(context) }

    var uiState by remember(activeGroupId) { mutableStateOf<SyncUiState<List<MovimientoModel>>>(SyncUiState.Loading) }
    var showAddMovementDialog by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    fun loadData() {
        val targetGroup = activeGroupId ?: return
        coroutineScope.launch {
            val api = RetrofitClient.createService(context)

            SyncEngine.sync(
                fetchLocal = {
                    cachedMovimientosByGroup[targetGroup]?.takeIf { it.isNotEmpty() }
                },
                fetchRemote = {
                    val movResp = api.getMovimientos(targetGroup)
                    val remote = movResp.data?.movimientos ?: emptyList()
                    remote.map { m ->
                        MovimientoModel(
                            id = m.id_movimiento,
                            nombre = m.nombre,
                            descripcion = m.descripcion ?: "",
                            monto = m.monto,
                            tipo = m.tipo,
                            categoria = m.categoria ?: "General",
                            usuario = "Santiago",
                            fecha = m.fecha_movimiento
                        )
                    }
                },
                saveLocal = { newMovs ->
                    if (activeGroupId != null) cachedMovimientosByGroup[activeGroupId] = newMovs
                },
                isDataEmpty = { it.isEmpty() }
            ).collect { state ->
                uiState = state
            }
        }
    }

    LaunchedEffect(activeGroupId) {
        if (activeGroupId != null) {
            val cached = cachedMovimientosByGroup[activeGroupId]
            uiState = if (!cached.isNullOrEmpty()) SyncUiState.Content(data = cached, hasLocalData = true) else SyncUiState.Loading
            loadData()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        val currentMovs = when (val s = uiState) {
            is SyncUiState.Content -> s.data
            else -> cachedMovimientosByGroup[activeGroupId] ?: emptyList()
        }

        HistorialHeader(
            totalMovimientos = currentMovs.size,
            onAddMovimientoClick = { showAddMovementDialog = true }
        )

        Spacer(modifier = Modifier.height(4.dp))

        when (val state = uiState) {
            is SyncUiState.Loading -> {
                Box(modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }

            is SyncUiState.Empty -> {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp)
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(24.dp)),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                                .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f), RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.Receipt, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
                        }

                        Text(
                            text = "Sin Movimientos Registrados",
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "Aún no hay ingresos o gastos registrados en este período. ¡Comienza a registrar tus finanzas!",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )

                        Button(
                            onClick = { showAddMovementDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(18.dp))
                                Text(text = "Registrar Primer Movimiento", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }
                    }
                }
            }

            is SyncUiState.Error -> {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp)
                        .border(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.4f), RoundedCornerShape(20.dp)),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(text = state.message, color = MaterialTheme.colorScheme.error, fontSize = 13.sp, textAlign = TextAlign.Center)
                        Button(
                            onClick = { loadData() },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                Text(text = "Reintentar", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            is SyncUiState.Content -> {
                state.data.forEach { mov ->
                    HistorialCardItem(movimiento = mov)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }

    if (showAddMovementDialog && activeGroupId != null) {
        AddMovementDialog(
            onDismiss = { showAddMovementDialog = false },
            onAddSuccess = { name, amount, type, cat, desc, date ->
                coroutineScope.launch {
                    val targetGroup = activeGroupId
                    val entity = MovimientoEntity(
                        id_movimiento = System.currentTimeMillis(),
                        id_usuario_creador = 1L,
                        id_grupo = targetGroup,
                        nombre = name,
                        monto = amount,
                        tipo = type,
                        categoria = cat,
                        descripcion = desc.ifBlank { null },
                        fecha_movimiento = date,
                        fecha_creacion = "2025-04-02"
                    )
                    movementsRepo.saveMovimiento(entity)
                    val model = MovimientoModel(entity.id_movimiento, entity.nombre, entity.descripcion ?: "", entity.monto, entity.tipo, entity.categoria ?: cat, "Santiago", entity.fecha_movimiento)
                    val updated = listOf(model) + (cachedMovimientosByGroup[targetGroup] ?: emptyList())
                    cachedMovimientosByGroup[targetGroup] = updated
                    uiState = SyncUiState.Content(data = updated, hasLocalData = true)
                    onDataChanged() // Refresh Resumen screen metrics!
                    Toast.makeText(context, "Movimiento '$name' registrado con éxito", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }
}
