package com.example.canasta_frontend.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.canasta_frontend.data.database.entity.InvitacionGrupoEntity

@Composable
fun PendingInvitationsDialog(
    invitaciones: List<InvitacionGrupoEntity>,
    respondingId: Long?,
    errorMessage: String?,
    onRespond: (Long, Boolean) -> Unit,
    onDismiss: () -> Unit,
    onSeeAll: () -> Unit
) {
    val cs = MaterialTheme.colorScheme
    Box(
        modifier = Modifier.fillMaxSize().background(cs.scrim),
        contentAlignment = Alignment.BottomCenter
    ) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = cs.surface),
            border = BorderStroke(1.dp, cs.outline)
        ) {
            Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Surface(color = cs.primary.copy(alpha = 0.15f), shape = RoundedCornerShape(10.dp)) {
                            Icon(Icons.Default.GroupAdd, null, tint = cs.primary, modifier = Modifier.padding(8.dp).size(20.dp))
                        }
                        Column {
                            Text("Tienes ${invitaciones.size} invitación${if (invitaciones.size > 1) "es" else ""}", style = MaterialTheme.typography.titleMedium, color = cs.onBackground)
                            Text("Aceptá para unirte al grupo", style = MaterialTheme.typography.bodySmall, color = cs.onSurfaceVariant)
                        }
                    }
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, "Cerrar", tint = cs.onSurfaceVariant) }
                }
                if (errorMessage != null) {
                    Text(errorMessage, color = cs.error, style = MaterialTheme.typography.bodySmall)
                }
                LazyColumn(modifier = Modifier.heightIn(max = 300.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(invitaciones, key = { it.id_invitacion }) { inv ->
                        val busy = respondingId == inv.id_invitacion
                        val anyBusy = respondingId != null
                        Card(
                            Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = cs.surfaceVariant),
                            border = BorderStroke(1.dp, cs.outline)
                        ) {
                            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Grupo #${inv.id_grupo}", style = MaterialTheme.typography.titleSmall, color = cs.onBackground)
                                Text("Invitación #${inv.id_invitacion} · ${inv.fecha_creacion}", style = MaterialTheme.typography.bodySmall, color = cs.onSurfaceVariant)
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedButton(
                                        onClick = { onRespond(inv.id_invitacion, false) },
                                        enabled = !anyBusy,
                                        modifier = Modifier.weight(1f).height(42.dp),
                                        shape = RoundedCornerShape(10.dp)
                                    ) { Icon(Icons.Default.Close, null, modifier = Modifier.size(16.dp)); Spacer(Modifier.width(4.dp)); Text("Rechazar") }
                                    Button(
                                        onClick = { onRespond(inv.id_invitacion, true) },
                                        enabled = !anyBusy,
                                        modifier = Modifier.weight(1f).height(42.dp),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = cs.primary, contentColor = cs.onPrimary)
                                    ) {
                                        if (busy) CircularProgressIndicator(Modifier.size(16.dp), strokeWidth = 2.dp, color = cs.onPrimary)
                                        else { Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp)); Spacer(Modifier.width(4.dp)); Text("Aceptar") }
                                    }
                                }
                            }
                        }
                    }
                }
                TextButton(onClick = onSeeAll, modifier = Modifier.fillMaxWidth()) { Text("Ver todas en Notificaciones") }
            }
        }
    }
}
