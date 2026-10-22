package com.tecsup.huellitasvvet.ui.dueno

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tecsup.huellitasvvet.model.Horarios
import com.tecsup.huellitasvvet.ui.components.AppTopBar
import com.tecsup.huellitasvvet.ui.components.FechaChip
import com.tecsup.huellitasvvet.ui.components.HoraChip
import com.tecsup.huellitasvvet.ui.components.MascotaAvatar
import com.tecsup.huellitasvvet.ui.components.VetButton
import com.tecsup.huellitasvvet.viewmodel.ReprogramarCitaViewModel

@Composable
fun ReprogramarCitaScreen(
    vm: ReprogramarCitaViewModel,
    onAtras: () -> Unit,
    onReprogramada: () -> Unit
) {
    val cita by vm.cita.collectAsState()
    val fecha by vm.fecha.collectAsState()
    val hora by vm.hora.collectAsState()
    val noDisponibles by vm.horasNoDisponibles.collectAsState()
    val puedeConfirmar by vm.puedeConfirmar.collectAsState()
    val mensaje by vm.mensaje.collectAsState()
    val reprogramada by vm.reprogramada.collectAsState()

    LaunchedEffect(reprogramada) {
        if (reprogramada) onReprogramada()
    }

    Scaffold(topBar = { AppTopBar("Reprogramar cita", onAtras) }) { padding ->
        val c = cita
        if (c == null) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Text(
                    "Cita actual",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                )

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        MascotaAvatar(especie = c.mascota.mascota.especie, tamano = 48.dp)
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(
                                c.servicio.nombre,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "${c.mascota.mascota.nombre} · ${fechaLegible(c.cita.fecha)} · ${c.cita.hora}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Text(
                    "Nueva fecha",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(vm.fechas) { f ->
                        FechaChip(
                            fecha = f,
                            seleccionada = f == fecha,
                            onClick = { vm.elegirFecha(f) }
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                ) {
                    Text(
                        "Nueva hora",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.weight(1f))
                    Text(
                        "Ocupado = gris",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Horarios.todos.chunked(4).forEach { fila ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        fila.forEach { h ->
                            HoraChip(
                                hora = h,
                                seleccionada = h == hora,
                                ocupada = h in noDisponibles,
                                onClick = { vm.elegirHora(h) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        repeat(4 - fila.size) { Spacer(Modifier.weight(1f)) }
                    }
                    Spacer(Modifier.height(8.dp))
                }

                mensaje?.let {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(Modifier.height(16.dp))
                VetButton(
                    texto = "Confirmar nueva fecha",
                    onClick = vm::confirmar,
                    enabled = puedeConfirmar
                )
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}
