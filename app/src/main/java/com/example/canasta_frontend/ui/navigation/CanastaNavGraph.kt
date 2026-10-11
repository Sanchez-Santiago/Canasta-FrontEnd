package com.example.canasta_frontend.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.canasta_frontend.data.database.entity.InvitacionGrupoEntity
import com.example.canasta_frontend.data.network.RetrofitClient
import com.example.canasta_frontend.data.network.SecureTokenManager
import com.example.canasta_frontend.data.preferences.GroupPreferences
import com.example.canasta_frontend.data.preferences.ThemeMode
import com.example.canasta_frontend.data.preferences.ThemePreferences
import com.example.canasta_frontend.data.repository.InvitationsRepository
import com.example.canasta_frontend.ui.components.GroupSelectorModal
import com.example.canasta_frontend.ui.components.InviteMemberDialog
import com.example.canasta_frontend.ui.components.MobileShell
import com.example.canasta_frontend.ui.components.PendingInvitationsDialog
import com.example.canasta_frontend.ui.screens.canastaScreens.CanastaScreen
import com.example.canasta_frontend.ui.screens.historialScreens.HistorialScreen
import com.example.canasta_frontend.ui.screens.loginScreens.LoginScreen
import com.example.canasta_frontend.ui.screens.notificacionesScreens.NotificacionesScreen
import com.example.canasta_frontend.ui.screens.ofertasScreens.OfertasScreen
import com.example.canasta_frontend.ui.screens.perfilScreens.PerfilScreen
import com.example.canasta_frontend.ui.screens.resumenScreens.ActivityModel
import com.example.canasta_frontend.ui.screens.resumenScreens.ResumenScreen
import com.example.canasta_frontend.ui.screens.splashScreens.SplashScreen
import com.example.canasta_frontend.ui.theme.CanastaTheme
import kotlinx.coroutines.launch

@Composable
fun CanastaNavGraph() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val themePreferences = remember { ThemePreferences(context) }
    val groupPreferences = remember { GroupPreferences(context) }
    val savedSelection by groupPreferences.selectionFlow.collectAsState(initial = null)

    val themeMode by themePreferences.themeModeFlow.collectAsState(initial = ThemeMode.SYSTEM)
    val systemDark = isSystemInDarkTheme()
    val isDarkTheme = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> systemDark
    }

    var currentRoute by remember { mutableStateOf("resumen") }
    var modoGrupo by remember { mutableStateOf("personal") }
    var groupName by remember { mutableStateOf("Canasta Personal") }
    var userName by remember { mutableStateOf("Usuario") }
    var activeGroupId by remember { mutableStateOf<Long?>(null) }
    var showGroupSelectorModal by remember { mutableStateOf(false) }
    var showCreateGroupModal by remember { mutableStateOf(false) }
    var showInviteDialog by remember { mutableStateOf(false) }
    var showNotifications by remember { mutableStateOf(false) }
    var unreadCount by remember { mutableIntStateOf(0) }
    var pendingInvites by remember { mutableStateOf<List<InvitacionGrupoEntity>>(emptyList()) }
    var showPendingDialog by remember { mutableStateOf(false) }
    var respondingInviteId by remember { mutableStateOf<Long?>(null) }
    var pendingError by remember { mutableStateOf<String?>(null) }
    var invitesCheckedOnce by remember { mutableStateOf(false) }
    var refreshTrigger by remember { mutableStateOf(0) }
    val invitationsRepo = remember(context) { InvitationsRepository(context) }
    val lifecycleOwner = LocalLifecycleOwner.current

    // Re-chequear al volver a primer plano (cada vez que se abre la app)
    DisposableEffect(lifecycleOwner) {
        val obs = LifecycleEventObserver { _, e ->
            if (e == Lifecycle.Event.ON_RESUME) refreshTrigger++
        }
        lifecycleOwner.lifecycle.addObserver(obs)
        onDispose { lifecycleOwner.lifecycle.removeObserver(obs) }
    }

    // Restaurar última ubicación (individual/grupal + grupo) al arrancar
    var restoredSelection by remember { mutableStateOf(false) }
    LaunchedEffect(savedSelection) {
        val sel = savedSelection
        if (sel != null && !restoredSelection) {
            modoGrupo = sel.modoGrupo
            groupName = sel.groupName
            // activeGroupId se valida contra el server en el efecto de abajo;
            // lo seteamos igual para no parpadear a personal
            if (sel.activeGroupId != null) activeGroupId = sel.activeGroupId
            restoredSelection = true
        }
    }

    fun persistSelection() {
        coroutineScope.launch {
            try { groupPreferences.saveSelection(modoGrupo, activeGroupId, groupName) } catch (_: Exception) { }
        }
    }

    var hasGrupalGroups by remember { mutableStateOf(false) }
    var presupuestoTotal by remember { mutableStateOf(0) }
    var totalIngresos by remember { mutableStateOf(0) }
    var totalGastos by remember { mutableStateOf(0) }
    var esenciales by remember { mutableStateOf(0) }
    var primarios by remember { mutableStateOf(0) }
    var secundarios by remember { mutableStateOf(0) }
    var recentActivities by remember { mutableStateOf<List<ActivityModel>>(emptyList()) }

    // Fetch dynamic user and group data from API
    LaunchedEffect(modoGrupo, activeGroupId, refreshTrigger) {
        try {
            val api = RetrofitClient.createService(context)

            // Get current user info for header & avatar
            val userResp = api.getYo()
            val fetchedName = userResp.data?.usuario?.nombre
            userName = if (!fetchedName.isNullOrBlank()) fetchedName else "Usuario"

            val gruposResp = api.getGrupos()
            val grupos = gruposResp.data?.grupos ?: emptyList()
            hasGrupalGroups = grupos.any { it.tipo == "grupal" }

            val targetGroup = if (activeGroupId != null) {
                grupos.find { it.id_grupo == activeGroupId }
            } else {
                if (modoGrupo == "personal") {
                    grupos.find { it.tipo == "personal" } ?: grupos.firstOrNull()
                } else {
                    grupos.find { it.tipo == "grupal" }
                }
            }

            if (targetGroup != null) {
                groupName = if (!targetGroup.nombre.isNullOrBlank()) targetGroup.nombre else "Canasta Personal"
                activeGroupId = targetGroup.id_grupo
                // cachear ubicación para el próximo arranque
                try { groupPreferences.saveSelection(targetGroup.tipo, targetGroup.id_grupo, groupName) } catch (_: Exception) { }
                presupuestoTotal = targetGroup.presupuesto_mensual?.toInt() ?: 0

                val movResp = api.getMovimientos(targetGroup.id_grupo)
                val movimientos = movResp.data?.movimientos ?: emptyList()
                totalIngresos = movimientos.filter { it.tipo == "ingreso" }.sumOf { it.monto }
                totalGastos = movimientos.filter { it.tipo == "gasto" }.sumOf { it.monto }

                recentActivities = movimientos.sortedByDescending { it.fecha_creacion }.take(5).map { m ->
                    ActivityModel(
                        title = m.nombre,
                        subtitle = m.fecha_movimiento,
                        amount = (if (m.tipo == "ingreso") "+" else "-") + "$${m.monto}",
                        isIngreso = m.tipo == "ingreso"
                    )
                }

                // Calculate estimated list costs by priority
                val listasResp = try { api.getListas(targetGroup.id_grupo) } catch (_: Exception) { null }
                val listas = listasResp?.data?.listas ?: emptyList()
                var es = 0
                var pr = 0
                var se = 0
                listas.filter { it.estado == "activa" }.forEach { l ->
                    val prods = try { api.getProductos(l.id_lista).data?.productos ?: emptyList() } catch (_: Exception) { emptyList() }
                    prods.filter { it.estado == "pendiente" }.forEach { p ->
                        val cost = (p.cantidad * (p.precio_estimado_unitario ?: 0)).toInt()
                        when (p.prioridad) {
                            "esencial" -> es += cost
                            "prioritario" -> pr += cost
                            else -> se += cost
                        }
                    }
                }
                esenciales = es
                primarios = pr
                secundarios = se
            } else {
                groupName = if (modoGrupo == "personal") "Canasta Personal" else "Sin Grupo Grupal"
                presupuestoTotal = 0
                totalIngresos = 0
                totalGastos = 0
                esenciales = 0
                primarios = 0
                secundarios = 0
                recentActivities = emptyList()
            }
        } catch (_: Exception) {
            hasGrupalGroups = false
            presupuestoTotal = 0
            totalIngresos = 0
            totalGastos = 0
            esenciales = 0
            primarios = 0
            secundarios = 0
            recentActivities = emptyList()
        }
    }

    // Badge campana + invitaciones recibidas: se comprueba cada vez que se abre / refresh
    LaunchedEffect(activeGroupId, refreshTrigger) {
        val gid = activeGroupId
        try {
            val api = RetrofitClient.createService(context)
            val n = if (gid != null) {
                try { api.getNotificaciones(gid).data?.values?.flatten()?.size ?: 0 } catch (_: Exception) { 0 }
            } else 0
            val recibidas = try { invitationsRepo.getRecibidas() } catch (_: Exception) { emptyList() }
            pendingInvites = recibidas
            unreadCount = n + recibidas.size
            // Auto-mostrar al abrir si hay pendientes nuevas
            if (recibidas.isNotEmpty() && !invitesCheckedOnce) {
                showPendingDialog = true
                invitesCheckedOnce = true
            }
        } catch (_: Exception) { }
    }

    CanastaTheme(darkTheme = isDarkTheme) {
        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
            NavHost(
                navController = navController,
                startDestination = "splash",
                modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
            ) {
                composable("splash") {
                    SplashScreen(
                        onNavigateToHome = {
                            navController.navigate("main") {
                                popUpTo("splash") { inclusive = true }
                            }
                        },
                        onNavigateToLogin = {
                            navController.navigate("login") {
                                popUpTo("splash") { inclusive = true }
                            }
                        }
                    )
                }

                composable("login") {
                    LoginScreen(
                        onLoginSuccess = {
                            navController.navigate("main") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    )
                }

                composable("main") {
                    var lastBackPress by remember { mutableLongStateOf(0L) }
                    BackHandler(enabled = true) {
                        when {
                            showNotifications -> { showNotifications = false; refreshTrigger++ }
                            showInviteDialog -> showInviteDialog = false
                            showPendingDialog -> { showPendingDialog = false; pendingError = null }
                            showGroupSelectorModal -> showGroupSelectorModal = false
                            showCreateGroupModal -> showCreateGroupModal = false
                            currentRoute != "resumen" -> currentRoute = "resumen"
                            else -> {
                                val now = System.currentTimeMillis()
                                if (now - lastBackPress < 2000L) {
                                    (context as? android.app.Activity)?.moveTaskToBack(true)
                                } else {
                                    lastBackPress = now
                                    android.widget.Toast.makeText(context, "Presioná otra vez para salir", android.widget.Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
                        MobileShell(
                            currentRoute = currentRoute,
                            modoGrupo = modoGrupo,
                            onModoChange = { newMode ->
                                modoGrupo = newMode
                                activeGroupId = null
                                persistSelection()
                            },
                            groupName = if (!groupName.isNullOrBlank()) groupName else "Canasta Personal",
                            userName = if (!userName.isNullOrBlank()) userName else "Usuario",
                            unreadCount = unreadCount,
                            onGroupClick = { showGroupSelectorModal = true },
                            onNotificationClick = { showNotifications = true },
                            onInviteClick = { showInviteDialog = true },
                            onNavClick = { screen ->
                                currentRoute = screen.route
                            }
                        ) { _ ->
                            Crossfade(
                                targetState = currentRoute,
                                animationSpec = tween(durationMillis = 200),
                                label = "TabCrossfade"
                            ) { tab ->
                                when (tab) {
                                    "resumen" -> ResumenScreen(
                                        groupName = if (!groupName.isNullOrBlank()) groupName else "Canasta Personal",
                                        modoGrupo = modoGrupo,
                                        hasGrupalGroups = hasGrupalGroups,
                                        presupuestoTotal = presupuestoTotal,
                                        totalIngresos = totalIngresos,
                                        totalGastos = totalGastos,
                                        esenciales = esenciales,
                                        primarios = primarios,
                                        secundarios = secundarios,
                                        recentActivities = recentActivities,
                                        onNavigateToListas = { currentRoute = "canasta" },
                                        onNavigateToOfertas = { currentRoute = "ofertas" },
                                        onNavigateToHistorial = { currentRoute = "historial" },
                                        onCreateGroupClick = { showCreateGroupModal = true },
                                        onInviteClick = { showInviteDialog = true }
                                    )
                                    "canasta" -> CanastaScreen(
                                        activeGroupId = activeGroupId,
                                        groupName = groupName,
                                        onDataChanged = { refreshTrigger++ }
                                    )
                                    "ofertas" -> OfertasScreen()
                                    "historial" -> HistorialScreen(
                                        activeGroupId = activeGroupId,
                                        onDataChanged = { refreshTrigger++ }
                                    )
                                    "perfil" -> PerfilScreen(
                                        isDarkTheme = isDarkTheme,
                                        activeGroupId = activeGroupId,
                                        onToggleTheme = { dark ->
                                            coroutineScope.launch {
                                                themePreferences.setThemeMode(if (dark) ThemeMode.DARK else ThemeMode.LIGHT)
                                            }
                                        },
                                        onInviteClick = { showInviteDialog = true },
                                        onNotificationsClick = { showNotifications = true },
                                        onLogout = {
                                            SecureTokenManager(context).clearToken()
                                            coroutineScope.launch { try { groupPreferences.clear() } catch (_: Exception) { } }
                                            navController.navigate("login") {
                                                popUpTo("main") { inclusive = true }
                                            }
                                        }
                                    )
                                    else -> ResumenScreen(
                                        groupName = if (!groupName.isNullOrBlank()) groupName else "Canasta Personal",
                                        modoGrupo = modoGrupo,
                                        hasGrupalGroups = hasGrupalGroups,
                                        presupuestoTotal = presupuestoTotal,
                                        totalIngresos = totalIngresos,
                                        totalGastos = totalGastos,
                                        esenciales = esenciales,
                                        primarios = primarios,
                                        secundarios = secundarios,
                                        recentActivities = recentActivities
                                    )
                                }
                            }
                        }

                        if (showGroupSelectorModal) {
                            GroupSelectorModal(
                                currentGroupId = activeGroupId,
                                startInCreate = false,
                                onGroupSelected = { grupo ->
                                    activeGroupId = grupo.id_grupo
                                    groupName = grupo.nombre
                                    modoGrupo = grupo.tipo
                                    hasGrupalGroups = true
                                    refreshTrigger++
                                    persistSelection()
                                },
                                onDismiss = { showGroupSelectorModal = false }
                            )
                        }

                        if (showCreateGroupModal) {
                            GroupSelectorModal(
                                currentGroupId = activeGroupId,
                                startInCreate = true,
                                onGroupSelected = { grupo ->
                                    activeGroupId = grupo.id_grupo
                                    groupName = grupo.nombre
                                    modoGrupo = grupo.tipo
                                    hasGrupalGroups = true
                                    refreshTrigger++
                                    persistSelection()
                                },
                                onDismiss = { showCreateGroupModal = false }
                            )
                        }

                        val inviteGid = activeGroupId
                        if (showInviteDialog && inviteGid != null) {
                            InviteMemberDialog(
                                groupId = inviteGid,
                                groupName = groupName,
                                onDismiss = { showInviteDialog = false },
                                onInvited = { refreshTrigger++ }
                            )
                        }

                        if (showNotifications) {
                            NotificacionesScreen(
                                groupId = activeGroupId,
                                onBack = { showNotifications = false; refreshTrigger++ },
                                onInviteResponded = { refreshTrigger++ }
                            )
                        }

                        if (showPendingDialog && pendingInvites.isNotEmpty()) {
                            PendingInvitationsDialog(
                                invitaciones = pendingInvites,
                                respondingId = respondingInviteId,
                                errorMessage = pendingError,
                                onRespond = { id, aceptar ->
                                    coroutineScope.launch {
                                        respondingInviteId = id
                                        pendingError = null
                                        val inv = pendingInvites.find { it.id_invitacion == id }
                                        when (val r = invitationsRepo.responder(id, aceptar)) {
                                            is com.example.canasta_frontend.data.repository.InviteResult.Success -> {
                                                pendingInvites = pendingInvites.filterNot { it.id_invitacion == id }
                                                if (pendingInvites.isEmpty()) showPendingDialog = false
                                                if (aceptar && inv != null) {
                                                    try {
                                                        val g = RetrofitClient.createService(context).getGrupoById(inv.id_grupo).data?.grupo
                                                        if (g != null) {
                                                            activeGroupId = g.id_grupo
                                                            groupName = g.nombre
                                                            modoGrupo = g.tipo
                                                            persistSelection()
                                                        }
                                                    } catch (_: Exception) { }
                                                    android.widget.Toast.makeText(context, "Te uniste al grupo #${inv.id_grupo}", android.widget.Toast.LENGTH_SHORT).show()
                                                } else {
                                                    android.widget.Toast.makeText(context, if (aceptar) "Invitación aceptada" else "Invitación rechazada", android.widget.Toast.LENGTH_SHORT).show()
                                                }
                                                refreshTrigger++
                                            }
                                            is com.example.canasta_frontend.data.repository.InviteResult.Failure -> {
                                                pendingError = r.message
                                            }
                                        }
                                        respondingInviteId = null
                                    }
                                },
                                onDismiss = { showPendingDialog = false; pendingError = null },
                                onSeeAll = { showPendingDialog = false; showNotifications = true }
                            )
                        }
                    }
                }
            }
        }
    }
}
