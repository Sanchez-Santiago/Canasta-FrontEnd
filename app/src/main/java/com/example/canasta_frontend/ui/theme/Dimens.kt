package com.example.canasta_frontend.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Canasta+ spacing / radii tokens — Bento Grid spec
data class CanastaSpacing(
    val xs: Dp = 4.dp,
    val sm: Dp = 8.dp,
    val md: Dp = 12.dp,
    val lg: Dp = 16.dp,
    val xl: Dp = 20.dp,
    val xxl: Dp = 24.dp,
    val xxxl: Dp = 32.dp
)

data class CanastaRadii(
    val sm: Dp = 10.dp,
    val md: Dp = 16.dp,
    val lg: Dp = 22.dp,
    val xl: Dp = 24.dp
)

val LocalCanastaSpacing = staticCompositionLocalOf { CanastaSpacing() }
val LocalCanastaRadii = staticCompositionLocalOf { CanastaRadii() }
