package com.tecsup.huellitasvvet.ui.veterinario

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tecsup.huellitasvvet.ui.components.graficos.DatoGrafico
import com.tecsup.huellitasvvet.ui.components.graficos.GraficoBarras
import com.tecsup.huellitasvvet.ui.components.graficos.GraficoDona
import com.tecsup.huellitasvvet.ui.dueno.fechaLegible
import com.tecsup.huellitasvvet.ui.theme.EstadoAtendida
import com.tecsup.huellitasvvet.ui.theme.EstadoCancelada
import com.tecsup.huellitasvvet.ui.theme.EstadoConfirmada
import com.tecsup.huellitasvvet.ui.theme.EstadoPendiente
import com.tecsup.huellitasvvet.viewmodel.PanelViewModel
import java.util.Locale

private val coloresEstado = listOf(EstadoPendiente, EstadoConfirmada, EstadoAtendida, EstadoCancelada)

@Composable
fun PanelScreen(
    vm: PanelViewModel,
    onVerAgenda: () -> Unit
) {
    val citasHoy by vm.citasHoy.collectAsState()
    val pendientes by vm.pendientes.collectAsState()
    val atendidas by vm.atendidas.collectAsState()
    val ingresos by vm.ingresosMes.collectAsState()
    val porEstado by vm.porEstado.collectAsState()
    val porServicio by vm.porServicio.collectAsState()

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("Panel de la clínica", style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold)
                Text("Hoy · ${fechaLegible(vm.hoy.toString())}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(Icons.Outlined.Notifications, contentDescription = "Notificaciones")
        }
        Spacer(Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Contador("Citas hoy", citasHoy, MaterialTheme.colorScheme.primary, Modifier.weight(1f))
            Contador("Pendientes", pendientes, EstadoPendiente, Modifier.weight(1f))
            Contador("Atendidas", atendidas, EstadoAtendida, Modifier.weight(1f))
        }
        Spacer(Modifier.height(12.dp))
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.Bottom) {
                Column(Modifier.weight(1f)) {
                    Text("Ingresos del mes", style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f))
                    Text(
                        "S/ " + String.format(Locale.US, "%,.2f", ingresos),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
                Text(
                    "$atendidas atenciones",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
        Titulo("Citas por estado")
        Tarjeta {
            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                GraficoDona(
                    datos = porEstado,
                    colores = coloresEstado,
                    textoCentro = porEstado.sumOf { it.valor.toInt() }.toString(),
                    subtituloCentro = "citas"
                )
                Spacer(Modifier.width(20.dp))
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    porEstado.forEachIndexed { i, dato ->
                        FilaLeyenda(dato, coloresEstado[i % coloresEstado.size])
                    }
                }
            }
        }

        Titulo("Citas por servicio")
        Tarjeta {
            GraficoBarras(
                datos = porServicio,
                colorBarra = MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
                colorDestacado = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(16.dp)
            )
        }
        if (pendientes > 0) {
            Spacer(Modifier.height(16.dp))
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = EstadoPendiente.copy(alpha = 0.15f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Warning, contentDescription = null, tint = EstadoPendiente)
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            if (pendientes == 1) "1 cita por confirmar"
                            else "$pendientes citas por confirmar",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = EstadoPendiente
                        )
                        Text("para hoy y mañana", style = MaterialTheme.typography.bodySmall)
                    }
                    Button(
                        onClick = onVerAgenda,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EstadoPendiente,
                            contentColor = Color.White
                        )
                    ) { Text("Ver", fontWeight = FontWeight.Bold) }
                }
            }
        }
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun Contador(titulo: String, valor: Int, color: Color, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = color.copy(alpha = 0.15f),
        modifier = modifier
    ) {
        Column(
            Modifier.padding(vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(valor.toString(), style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold, color = color)
            Text(titulo, style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold, color = color)
        }
    }
}

@Composable
private fun Titulo(texto: String) {
    Text(
        texto,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 20.dp, bottom = 8.dp)
    )
}

@Composable
private fun Tarjeta(contenido: @Composable () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) { contenido() }
}

@Composable
private fun FilaLeyenda(dato: DatoGrafico, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier
                .size(10.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(color)
        )
        Spacer(Modifier.width(8.dp))
        Text(dato.etiqueta, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
        Text(dato.valor.toInt().toString(), style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold)
    }
}