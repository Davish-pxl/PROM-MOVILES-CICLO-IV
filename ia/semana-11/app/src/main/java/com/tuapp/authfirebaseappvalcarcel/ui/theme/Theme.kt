package com.tuapp.authfirebaseappvalcarcel.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = TecsupAccent,
    onPrimary = Color.Black,
    primaryContainer = TecsupDarkBlue,
    onPrimaryContainer = TecsupLightBlue,
    secondary = TecsupBlue,
    background = Color(0xFF0F172A),
    surface = Color(0xFF1E293B),
    onSurface = Color.White,
    onBackground = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = TecsupBlue,
    onPrimary = Color.White,
    primaryContainer = TecsupLightBlue,
    onPrimaryContainer = TecsupDarkBlue,
    secondary = TecsupAccent,
    background = BackgroundLight,
    surface = CardBackground,
    onSurface = TextPrimary,
    onBackground = TextPrimary,
    surfaceVariant = SurfaceLight,
    onSurfaceVariant = TextSecondary
)

@Composable
fun AuthFireBaseAppValcarcelTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content
    )
}
