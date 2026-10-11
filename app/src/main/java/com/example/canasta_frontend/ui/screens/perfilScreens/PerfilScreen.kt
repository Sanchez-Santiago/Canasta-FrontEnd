package com.example.canasta_frontend.ui.screens.perfilScreens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.canasta_frontend.data.database.entity.UsuarioEntity
import com.example.canasta_frontend.data.network.RetrofitClient
import com.example.canasta_frontend.data.network.UpdatePasswordRequest
import com.example.canasta_frontend.data.network.UpdateProfileRequest
import com.example.canasta_frontend.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun PerfilScreen(
    isDarkTheme: Boolean = true,
    activeGroupId: Long? = null,
    onToggleTheme: (Boolean) -> Unit = {},
    onInviteClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    var usuario by remember { mutableStateOf<UsuarioEntity?>(null) }
    var miembros by remember { mutableStateOf<List<MiembroModel>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var showEditName by remember { mutableStateOf(false) }
    var showPassword by remember { mutableStateOf(false) }

    fun reloadUser() {
        scope.launch {
            try {
                val api = RetrofitClient.createService(context)
                val user = api.getYo().data?.usuario
                if (user != null) {
                    usuario = user
                    miembros = listOf(MiembroModel(user.nombre, user.email, "administrador"))
                }
                if (activeGroupId != null) {
                    try {
                        val m = api.getMiembros(activeGroupId)
                        val list = m.data?.values?.flatten() ?: emptyList()
                        if (list.isNotEmpty()) {
                            miembros = list.mapNotNull { mm ->
                                val n = (mm["nombre"] as? String) ?: (mm["email"] as? String) ?: return@mapNotNull null
                                val e = (mm["email"] as? String) ?: ""
                                val r = (mm["rol"] as? String) ?: "miembro"
                                MiembroModel(n, e, r)
                            }
                        }
                    } catch (_: Exception) { }
                }
            } catch (_: Exception) { } finally { isLoading = false }
        }
    }

    LaunchedEffect(Unit) { reloadUser() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        if (isLoading) {
            Box(modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
            PerfilHeader(
                nombre = usuario?.nombre,
                email = usuario?.email,
                localidad = usuario?.localidad ?: usuario?.provincia
            )

            // Members Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Integrantes del Grupo", color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Button(
                            onClick = onInviteClick,
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(imageVector = Icons.Default.PersonAdd, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
                                Text(text = "Invitar", color = MaterialTheme.colorScheme.primary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    if (miembros.isEmpty()) {
                        Text(text = "No hay integrantes cargados.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            miembros.forEach { miembro ->
                                PerfilMiembroItem(miembro = miembro)
                            }
                        }
                    }
                }
            }

            // Settings Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(text = "Ajustes del Sistema", color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold, fontSize = 14.sp)

                    // Theme Mode Selector
                    Text(text = "Apariencia y Tema", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("dark" to "🌙 Modo Oscuro", "light" to "☀️ Modo Claro").forEach { (modeKey, label) ->
                            val isSelected = (modeKey == "dark" && isDarkTheme) || (modeKey == "light" && !isDarkTheme)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surfaceVariant)
                                    .border(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline, RoundedCornerShape(10.dp))
                                    .clickable { onToggleTheme(modeKey == "dark") }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    OutlinedButton(
                        onClick = { showEditName = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onBackground),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically) {
                            Spacer(modifier = Modifier.width(2.dp))
                            Column {
                                Text(text = "Cambiar nombre y localidad", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                Text(text = usuario?.email ?: "Email no disponible (no editable)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }

                    OutlinedButton(
                        onClick = { showPassword = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onBackground),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically) {
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(text = "Cambiar contraseña", fontSize = 13.sp)
                        }
                    }

                    OutlinedButton(
                        onClick = onNotificationsClick,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onBackground),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.Notifications, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(text = "Centro de Notificaciones", fontSize = 13.sp)
                        }
                    }

                    Button(
                        onClick = onLogout,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.2f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Cerrar Sesión", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }

    if (showEditName) {
        var nombre by remember { mutableStateOf(usuario?.nombre ?: "") }
        var localidad by remember { mutableStateOf(usuario?.localidad ?: "") }
        var err by remember { mutableStateOf<String?>(null) }
        var saving by remember { mutableStateOf(false) }
        AlertDialog(
            onDismissRequest = { if (!saving) showEditName = false },
            confirmButton = {
                Button(onClick = {
                    if (nombre.trim().length < 2) { err = "Nombre mínimo 2 caracteres"; return@Button }
                    scope.launch {
                        saving = true; err = null
                        try {
                            RetrofitClient.createService(context).updateYo(UpdateProfileRequest(nombre = nombre.trim(), localidad = localidad.trim().ifBlank { null }))
                            showEditName = false; isLoading = true; reloadUser()
                        } catch (e: Exception) { err = e.message ?: "No se pudo guardar" } finally { saving = false }
                    }
                }, enabled = !saving) { Text(if (saving) "Guardando..." else "Guardar") }
            },
            dismissButton = { TextButton(onClick = { showEditName = false }) { Text("Cancelar") } },
            title = { Text("Editar perfil") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (err != null) Text(err!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                    OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = localidad, onValueChange = { localidad = it }, label = { Text("Localidad") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    Text("El email no se puede cambiar desde la app (el backend no lo permite).", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        )
    }

    if (showPassword) {
        var actual by remember { mutableStateOf("") }
        var nueva by remember { mutableStateOf("") }
        var confirm by remember { mutableStateOf("") }
        var err by remember { mutableStateOf<String?>(null) }
        var saving by remember { mutableStateOf(false) }
        AlertDialog(
            onDismissRequest = { if (!saving) showPassword = false },
            confirmButton = {
                Button(onClick = {
                    if (nueva.length < 8) { err = "Mínimo 8 caracteres"; return@Button }
                    if (nueva != confirm) { err = "No coinciden"; return@Button }
                    scope.launch {
                        saving = true; err = null
                        try {
                            RetrofitClient.createService(context).updateContrasena(UpdatePasswordRequest(actual, nueva))
                            showPassword = false
                        } catch (e: Exception) { err = e.message ?: "No se pudo cambiar" } finally { saving = false }
                    }
                }, enabled = !saving) { Text(if (saving) "Guardando..." else "Cambiar") }
            },
            dismissButton = { TextButton(onClick = { showPassword = false }) { Text("Cancelar") } },
            title = { Text("Cambiar contraseña") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (err != null) Text(err!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                    OutlinedTextField(value = actual, onValueChange = { actual = it }, label = { Text("Actual") }, singleLine = true, modifier = Modifier.fillMaxWidth(), visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation())
                    OutlinedTextField(value = nueva, onValueChange = { nueva = it }, label = { Text("Nueva (8+)") }, singleLine = true, modifier = Modifier.fillMaxWidth(), visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation())
                    OutlinedTextField(value = confirm, onValueChange = { confirm = it }, label = { Text("Confirmar") }, singleLine = true, modifier = Modifier.fillMaxWidth(), visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation())
                }
            }
        )
    }
}
