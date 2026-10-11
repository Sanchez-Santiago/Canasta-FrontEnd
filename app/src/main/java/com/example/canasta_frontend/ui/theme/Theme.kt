package com.example.canasta_frontend.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = EmeraldAccent,
    onPrimary = ObsidianBackground,
    primaryContainer = ForestGreen,
    onPrimaryContainer = EmeraldAccent,
    secondary = GlacierCyan,
    onSecondary = ObsidianBackground,
    tertiary = StatusWarning,
    onTertiary = ObsidianBackground,
    background = ObsidianBackground,
    onBackground = TextPrimaryDark,
    surface = GraphiteSurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = GraphiteSurfaceElevated,
    onSurfaceVariant = TextSecondaryDark,
    surfaceContainerLowest = ObsidianBackground,
    surfaceContainerLow = ObsidianElevated,
    surfaceContainer = GraphiteSurface,
    surfaceContainerHigh = GraphiteSurfaceElevated,
    outline = GlassBorder,
    outlineVariant = GlassBorderSubtle,
    error = StatusError,
    errorContainer = Color(0xFF4C1D1D),
    onError = TextPrimaryDark,
    onErrorContainer = StatusError,
    scrim = Color.Black.copy(alpha = 0.6f)
)

private val LightColorScheme = lightColorScheme(
    primary = EmeraldPressed,
    onPrimary = Color.White,
    primaryContainer = EmeraldContainerLight,
    onPrimaryContainer = EmeraldDark,
    secondary = Color(0xFF0284C7),
    onSecondary = Color.White,
    tertiary = StatusWarning,
    onTertiary = Color.White,
    background = BackgroundLight,
    onBackground = TextPrimaryLight,
    surface = SurfaceLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = CardLight,
    onSurfaceVariant = TextSecondaryLight,
    surfaceContainerLowest = SurfaceLight,
    surfaceContainerLow = BackgroundLight,
    surfaceContainer = CardLight,
    surfaceContainerHigh = Color(0xFFE8EEF3),
    outline = CardBorderLight,
    outlineVariant = GlassBorderSubtleLight,
    error = StatusError,
    errorContainer = Color(0xFFFFE4E6),
    onError = Color.White,
    onErrorContainer = Color(0xFF881337),
    scrim = Color(0xFF0F172A).copy(alpha = 0.4f)
)

@Composable
fun CanastaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = android.graphics.Color.TRANSPARENT
            window.navigationBarColor = android.graphics.Color.TRANSPARENT
            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = !darkTheme
            controller.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
