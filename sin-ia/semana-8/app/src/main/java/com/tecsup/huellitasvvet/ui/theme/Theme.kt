package com.tecsup.huellitasvvet.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.tecsup.huellitasvvet.model.Rol

private val DuenoClaro = lightColorScheme(
    primary = Esmeralda,
    onPrimary = Color.White,
    primaryContainer = EsmeraldaContenedor,
    onPrimaryContainer = Color(0xFF064E3B),
    background = FondoDueno,
    surface = Color.White,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF64748B),
    outlineVariant = Color(0xFFE2E8F0)
)

private val DuenoOscuro = darkColorScheme(
    primary = EsmeraldaOscuro,
    onPrimary = Color(0xFF003824),
    primaryContainer = EsmeraldaContenedorOscuro,
    onPrimaryContainer = EsmeraldaContenedor
)

private val VetClaro = lightColorScheme(
    primary = Azul,
    onPrimary = Color.White,
    primaryContainer = AzulContenedor,
    onPrimaryContainer = Color(0xFF1E3A8A),
    background = FondoVet,
    surface = Color.White,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF64748B),
    outlineVariant = Color(0xFFE2E8F0)
)

private val VetOscuro = darkColorScheme(
    primary = AzulOscuro,
    onPrimary = Color(0xFF0B1F4D),
    primaryContainer = AzulContenedorOscuro,
    onPrimaryContainer = AzulContenedor
)

@Composable
fun VetTheme(
    rol: Rol = Rol.CLIENTE,
    oscuro: Boolean = false,
    content: @Composable () -> Unit
) {
    val esquema = when (rol) {
        Rol.ADMIN -> if (oscuro) VetOscuro else VetClaro
        else -> if (oscuro) DuenoOscuro else DuenoClaro
    }
    MaterialTheme(colorScheme = esquema, typography = Typography, content = content)
}