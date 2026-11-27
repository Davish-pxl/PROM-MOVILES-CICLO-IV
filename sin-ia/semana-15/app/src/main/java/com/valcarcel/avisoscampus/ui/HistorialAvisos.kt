package com.valcarcel.avisoscampus.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.valcarcel.avisoscampus.model.Aviso
import com.valcarcel.avisoscampus.model.Origen

@Composable
fun HistorialAvisos(avisos: List<Aviso>) {
    Tarjeta("Historial") {
        if (avisos.isEmpty()) {
            Text(
                text = "Sin avisos todavía",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            avisos.take(20).forEach { aviso ->
                FilaAviso(aviso)
            }
        }
    }
}

@Composable
private fun FilaAviso(aviso: Aviso) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            EtiquetaOrigen(aviso.origen)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = aviso.titulo,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = aviso.texto,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = aviso.hora,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun EtiquetaOrigen(origen: Origen) {
    val (fondo, letra) = when (origen) {
        Origen.BOTON -> Color(0xFF1E88E5) to Color.White
        Origen.RECORDATORIO -> Color(0xFFFFB300) to Color(0xFF3E2723)
        Origen.MODO_AVION -> Color(0xFF7E57C2) to Color.White
    }
    Surface(color = fondo, shape = RoundedCornerShape(50)) {
        Text(
            text = origen.texto,
            color = letra,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}