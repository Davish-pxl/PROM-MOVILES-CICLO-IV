package com.valcarcel.centrotareas.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.valcarcel.centrotareas.model.CargaUi
import com.valcarcel.centrotareas.model.PrimosUi
import com.valcarcel.centrotareas.viewmodel.TareasViewModel
private val VerdeFondo = Color(0xFFE6F4EA)
private val VerdeTexto = Color(0xFF1B5E20)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaTareas(vm: TareasViewModel) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("CentroTareas", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TarjetaTareaLarga(vm)
            TarjetaPrimos(vm)
            TarjetaDosCargas(vm)
        }
    }
}
@Composable
fun TarjetaTareaLarga(vm: TareasViewModel) {
    val mensaje by vm.mensaje.collectAsState()
    val progreso by vm.progreso.collectAsState()
    val corriendo by vm.corriendo.collectAsState()
    var toques by rememberSaveable { mutableIntStateOf(0) }

    TarjetaGris {
        TituloTarjeta("Tarea con progreso")

        Button(onClick = { toques++ }) {
            Text("Tócame: $toques")
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { vm.tareaLarga() },
                enabled = !corriendo
            ) {
                Text("Iniciar")
            }
            OutlinedButton(
                onClick = { vm.cancelar() },
                enabled = corriendo
            ) {
                Text("Cancelar")
            }
        }

        LinearProgressIndicator(
            progress = { progreso },
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = "${(progreso * 100).toInt()} %",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(text = mensaje)
    }
}
@Composable
fun TarjetaPrimos(vm: TareasViewModel) {
    val primos by vm.primos.collectAsState()
    var texto by rememberSaveable { mutableStateOf("500000") }
    var ultimoN by rememberSaveable { mutableIntStateOf(0) }

    val calculando = primos is PrimosUi.Calculando
    val numero = texto.toIntOrNull()
    val numeroValido = numero != null && numero >= 2

    TarjetaGris {
        TituloTarjeta("Contar números primos")

        OutlinedTextField(
            value = texto,
            onValueChange = { nuevo ->
                if (nuevo.length <= 9 && nuevo.all { it.isDigit() }) texto = nuevo
            },
            label = { Text("Hasta el número") },
            singleLine = true,
            enabled = !calculando,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = {
                    if (numero != null && numero >= 2) {
                        ultimoN = numero
                        vm.contar(numero)
                    }
                },
                enabled = !calculando && numeroValido
            ) {
                Text("Contar")
            }
            OutlinedButton(
                onClick = { vm.cancelarPrimos() },
                enabled = calculando
            ) {
                Text("Cancelar")
            }
        }

        when (val estado = primos) {
            PrimosUi.Inicial -> Unit
            PrimosUi.Calculando -> Text("Calculando...")
            PrimosUi.Cancelado -> Text("Cancelado")
            is PrimosUi.Listo -> RecuadroResultado(
                "Primos hasta $ultimoN: ${estado.cantidad}",
                "Tiempo: ${estado.milisegundos} ms"
            )
        }
    }
}
@Composable
fun TarjetaDosCargas(vm: TareasViewModel) {
    val carga by vm.carga.collectAsState()
    val simularError by vm.simularError.collectAsState()
    val historial by vm.historialTiempos.collectAsState()
    val promediosPorModo by vm.promediosPorModo.collectAsState()
    val cargando = carga is CargaUi.Cargando

    TarjetaGris {
        TituloTarjeta("Dos cargas")

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Switch(
                checked = simularError,
                onCheckedChange = { vm.cambiarSimularError(it) }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Simular error",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(
                onClick = { vm.cargarUnaPorUna() },
                enabled = !cargando
            ) {
                Text("Una por una")
            }
            Button(
                onClick = { vm.cargarALaVez() },
                enabled = !cargando
            ) {
                Text("A la vez")
            }
        }

        when (val estado = carga) {
            CargaUi.Inicial -> Unit
            CargaUi.Cargando -> CircularProgressIndicator()
            is CargaUi.Exito -> RecuadroResultado(
                "Modo: ${estado.modo}",
                "Perfil: ${estado.perfil}",
                "Pedidos: ${estado.pedidos}",
                "Tiempo: ${estado.milisegundos} ms"
            )
            is CargaUi.Error -> {
                RecuadroError(estado.mensaje)
                OutlinedButton(onClick = { vm.cargarALaVez() }) {
                    Text("Reintentar")
                }
            }
        }

        if (historial.isNotEmpty()) {
            Text(
                text = "Comparación de tiempos",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "N",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.weight(0.5f),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Modo",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.weight(2f),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Tiempo (ms)",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.weight(1.5f),
                    textAlign = TextAlign.End
                )
            }

            historial.forEachIndexed { index, registro ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${index + 1}",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.weight(0.5f),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = registro.modo,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.weight(2f),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "${registro.milisegundos}",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.weight(1.5f),
                        textAlign = TextAlign.End
                    )
                }
            }

            promediosPorModo["Una por una"]?.let { promUna ->
                Text(
                    text = "Promedio Una por una: $promUna ms",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            promediosPorModo["A la vez"]?.let { promVez ->
                Text(
                    text = "Promedio A la vez: $promVez ms",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            TextButton(
                onClick = { vm.limpiarHistorial() }
            ) {
                Text("Limpiar historial")
            }
        }
    }
}
@Composable
private fun TarjetaGris(contenido: @Composable () -> Unit) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            contenido()
        }
    }
}

@Composable
private fun TituloTarjeta(texto: String) {
    Text(
        text = texto,
        modifier = Modifier.fillMaxWidth(),
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
private fun RecuadroResultado(titulo: String, vararg detalles: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = VerdeFondo
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = titulo, fontWeight = FontWeight.Bold, color = VerdeTexto)
            detalles.forEach { detalle ->
                Text(
                    text = detalle,
                    style = MaterialTheme.typography.bodySmall,
                    color = VerdeTexto
                )
            }
        }
    }
}

@Composable
private fun RecuadroError(mensaje: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.errorContainer
    ) {
        Text(
            text = mensaje,
            modifier = Modifier.padding(12.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onErrorContainer,
            textAlign = TextAlign.Center
        )
    }
}