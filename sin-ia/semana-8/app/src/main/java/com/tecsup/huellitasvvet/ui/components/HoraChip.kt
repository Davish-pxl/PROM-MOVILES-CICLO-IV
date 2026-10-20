package com.tecsup.huellitasvvet.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp

@Composable
fun HoraChip(
    hora: String,
    seleccionada: Boolean,
    ocupada: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cs = MaterialTheme.colorScheme
    val forma = RoundedCornerShape(12.dp)
    val fondo = when {
        ocupada -> cs.surfaceVariant
        seleccionada -> cs.primary
        else -> cs.surface
    }
    val borde = when {
        ocupada -> cs.surfaceVariant
        seleccionada -> cs.primary
        else -> cs.outlineVariant
    }
    val colorTexto = when {
        ocupada -> cs.onSurfaceVariant.copy(alpha = 0.5f)
        seleccionada -> cs.onPrimary
        else -> cs.onSurface
    }

    Box(
        modifier = modifier
            .height(44.dp)
            .clip(forma)
            .background(fondo)
            .border(1.dp, borde, forma)
            .clickable(enabled = !ocupada, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            hora,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = if (seleccionada) FontWeight.Bold else FontWeight.Medium,
            color = colorTexto,
            textDecoration = if (ocupada) TextDecoration.LineThrough else null
        )
    }
}