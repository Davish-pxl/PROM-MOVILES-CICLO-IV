package com.tuapp.valcarcelcomida.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val ColoresClaros = lightColorScheme(
    primary = Pimenton,
    onPrimary = SobrePimenton,
    primaryContainer = PimentonSuave,
    onPrimaryContainer = SobrePimentonSuave,
    secondary = Canela,
    secondaryContainer = CanelaSuave,
    onSecondaryContainer = SobreCanelaSuave,
    tertiary = Oliva,
    tertiaryContainer = OlivaSuave,
    onTertiaryContainer = SobreOlivaSuave,
    background = Crema,
    onBackground = SobreCrema,
    surface = Crema,
    onSurface = SobreCrema,
    surfaceVariant = CremaVariante,
    onSurfaceVariant = SobreCremaVariante,
    outline = Contorno
)

private val ColoresOscuros = darkColorScheme(
    primary = PimentonOscuro,
    onPrimary = SobrePimentonOscuro,
    primaryContainer = PimentonSuaveOscuro,
    onPrimaryContainer = SobrePimentonSuaveOscuro,
    secondary = CanelaOscuro,
    secondaryContainer = CanelaSuaveOscuro,
    onSecondaryContainer = SobreCanelaSuaveOscuro,
    tertiary = OlivaOscuro,
    tertiaryContainer = OlivaSuaveOscuro,
    onTertiaryContainer = SobreOlivaSuaveOscuro,
    background = FondoOscuro,
    onBackground = SobreFondoOscuro,
    surface = FondoOscuro,
    onSurface = SobreFondoOscuro,
    surfaceVariant = VarianteOscuro,
    onSurfaceVariant = SobreVarianteOscuro,
    outline = ContornoOscuro
)

private val Formas = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

@Composable
fun ValcarcelComidaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) ColoresOscuros else ColoresClaros,
        typography = Typography,
        shapes = Formas,
        content = content
    )
}