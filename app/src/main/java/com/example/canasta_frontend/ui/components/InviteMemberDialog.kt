package com.example.canasta_frontend.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.canasta_frontend.data.network.RetrofitClient
import com.example.canasta_frontend.data.repository.InvitationsRepository
import com.example.canasta_frontend.data.repository.InviteResult
import kotlinx.coroutines.launch

@Composable
fun InviteMemberDialog(
    groupId: Long,
    groupName: String,
    onDismiss: () -> Unit,
    onInvited: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val cs = MaterialTheme.colorScheme
    val focusManager = LocalFocusManager.current
    val repo = remember(context) { InvitationsRepository(context) }

    var email by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var sending by remember { mutableStateOf(false) }
    var success by remember { mutableStateOf<String?>(null) }

    fun isValidEmail(v: String) = v.contains("@") && v.contains(".") && v.length >= 5

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(cs.scrim)
            .clickable(enabled = false) {}
            .imePadding()
            .navigationBarsPadding(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp).heightIn(max = 520.dp),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = cs.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, cs.outline)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Surface(color = cs.primary.copy(alpha = 0.15f), shape = RoundedCornerShape(10.dp)) {
                            Icon(Icons.Default.PersonAdd, null, tint = cs.primary, modifier = Modifier.padding(8.dp).size(20.dp))
                        }
                        Column {
                            Text("Invitar al grupo", style = MaterialTheme.typography.titleMedium, color = cs.onBackground)
                            Text(groupName, style = MaterialTheme.typography.bodySmall, color = cs.onSurfaceVariant, maxLines = 1)
                        }
                    }
                    IconButton(onClick = { if (!sending) onDismiss() }) {
                        Icon(Icons.Default.Close, "Cerrar", tint = cs.onSurfaceVariant)
                    }
                }

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it.trim(); error = null; success = null },
                    label = { Text("Email del invitado") },
                    placeholder = { Text("persona@email.com") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                    isError = error != null,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = cs.primary, unfocusedBorderColor = cs.outline,
                        focusedLabelColor = cs.primary, cursorColor = cs.primary,
                        focusedTextColor = cs.onBackground, unfocusedTextColor = cs.onBackground
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                if (error != null) Text(error!!, color = cs.error, style = MaterialTheme.typography.bodySmall)
                if (success != null) Text(success!!, color = cs.primary, style = MaterialTheme.typography.bodySmall)

                Text(
                    "Se envía una invitación pendiente. La persona la acepta desde sus invitaciones recibidas.",
                    style = MaterialTheme.typography.bodySmall, color = cs.onSurfaceVariant
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { if (!sending) onDismiss() },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) { Text("Cancelar") }
                    Button(
                        onClick = {
                            if (!isValidEmail(email)) { error = "Ingresá un email válido"; return@Button }
                            scope.launch {
                                sending = true; error = null; success = null
                                when (val r = repo.invitar(groupId, email)) {
                                    is InviteResult.Success -> {
                                        success = "Invitación enviada a ${email.lowercase().trim()}"
                                        email = ""
                                        onInvited()
                                    }
                                    is InviteResult.Failure -> error = r.message
                                }
                                sending = false
                            }
                        },
                        enabled = !sending,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = cs.primary, contentColor = cs.onPrimary)
                    ) {
                        if (sending) CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp, color = cs.onPrimary)
                        else Text("Enviar", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
