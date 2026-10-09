package com.tuapp.saludpluscitas.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = AzulPrimario,
    onPrimary = Color.White,
    primaryContainer = TarjetaAzul,
    onPrimaryContainer = AzulPrimario,
    secondary = AzulPrimario,
    onSecondary = Color.White,
    background = FondoBlanco,
    onBackground = TextoPrincipal,
    surface = FondoBlanco,
    onSurface = TextoPrincipal,
    surfaceVariant = SuperficieSuave,
    onSurfaceVariant = TextoSecundario,
    outline = BordeGris,
    outlineVariant = BordeGris
)

private val DarkColorScheme = darkColorScheme(
    primary = AzulPrimario,
    onPrimary = Color.White,
    background = FondoBlanco,
    onBackground = TextoPrincipal,
    surface = FondoBlanco,
    onSurface = TextoPrincipal,
    surfaceVariant = SuperficieSuave,
    onSurfaceVariant = TextoSecundario
)

@Composable
fun ClinicaSaludPlusTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
