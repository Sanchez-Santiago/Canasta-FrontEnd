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
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LightMode
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
    groupName: String = "",
    onToggleTheme: (Boolean) -> Unit = {},
    onInviteClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onGroupChanged: () -> Unit = {},
    onAbandoned: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val groupsRepo = remember(context) { com.example.canasta_frontend.data.repository.GroupsRepository(context) }

    var usuario by remember { mutableStateOf<UsuarioEntity?>(null) }
    var miembros by remember { mutableStateOf<List<MiembroModel>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var showEditName by remember { mutableStateOf(false) }
    var showPassword by remember { mutableStateOf(false) }
    var showEditGroup by remember { mutableStateOf(false) }
    var confirmAbandon by remember { mutableStateOf(false) }
    var expelTarget by remember { mutableStateOf<MiembroModel?>(null) }
    var actionError by remember { mutableStateOf<String?>(null) }
    var savingGroup by remember { mutableStateOf(false) }

    val myRole = miembros.find { it.email.equals(usuario?.email ?: "", ignoreCase = true) }?.rol
    val isAdmin = myRole == "administrador" && activeGroupId != null
    val isGrupal = activeGroupId != null

    fun reloadUser() {
        scope.launch {
            try {
                val api = RetrofitClient.createService(context)
                val user = api.getYo().data?.usuario
                if (user != null) {
                    usuario = user
                    miembros = listOf(MiembroModel(id = user.id_usuario, nombre = user.nombre, email = user.email, rol = "administrador"))
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
                                val id = (mm["id_usuario"] as? Number)?.toLong()
                                MiembroModel(id = id, nombre = n, email = e, rol = r)
                            }
                        }
                    } catch (_: Exception) { }
                }
            } catch (_: Exception) { } finally { isLoading = false }
        }
    }

    LaunchedEffect(activeGroupId) { reloadUser() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        if (isLoading) {
            com.example.canasta_frontend.ui.components.ProfileSkeleton()
        } else {
            PerfilHeader(
                nombre = usuario?.nombre,
                email = usuario?.email,
                localidad = usuario?.localidad ?: usuario?.provincia
            )

            // Members Section — solo grupal (en individual se oculta todo)
            if (isGrupal) {
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
                        Column {
                            Text(text = "Integrantes del Grupo", color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            if (groupName.isNotBlank()) Text(groupName, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp, maxLines = 1)
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            if (isAdmin) {
                                OutlinedButton(
                                    onClick = { showEditGroup = true },
                                    modifier = Modifier.size(40.dp),
                                    contentPadding = PaddingValues(0.dp),
                                    shape = androidx.compose.foundation.shape.CircleShape
                                ) { Icon(imageVector = Icons.Default.Edit, contentDescription = "Editar grupo", modifier = Modifier.size(18.dp)) }
                            }
                            Button(
                                onClick = onInviteClick,
                                modifier = Modifier.size(40.dp),
                                contentPadding = PaddingValues(0.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), contentColor = MaterialTheme.colorScheme.primary),
                                shape = androidx.compose.foundation.shape.CircleShape
                            ) {
                                Icon(imageVector = Icons.Default.PersonAdd, contentDescription = "Invitar integrante", modifier = Modifier.size(18.dp))
                            }
                        }
                    }

                    if (actionError != null) Text(actionError!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)

                    if (miembros.isEmpty()) {
                        Text(text = "No hay integrantes cargados.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            miembros.forEach { miembro ->
                                val isSelf = miembro.email.equals(usuario?.email ?: "", ignoreCase = true)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(modifier = Modifier.weight(1f)) {
                                        PerfilMiembroItem(miembro = miembro)
                                    }
                                    if (isAdmin && !isSelf && miembro.id != null) {
                                        TextButton(onClick = { expelTarget = miembro }) {
                                            Text("Quitar", color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    OutlinedButton(
                        onClick = { confirmAbandon = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(12.dp)
                    ) { Text("Abandonar grupo", fontSize = 13.sp) }
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

                    // Theme Mode Selector — icon-only
                    Text(text = "Apariencia", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("dark" to Icons.Default.DarkMode, "light" to Icons.Default.LightMode).forEach { (modeKey, icon) ->
                            val isSelected = (modeKey == "dark" && isDarkTheme) || (modeKey == "light" && !isDarkTheme)
                            val label = if (modeKey == "dark") "Modo oscuro" else "Modo claro"
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(androidx.compose.foundation.shape.CircleShape)
                                    .background(if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surfaceVariant)
                                    .border(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline, androidx.compose.foundation.shape.CircleShape)
                                    .clickable { onToggleTheme(modeKey == "dark") },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = label,
                                    tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(22.dp)
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

    if (showEditGroup && activeGroupId != null) {
        var nombre by remember { mutableStateOf(groupName) }
        var descripcion by remember { mutableStateOf("") }
        var err by remember { mutableStateOf<String?>(null) }
        AlertDialog(
            onDismissRequest = { if (!savingGroup) showEditGroup = false },
            title = { Text("Editar grupo") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (err != null) Text(err!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                    OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                    OutlinedTextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text("Descripción") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                    Text("Solo el administrador puede editar. El personal no se modifica.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (nombre.trim().length < 2) { err = "Mínimo 2 caracteres"; return@Button }
                    scope.launch {
                        savingGroup = true; err = null
                        when (val r = groupsRepo.updateGrupo(activeGroupId, nombre.trim(), descripcion.trim().ifBlank { "" }, null)) {
                            is com.example.canasta_frontend.data.repository.RepoResult.Ok -> {
                                showEditGroup = false; onGroupChanged()
                                android.widget.Toast.makeText(context, "Grupo actualizado", android.widget.Toast.LENGTH_SHORT).show()
                            }
                            is com.example.canasta_frontend.data.repository.RepoResult.Err -> err = r.message
                        }
                        savingGroup = false
                    }
                }, enabled = !savingGroup) { Text(if (savingGroup) "Guardando..." else "Guardar") }
            },
            dismissButton = { TextButton(onClick = { showEditGroup = false }, enabled = !savingGroup) { Text("Cancelar") } }
        )
    }

    val target = expelTarget
    if (target != null && activeGroupId != null && target.id != null) {
        AlertDialog(
            onDismissRequest = { expelTarget = null },
            title = { Text("Quitar miembro") },
            text = { Text("Se quita a '${target.nombre}' del grupo. Solo admin.") },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        when (val r = groupsRepo.expulsarMiembro(activeGroupId, target.id)) {
                            is com.example.canasta_frontend.data.repository.RepoResult.Ok -> {
                                expelTarget = null; reloadUser(); onGroupChanged()
                                android.widget.Toast.makeText(context, "Miembro eliminado", android.widget.Toast.LENGTH_SHORT).show()
                            }
                            is com.example.canasta_frontend.data.repository.RepoResult.Err -> {
                                actionError = r.message; expelTarget = null
                            }
                        }
                    }
                }) { Text("Quitar", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = { TextButton(onClick = { expelTarget = null }) { Text("Cancelar") } }
        )
    }

    if (confirmAbandon && activeGroupId != null) {
        AlertDialog(
            onDismissRequest = { confirmAbandon = false },
            title = { Text("Abandonar grupo") },
            text = { Text("Salís del grupo '$groupName'. Tus listas de ese grupo dejan de verse. Si sos el último admin, primero eliminá el grupo.") },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        when (val r = groupsRepo.abandonarGrupo(activeGroupId)) {
                            is com.example.canasta_frontend.data.repository.RepoResult.Ok -> {
                                confirmAbandon = false; onAbandoned()
                                android.widget.Toast.makeText(context, "Saliste del grupo", android.widget.Toast.LENGTH_SHORT).show()
                            }
                            is com.example.canasta_frontend.data.repository.RepoResult.Err -> {
                                actionError = r.message; confirmAbandon = false
                            }
                        }
                    }
                }) { Text("Abandonar", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = { TextButton(onClick = { confirmAbandon = false }) { Text("Cancelar") } }
        )
    }
}
