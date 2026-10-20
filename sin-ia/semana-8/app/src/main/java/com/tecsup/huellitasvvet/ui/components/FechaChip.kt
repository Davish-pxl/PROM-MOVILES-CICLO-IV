package com.tecsup.huellitasvvet.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.LocalDate

private val DIAS_CORTOS = listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom")

@Composable
fun FechaChip(
    fecha: LocalDate,
    seleccionada: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cs = MaterialTheme.colorScheme
    val forma = RoundedCornerShape(14.dp)
    val colorTexto = if (seleccionada) cs.onPrimary else cs.onSurface

    Column(
        modifier = modifier
            .width(60.dp)
            .clip(forma)
            .background(if (seleccionada) cs.primary else cs.surface)
            .border(1.dp, if (seleccionada) cs.primary else cs.outlineVariant, forma)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            DIAS_CORTOS[fecha.dayOfWeek.value - 1],
            style = MaterialTheme.typography.labelMedium,
            color = if (seleccionada) cs.onPrimary else cs.onSurfaceVariant
        )
        Text(
            fecha.dayOfMonth.toString(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = colorTexto
        )
    }
}