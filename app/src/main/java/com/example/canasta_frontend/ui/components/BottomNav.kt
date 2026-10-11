package com.example.canasta_frontend.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.canasta_frontend.ui.theme.*

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Resumen : Screen("resumen", "Resumen", Icons.Default.Wallet)
    object Canasta : Screen("canasta", "Canasta", Icons.Default.ShoppingCart)
    object Ofertas : Screen("ofertas", "Ofertas", Icons.Default.LocalOffer)
    object Historial : Screen("historial", "Movimientos", Icons.Default.Receipt)
    object Perfil : Screen("perfil", "Perfil", Icons.Default.Person)
}

@Composable
fun BottomNav(
    currentRoute: String,
    onItemClick: (Screen) -> Unit
) {
    val items = listOf(
        Screen.Resumen,
        Screen.Canasta,
        Screen.Ofertas,
        Screen.Historial,
        Screen.Perfil
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.95f))
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), CircleShape)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { screen ->
                val selected = currentRoute == screen.route
                val tintColor by animateColorAsState(
                    targetValue = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    animationSpec = tween(durationMillis = 200),
                    label = "NavColor"
                )

                Column(
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable { onItemClick(screen) }
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else Color.Transparent)
                            .border(1.dp, if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.4f) else Color.Transparent, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = screen.icon,
                            contentDescription = screen.title,
                            tint = tintColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Text(
                        text = screen.title,
                        color = tintColor,
                        fontSize = 9.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF080A09)
@Composable
fun BottomNavPreview() {
    CanastaTheme(darkTheme = true) {
        Box(modifier = Modifier.padding(16.dp)) {
            BottomNav(
                currentRoute = "resumen",
                onItemClick = {}
            )
        }
    }
}
