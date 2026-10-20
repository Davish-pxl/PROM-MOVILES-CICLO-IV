package com.tecsup.huellitasvvet.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tecsup.huellitasvvet.model.Especie

private val Cafe = Color(0xFFB7794B)
private val CafeOscuro = Color(0xFF7C4A2D)
private val Crema = Color(0xFFF3E1D0)
private val Naranja = Color(0xFF9CA3AF)
private val NaranjaOscuro = Color(0xFF4B5563)
private val Rosa = Color(0xFFF472B6)
private val Negro = Color(0xFF1F2937)

@Composable
fun MascotaAvatar(
    especie: String,
    modifier: Modifier = Modifier,
    tamano: Dp = 56.dp
) {
    val fondo = MaterialTheme.colorScheme.primaryContainer
    val tinta = MaterialTheme.colorScheme.primary

    Canvas(modifier = modifier.size(tamano)) {
        val r = size.minDimension / 2f
        val c = center
        drawCircle(color = fondo, radius = r, center = c)
        when (especie) {
            Especie.PERRO.name -> dibujarPerro(c, r)
            Especie.GATO.name -> dibujarGato(c, r)
            else -> dibujarHuella(c, r, tinta)
        }
    }
}

private fun DrawScope.dibujarPerro(c: Offset, r: Float) {
    drawOval(CafeOscuro, Offset(c.x - r * 0.72f, c.y - r * 0.45f), Size(r * 0.42f, r * 0.80f))
    drawOval(CafeOscuro, Offset(c.x + r * 0.30f, c.y - r * 0.45f), Size(r * 0.42f, r * 0.80f))

    drawCircle(Cafe, r * 0.50f, Offset(c.x, c.y + r * 0.05f))
    drawOval(Crema, Offset(c.x - r * 0.26f, c.y + r * 0.08f), Size(r * 0.52f, r * 0.36f))

    drawCircle(Negro, r * 0.07f, Offset(c.x - r * 0.18f, c.y - r * 0.08f))
    drawCircle(Negro, r * 0.07f, Offset(c.x + r * 0.18f, c.y - r * 0.08f))
    drawOval(Negro, Offset(c.x - r * 0.09f, c.y + r * 0.12f), Size(r * 0.18f, r * 0.12f))
}

private fun DrawScope.dibujarGato(c: Offset, r: Float) {
    val orejaIzq = Path().apply {
        moveTo(c.x - r * 0.48f, c.y - r * 0.15f)
        lineTo(c.x - r * 0.40f, c.y - r * 0.72f)
        lineTo(c.x - r * 0.08f, c.y - r * 0.38f)
        close()
    }
    val orejaDer = Path().apply {
        moveTo(c.x + r * 0.48f, c.y - r * 0.15f)
        lineTo(c.x + r * 0.40f, c.y - r * 0.72f)
        lineTo(c.x + r * 0.08f, c.y - r * 0.38f)
        close()
    }
    drawPath(orejaIzq, Naranja)
    drawPath(orejaDer, Naranja)
    drawCircle(Naranja, r * 0.50f, Offset(c.x, c.y + r * 0.05f))

    drawOval(Negro, Offset(c.x - r * 0.24f, c.y - r * 0.12f), Size(r * 0.11f, r * 0.18f))
    drawOval(Negro, Offset(c.x + r * 0.13f, c.y - r * 0.12f), Size(r * 0.11f, r * 0.18f))
    val nariz = Path().apply {
        moveTo(c.x - r * 0.07f, c.y + r * 0.10f)
        lineTo(c.x + r * 0.07f, c.y + r * 0.10f)
        lineTo(c.x, c.y + r * 0.19f)
        close()
    }
    drawPath(nariz, Rosa)
    val grosor = r * 0.03f
    listOf(-1f, 1f).forEach { lado ->
        drawLine(NaranjaOscuro, Offset(c.x + lado * r * 0.12f, c.y + r * 0.18f),
            Offset(c.x + lado * r * 0.58f, c.y + r * 0.10f), grosor, StrokeCap.Round)
        drawLine(NaranjaOscuro, Offset(c.x + lado * r * 0.12f, c.y + r * 0.22f),
            Offset(c.x + lado * r * 0.58f, c.y + r * 0.28f), grosor, StrokeCap.Round)
    }
}

private fun DrawScope.dibujarHuella(c: Offset, r: Float, color: Color) {
    drawOval(color, Offset(c.x - r * 0.28f, c.y - r * 0.02f), Size(r * 0.56f, r * 0.44f))
    drawCircle(color, r * 0.12f, Offset(c.x - r * 0.36f, c.y - r * 0.18f))
    drawCircle(color, r * 0.12f, Offset(c.x - r * 0.13f, c.y - r * 0.40f))
    drawCircle(color, r * 0.12f, Offset(c.x + r * 0.13f, c.y - r * 0.40f))
    drawCircle(color, r * 0.12f, Offset(c.x + r * 0.36f, c.y - r * 0.18f))
}
