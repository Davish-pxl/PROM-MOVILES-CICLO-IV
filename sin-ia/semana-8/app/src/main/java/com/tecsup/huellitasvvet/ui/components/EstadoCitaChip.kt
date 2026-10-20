package com.tecsup.huellitasvvet.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tecsup.huellitasvvet.model.EstadoCita
import com.tecsup.huellitasvvet.ui.theme.EstadoAtendida
import com.tecsup.huellitasvvet.ui.theme.EstadoCancelada
import com.tecsup.huellitasvvet.ui.theme.EstadoConfirmada
import com.tecsup.huellitasvvet.ui.theme.EstadoPendiente

@Composable
fun EstadoCitaChip(estado: String, modifier: Modifier = Modifier) {
    val (color, texto) = when (estado) {
        EstadoCita.PENDIENTE.name -> EstadoPendiente to "Pendiente"
        EstadoCita.CONFIRMADA.name -> EstadoConfirmada to "Confirmada"
        EstadoCita.ATENDIDA.name -> EstadoAtendida to "Atendida"
        else -> EstadoCancelada to "Cancelada"
    }
    Surface(
        shape = RoundedCornerShape(50),
        color = color.copy(alpha = 0.15f),
        modifier = modifier
    ) {
        Text(
            texto,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = color,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
        )
    }
}