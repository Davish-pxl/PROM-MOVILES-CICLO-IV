package com.tecsup.huellitasvvet.ui.components.graficos

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun GraficoBarras(
    datos: List<DatoGrafico>,
    colorBarra: Color,
    colorDestacado: Color,
    modifier: Modifier = Modifier,
    altoMaximo: Dp = 120.dp
) {
    if (datos.isEmpty()) {
        Box(modifier.fillMaxWidth().height(altoMaximo), contentAlignment = Alignment.Center) {
            Text("Sin datos", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        return
    }
    val maximo = datos.maxOf { it.valor }.coerceAtLeast(1f)

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        datos.forEach { dato ->
            val esMaximo = dato.valor == maximo
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    dato.valor.toInt().toString(),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                val alto = (altoMaximo * (dato.valor / maximo)).coerceAtLeast(4.dp)
                Canvas(
                    Modifier
                        .width(48.dp)
                        .height(alto)
                ) {
                    drawRoundRect(
                        color = if (esMaximo) colorDestacado else colorBarra,
                        cornerRadius = CornerRadius(6.dp.toPx())
                    )
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    dato.etiqueta,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}