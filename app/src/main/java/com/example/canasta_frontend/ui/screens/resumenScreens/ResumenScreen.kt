package com.example.canasta_frontend.ui.screens.resumenScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.canasta_frontend.ui.components.BentoCompactCard
import com.example.canasta_frontend.ui.components.BentoHeroCard

@Composable
fun ResumenScreen(
    groupName: String = "Canasta Personal",
    modoGrupo: String = "personal",
    hasGrupalGroups: Boolean = true,
    presupuestoTotal: Int = 0,
    totalIngresos: Int = 0,
    totalGastos: Int = 0,
    esenciales: Int = 0,
    primarios: Int = 0,
    secundarios: Int = 0,
    recentActivities: List<ActivityModel> = emptyList(),
    onNavigateToListas: () -> Unit = {},
    onNavigateToOfertas: () -> Unit = {},
    onNavigateToHistorial: () -> Unit = {},
    onCreateGroupClick: () -> Unit = {},
    onInviteClick: () -> Unit = {}
) {
    if (modoGrupo == "grupal" && !hasGrupalGroups) {
        EmptyGrupalState(onCreateGroupClick = onCreateGroupClick)
        return
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Bento Hero Card: Primary Financial Balance + donut disponible/gastado/estimado
        BentoHeroCard(
            groupName = groupName,
            totalIngresos = totalIngresos,
            totalGastos = totalGastos,
            estimadoGastar = esenciales + primarios + secundarios
        )

        // Bento 2-Column Row: Compact KPI Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            BentoCompactCard(
                title = "Compras pendientes",
                valueText = "${esenciales + primarios + secundarios} est.",
                subtitleText = "Ver canastas ›",
                icon = Icons.Default.ShoppingCart,
                modifier = Modifier.weight(1f),
                onClick = onNavigateToListas
            )

            BentoCompactCard(
                title = "Mis grupos",
                valueText = if (hasGrupalGroups) "Grupal + personal" else "1 personal",
                subtitleText = "Gestionar ›",
                icon = Icons.Default.Group,
                modifier = Modifier.weight(1f),
                onClick = onCreateGroupClick
            )
        }

        // Estimaciones por Prioridad (Planificación)
        PriorityEstimationsCard(
            esenciales = esenciales,
            primarios = primarios,
            secundarios = secundarios,
            onNavigateToListas = onNavigateToListas
        )

        // Banners y Colaboración
        OffersBannerCard(
            onNavigateToOfertas = onNavigateToOfertas
        )

        // Movimientos Recientes
        RecentActivityCard(
            recentActivities = recentActivities,
            onNavigateToHistorial = onNavigateToHistorial
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}
