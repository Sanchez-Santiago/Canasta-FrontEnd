package com.example.canasta_frontend.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Canasta+ Official Color Tokens
val ObsidianBackground = Color(0xFF080A09)
val ObsidianElevated = Color(0xFF0D110F)
val GraphiteSurface = Color(0xFF111613)
val GraphiteSurfaceElevated = Color(0xFF19221C)
val GlassTint = Color(0xFF263A2E)

val EmeraldAccent = Color(0xFF35E887)
val EmeraldPressed = Color(0xFF23C96F)
val EmeraldMuted = Color(0xFF194B31)
val ForestGreen = Color(0xFF123C29)
val GlacierCyan = Color(0xFF38BDF8)

val TextPrimaryDark = Color(0xFFF5F7F5)
val TextSecondaryDark = Color(0xFFA3ACA6)
val TextMutedDark = Color(0xFF747F77)

val GlassBorder = Color(0xFFFFFFFF).copy(alpha = 0.12f)
val GlassBorderSubtle = Color(0xFFFFFFFF).copy(alpha = 0.08f)

val StatusSuccess = Color(0xFF4ADE80)
val StatusWarning = Color(0xFFFBBF24)
val StatusError = Color(0xFFFB7185)

// Backward-Compatibility Aliases
val BackgroundDark = ObsidianBackground
val SurfaceDark = GraphiteSurface
val CardDark = GraphiteSurfaceElevated
val CardBorder = GlassBorder

val ForestEmerald = EmeraldAccent
val ForestEmeraldDark = EmeraldPressed
val SlateSmoked = GlassTint
val WarmAmber = StatusWarning
val MutedCoral = StatusError

val TextPrimary = TextPrimaryDark
val TextSecondary = TextSecondaryDark

// Light Mode Tokens
val BackgroundLight = Color(0xFFF8FAFC)
val SurfaceLight = Color(0xFFFFFFFF)
val CardLight = Color(0xFFF1F5F9)
val CardBorderLight = Color(0xFFE2E8F0)
val TextPrimaryLight = Color(0xFF0F172A)
val TextSecondaryLight = Color(0xFF64748B)
val TextMutedLight = Color(0xFF94A3B8)
val GlassBorderLight = Color(0xFF0F172A).copy(alpha = 0.10f)
val GlassBorderSubtleLight = Color(0xFF0F172A).copy(alpha = 0.06f)
val EmeraldDark = Color(0xFF065F46)
val EmeraldContainerLight = Color(0xFFD1FAE5)

// Official Gradients
val EmeraldDepthGradient = Brush.linearGradient(
    colors = listOf(GraphiteSurface, ForestGreen, Color(0xFF0B110D))
)

val AmbientForestGradient = Brush.linearGradient(
    colors = listOf(ObsidianBackground, Color(0xFF102719), ObsidianBackground)
)

val ActiveEmeraldGradient = Brush.linearGradient(
    colors = listOf(EmeraldAccent, EmeraldPressed)
)

// Light variants (superficies claras con tinte verde discreto)
val LightDepthGradient = Brush.linearGradient(
    colors = listOf(SurfaceLight, EmeraldContainerLight, SurfaceLight)
)

val LightEmeraldGradient = Brush.linearGradient(
    colors = listOf(EmeraldAccent, EmeraldPressed)
)
