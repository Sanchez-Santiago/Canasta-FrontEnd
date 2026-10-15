package com.example.canasta_frontend.ui.screens.notificacionesScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.canasta_frontend.data.database.entity.InvitacionGrupoEntity
import com.example.canasta_frontend.data.database.entity.NotificacionEntity
import com.example.canasta_frontend.data.network.RetrofitClient
import com.example.canasta_frontend.data.repository.InvitationsRepository
import kotlinx.coroutines.launch

@Composable
fun NotificacionesScreen(
    groupId: Long?,
    onBack: () -> Unit,
    onInviteResponded: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val cs = MaterialTheme.colorScheme
    val repo = remember(context) { InvitationsRepository(context) }

    var notis by remember { mutableStateOf<List<NotificacionEntity>>(emptyList()) }
    var recibidas by remember { mutableStateOf<List<InvitacionGrupoEntity>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var respondingId by remember { mutableStateOf<Long?>(null) }
    var respondError by remember { mutableStateOf<String?>(null) }

    fun load() {
        scope.launch {
            loading = true; error = null
            try {
                val api = RetrofitClient.createService(context)
                val list = if (groupId != null) {
                    val resp = api.getNotificaciones(groupId)
                    resp.data?.values?.flatten() ?: emptyList()
                } else emptyList()
                notis = list
                recibidas = try { repo.getRecibidas() } catch (_: Exception) { emptyList() }
            } catch (e: Exception) {
                error = e.message ?: "No se pudieron cargar"
            } finally { loading = false }
        }
    }

    fun respond(id: Long, aceptar: Boolean) {
        scope.launch {
            respondingId = id
            respondError = null
            when (val r = repo.responder(id, aceptar)) {
                is com.example.canasta_frontend.data.repository.InviteResult.Success -> {
                    recibidas = recibidas.filterNot { it.id_invitacion == id }
                    android.widget.Toast.makeText(
                        context,
                        if (aceptar) "Te uniste al grupo" else "Invitación rechazada",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                    onInviteResponded()
                }
                is com.example.canasta_frontend.data.repository.InviteResult.Failure -> respondError = r.message
            }
            respondingId = null
        }
    }

    LaunchedEffect(groupId) { load() }

    Box(
        modifier = Modifier.fillMaxSize().background(cs.background).statusBarsPadding().navigationBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = cs.onBackground) }
                Surface(color = cs.primary.copy(alpha = 0.15f), shape = RoundedCornerShape(12.dp)) {
                    Icon(Icons.Default.Notifications, null, tint = cs.primary, modifier = Modifier.padding(8.dp).size(22.dp))
                }
                Column {
                    Text("Notificaciones", style = MaterialTheme.typography.titleLarge, color = cs.onBackground)
                    Text(if (groupId != null) "Grupo #$groupId" else "Sin grupo", style = MaterialTheme.typography.bodySmall, color = cs.onSurfaceVariant)
                }
            }

            if (loading) {
                com.example.canasta_frontend.ui.components.RowListSkeleton(count = 4)
            } else if (error != null) {
                Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = cs.surface), border = androidx.compose.foundation.BorderStroke(1.dp, cs.outline)) {
                    Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(error!!, color = cs.error, style = MaterialTheme.typography.bodyMedium)
                        Button(onClick = { load() }) { Text("Reintentar") }
                    }
                }
            } else if (notis.isEmpty() && recibidas.isEmpty()) {
                Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = cs.surface), border = androidx.compose.foundation.BorderStroke(1.dp, cs.outline)) {
                    Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Icon(Icons.Default.Notifications, null, tint = cs.primary, modifier = Modifier.size(32.dp))
                        Text("Sin notificaciones", style = MaterialTheme.typography.titleMedium, color = cs.onBackground)
                        Text("Cuando haya movimientos, invitaciones o avisos del grupo van a aparecer acá.", style = MaterialTheme.typography.bodySmall, color = cs.onSurfaceVariant)
                    }
                }
            } else {
                if (recibidas.isNotEmpty()) {
                    Text("Invitaciones recibidas (${recibidas.size})", style = MaterialTheme.typography.titleSmall, color = cs.onSurfaceVariant)
                    if (respondError != null) Text(respondError!!, color = cs.error, style = MaterialTheme.typography.bodySmall)
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(recibidas, key = { it.id_invitacion }) { inv ->
                            val busy = respondingId == inv.id_invitacion
                            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = cs.surfaceVariant), border = androidx.compose.foundation.BorderStroke(1.dp, cs.outline)) {
                                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text("Grupo #${inv.id_grupo}", style = MaterialTheme.typography.titleSmall, color = cs.onBackground)
                                    Text("Invitación #${inv.id_invitacion} · ${inv.fecha_creacion}", style = MaterialTheme.typography.bodySmall, color = cs.onSurfaceVariant)
                                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        OutlinedButton(onClick = { respond(inv.id_invitacion, false) }, enabled = !busy, modifier = Modifier.weight(1f).height(42.dp), shape = RoundedCornerShape(10.dp)) {
                                            Icon(Icons.Default.Close, null, modifier = Modifier.size(15.dp)); Spacer(Modifier.width(4.dp)); Text("Rechazar")
                                        }
                                        Button(onClick = { respond(inv.id_invitacion, true) }, enabled = !busy, modifier = Modifier.weight(1f).height(42.dp), shape = RoundedCornerShape(10.dp), colors = ButtonDefaults.buttonColors(containerColor = cs.primary, contentColor = cs.onPrimary)) {
                                            if (busy) CircularProgressIndicator(Modifier.size(15.dp), strokeWidth = 2.dp, color = cs.onPrimary)
                                            else { Icon(Icons.Default.Check, null, modifier = Modifier.size(15.dp)); Spacer(Modifier.width(4.dp)); Text("Aceptar") }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                Text("Tablón del grupo (${notis.size})", style = MaterialTheme.typography.titleSmall, color = cs.onSurfaceVariant)
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
                    items(notis, key = { it.id_notificacion }) { n ->
                        Card(
                            Modifier.fillMaxWidth().border(1.dp, cs.outline, RoundedCornerShape(16.dp)),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = cs.surface)
                        ) {
                            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(n.titulo ?: "", style = MaterialTheme.typography.titleSmall, color = cs.onBackground)
                                Text(n.tipo ?: "", style = MaterialTheme.typography.labelSmall, color = cs.primary)
                                if (!n.descripcion.isNullOrBlank()) Text(n.descripcion, style = MaterialTheme.typography.bodySmall, color = cs.onSurfaceVariant)
                                Text(n.fecha_creacion ?: "", style = MaterialTheme.typography.bodySmall, color = cs.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }
    }
}
