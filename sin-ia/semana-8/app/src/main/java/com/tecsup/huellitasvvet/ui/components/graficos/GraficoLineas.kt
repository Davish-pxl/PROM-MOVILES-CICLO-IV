package com.tecsup.huellitasvvet.ui.components.graficos

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun GraficoLineas(
    datos: List<DatoGrafico>,
    color: Color,
    modifier: Modifier = Modifier
) {
    if (datos.size < 2) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(140.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Se necesitan al menos 2 atenciones para ver la evolución",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
        return
    }

    val textMeasurer = rememberTextMeasurer()
    val labelColor = MaterialTheme.colorScheme.onSurfaceVariant
    val textColor = MaterialTheme.colorScheme.onSurface
    val surfaceColor = MaterialTheme.colorScheme.surface
    val labelStyle = MaterialTheme.typography.labelSmall.copy(color = labelColor)
    val valueStyle = MaterialTheme.typography.labelMedium.copy(
        color = textColor,
        fontWeight = FontWeight.Bold
    )

    Canvas(modifier = modifier) {
        val minValor = datos.minOf { it.valor }
        val maxValor = datos.maxOf { it.valor }
        val rango = (maxValor - minValor).let { if (it == 0f) 1f else it }

        val paddingTop = 28.dp.toPx()
        val paddingBottom = 28.dp.toPx()
        val paddingLeft = 32.dp.toPx()
        val paddingRight = 32.dp.toPx()

        val areaWidth = size.width - paddingLeft - paddingRight
        val areaHeight = size.height - paddingTop - paddingBottom

        val puntos = datos.mapIndexed { index, dato ->
            val x = paddingLeft + (index.toFloat() / (datos.size - 1)) * areaWidth
            val y = if (maxValor == minValor) {
                paddingTop + areaHeight / 2f
            } else {
                paddingTop + areaHeight - ((dato.valor - minValor) / rango) * areaHeight
            }
            Offset(x, y)
        }

        val path = Path().apply {
            moveTo(puntos.first().x, puntos.first().y)
            for (i in 1 until puntos.size) {
                lineTo(puntos[i].x, puntos[i].y)
            }
        }
        drawPath(
            path = path,
            color = color,
            style = Stroke(
                width = 3.dp.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        puntos.forEachIndexed { i, pt ->
            val dato = datos[i]

            drawCircle(color = color, radius = 6.dp.toPx(), center = pt)
            drawCircle(color = surfaceColor, radius = 3.dp.toPx(), center = pt)

            val valTexto = if (dato.valor % 1f == 0f) "${dato.valor.toInt()} kg" else "${dato.valor} kg"
            val valResult = textMeasurer.measure(valTexto, style = valueStyle)
            drawText(
                textLayoutResult = valResult,
                topLeft = Offset(
                    pt.x - valResult.size.width / 2f,
                    pt.y - valResult.size.height - 6.dp.toPx()
                )
            )

            val tagResult = textMeasurer.measure(dato.etiqueta, style = labelStyle)
            drawText(
                textLayoutResult = tagResult,
                topLeft = Offset(
                    pt.x - tagResult.size.width / 2f,
                    pt.y + 8.dp.toPx()
                )
            )
        }
    }
}
