package com.example.canasta_frontend.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@Composable
fun MobileShell(
    currentRoute: String,
    modoGrupo: String = "personal",
    onModoChange: (String) -> Unit = {},
    groupName: String = "Canasta Personal",
    userName: String = "Usuario",
    avatarUrl: String? = null,
    unreadCount: Int = 0,
    onGroupClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onInviteClick: () -> Unit = {},
    onNavClick: (Screen) -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize().statusBarsPadding(),
        topBar = {
            TopHeader(
                modoGrupo = modoGrupo,
                onModoChange = onModoChange,
                groupName = groupName,
                userName = userName,
                avatarUrl = avatarUrl,
                unreadCount = unreadCount,
                onGroupClick = onGroupClick,
                onNotificationClick = onNotificationClick,
                onInviteClick = onInviteClick
            )
        },
        bottomBar = {
            BottomNav(
                currentRoute = currentRoute,
                onItemClick = onNavClick
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            content(paddingValues)
        }
    }
}
