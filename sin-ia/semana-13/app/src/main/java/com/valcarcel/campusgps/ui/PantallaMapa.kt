package com.valcarcel.campusgps.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.valcarcel.campusgps.model.UbicacionUi
import com.valcarcel.campusgps.util.formatearDistancia
import com.valcarcel.campusgps.viewmodel.MapaViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaMapa(vm: MapaViewModel) {
    val estado by vm.estado.collectAsState()
    val lugares by vm.lugares.collectAsState()
    val seleccionado by vm.seleccionado.collectAsState()
    val masCercano by vm.masCercano.collectAsState()

    val contexto = LocalContext.current
    val lanzador = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { resultado ->
        if (resultado.values.any { it }) {
            vm.buscar()
        } else {
            vm.permisoDenegado()
        }
    }

    fun alPulsar() {
        val tieneFine = ContextCompat.checkSelfPermission(
            contexto, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val tieneCoarse = ContextCompat.checkSelfPermission(
            contexto, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (tieneFine || tieneCoarse) {
            vm.buscar()
        } else {
            lanzador.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("CampusGPS") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        when (val e = estado) {
            is UbicacionUi.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    BotonUbicacion(
                        habilitado = true,
                        onClick = { alPulsar() }
                    )
                    Spacer(Modifier.height(16.dp))
                    MensajeError(
                        error = e,
                        onReintentar = { alPulsar() },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            else -> {
                val cercano = masCercano

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    MapaUbicacion(
                        estado = e,
                        lugares = lugares,
                        seleccionado = seleccionado,
                        masCercanoId = cercano?.id,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        BotonUbicacion(
                            habilitado = e !is UbicacionUi.Buscando,
                            onClick = { alPulsar() }
                        )
                        Spacer(Modifier.height(6.dp))

                        OutlinedButton(
                            onClick = { cercano?.let { vm.seleccionar(it.id) } },
                            enabled = cercano != null
                        ) {
                            Text("Ir al más cercano")
                        }
                        if (cercano != null) {
                            val distancia = lugares
                                .firstOrNull { it.lugar.id == cercano.id }
                                ?.metros
                                ?.let { formatearDistancia(it) }
                                ?: ""
                            Text(
                                text = buildAnnotatedString {
                                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                        append("Más cercano: ")
                                    }
                                    append("${cercano.nombre} ($distancia)")
                                },
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        if (e is UbicacionUi.Lista) {
                            Text(
                                text = "Latitud: ${String.format(Locale.US, "%.4f", e.posicion.latitud)}   " +
                                        "Longitud: ${String.format(Locale.US, "%.4f", e.posicion.longitud)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        ListaLugares(
                            lugares = lugares,
                            seleccionado = seleccionado,
                            onClick = { id -> vm.seleccionar(id) },
                            masCercanoId = cercano?.id,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BotonUbicacion(habilitado: Boolean, onClick: () -> Unit) {
    Button(onClick = onClick, enabled = habilitado) {
        Text("Obtener mi ubicación")
    }
}