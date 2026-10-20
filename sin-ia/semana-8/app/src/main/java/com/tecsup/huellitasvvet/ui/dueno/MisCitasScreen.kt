package com.tecsup.huellitasvvet.ui.dueno

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tecsup.huellitasvvet.data.local.relation.CitaConDetalle
import com.tecsup.huellitasvvet.ui.components.AppTopBar
import com.tecsup.huellitasvvet.ui.components.CitaCard
import com.tecsup.huellitasvvet.ui.theme.EstadoPendiente
import com.tecsup.huellitasvvet.viewmodel.MisCitasViewModel

@Composable
fun MisCitasScreen(
    vm: MisCitasViewModel,
    onAtras: () -> Unit,
    onPedirCita: () -> Unit
) {
    val proximas by vm.proximas.collectAsState()
    val historial by vm.historial.collectAsState()
    val mensaje by vm.mensaje.collectAsState()

    var pestana by rememberSaveable { mutableIntStateOf(0) }
    var citaACancelar by remember { mutableStateOf<CitaConDetalle?>(null) }
    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(mensaje) {
        mensaje?.let {
            snackbar.showSnackbar(it)
            vm.limpiarMensaje()
        }
    }

    Scaffold(
        topBar = { AppTopBar("Mis citas", onAtras) },
        snackbarHost = { SnackbarHost(snackbar) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onPedirCita,
                shape = RoundedCornerShape(16.dp),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Pedir una cita")
            }
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(8.dp))
            SelectorPestanas(pestana) { pestana = it }
            Spacer(Modifier.height(16.dp))

            val lista = if (pestana == 0) proximas else historial
            if (lista.isEmpty()) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        if (pestana == 0) "No tienes citas próximas"
                        else "Aún no hay citas en tu historial",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (pestana == 0) AvisoCancelacion()
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 96.dp)
                ) {
                    items(lista, key = { it.cita.id }) { c ->
                        CitaCard(
                            c = c,
                            onCancelar = if (pestana == 0) {
                                {
                                    if (vm.puedeCancelar(c.cita)) citaACancelar = c
                                    else vm.cancelar(c.cita)
                                }
                            } else null
                        )
                    }
                    if (pestana == 0) {
                        item { AvisoCancelacion() }
                    }
                }
            }
        }
    }

    citaACancelar?.let { c ->
        AlertDialog(
            onDismissRequest = { citaACancelar = null },
            title = { Text("Cancelar cita") },
            text = {
                Text(
                    "¿Seguro que deseas cancelar ${c.servicio.nombre} de ${c.mascota.mascota.nombre} " +
                            "el ${fechaLegible(c.cita.fecha)} a las ${c.cita.hora}?"
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    vm.cancelar(c.cita)
                    citaACancelar = null
                }) {
                    Text("Sí, cancelar", color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { citaACancelar = null }) { Text("No") }
            }
        )
    }
}

@Composable
private fun AvisoCancelacion() {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = EstadoPendiente.copy(alpha = 0.12f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Outlined.Schedule, contentDescription = null,
                tint = EstadoPendiente, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(10.dp))
            Text("Puedes cancelar hasta 2 horas antes",
                style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun SelectorPestanas(seleccionada: Int, onCambiar: (Int) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(4.dp)
    ) {
        listOf("Próximas", "Historial").forEachIndexed { i, texto ->
            val elegida = i == seleccionada
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (elegida) MaterialTheme.colorScheme.surface else Color.Transparent)
                    .clickable { onCambiar(i) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    texto,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = if (elegida) FontWeight.Bold else FontWeight.Medium,
                    color = if (elegida) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}