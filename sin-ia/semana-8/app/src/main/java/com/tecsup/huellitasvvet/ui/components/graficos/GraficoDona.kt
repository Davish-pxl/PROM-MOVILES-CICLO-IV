package com.tecsup.huellitasvvet.ui.components.graficos

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
data class DatoGrafico(
    val etiqueta: String,
    val valor: Float
)
@Composable
fun GraficoDona(
    datos: List<DatoGrafico>,
    colores: List<Color>,
    modifier: Modifier = Modifier,
    tamano: Dp = 120.dp,
    grosor: Dp = 18.dp,
    textoCentro: String? = null,
    subtituloCentro: String? = null
) {
    val total = datos.sumOf { it.valor.toDouble() }.toFloat()
    val colorVacio = MaterialTheme.colorScheme.surfaceVariant

    Box(modifier.size(tamano), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val g = grosor.toPx()
            val diametro = size.minDimension - g
            val esquina = Offset((size.width - diametro) / 2f, (size.height - diametro) / 2f)
            val area = Size(diametro, diametro)

            if (total <= 0f) {
                drawArc(colorVacio, 0f, 360f, false, esquina, area, style = Stroke(g))
            } else {
                var inicio = -90f
                datos.forEachIndexed { i, dato ->
                    val barrido = 360f * dato.valor / total
                    if (barrido > 0f) {
                        drawArc(colores[i % colores.size], inicio, barrido, false,
                            esquina, area, style = Stroke(g))
                    }
                    inicio += barrido
                }
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            textoCentro?.let {
                Text(it, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }
            subtituloCentro?.let {
                Text(it, style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}