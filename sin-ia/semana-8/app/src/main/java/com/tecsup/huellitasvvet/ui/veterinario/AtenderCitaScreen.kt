package com.tecsup.huellitasvvet.ui.veterinario

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.tecsup.huellitasvvet.model.EstadoCita
import com.tecsup.huellitasvvet.ui.components.AppTopBar
import com.tecsup.huellitasvvet.ui.components.MascotaAvatar
import com.tecsup.huellitasvvet.ui.components.VetButton
import com.tecsup.huellitasvvet.ui.components.VetTextField
import com.tecsup.huellitasvvet.ui.dueno.especieSegunSexo
import com.tecsup.huellitasvvet.ui.dueno.fechaLegible
import com.tecsup.huellitasvvet.ui.dueno.iconoServicio
import com.tecsup.huellitasvvet.ui.dueno.textoEdad
import com.tecsup.huellitasvvet.ui.dueno.textoSexo
import com.tecsup.huellitasvvet.viewmodel.AtenderCitaViewModel

@Composable
fun AtenderCitaScreen(
    vm: AtenderCitaViewModel,
    onAtras: () -> Unit,
    onTerminado: () -> Unit
) {
    val cita by vm.cita.collectAsState()
    val errores by vm.errores.collectAsState()
    val mensaje by vm.mensaje.collectAsState()
    val terminado by vm.terminado.collectAsState()

    var peso by rememberSaveable { mutableStateOf("") }
    var diagnostico by rememberSaveable { mutableStateOf("") }
    var tratamiento by rememberSaveable { mutableStateOf("") }
    var cargado by rememberSaveable { mutableStateOf(false) }
    var confirmarCancelar by remember { mutableStateOf(false) }

    LaunchedEffect(cita) {
        val c = cita
        if (c != null && !cargado) {
            peso = c.mascota.mascota.peso.toString()
            cargado = true
        }
    }
    LaunchedEffect(terminado) { if (terminado) onTerminado() }

    Scaffold(topBar = { AppTopBar("Atender cita", onAtras) }) { padding ->
        val c = cita
        if (c == null) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
            return@Scaffold
        }
        val m = c.mascota.mascota
        val atendible = c.cita.estado == EstadoCita.PENDIENTE.name ||
                c.cita.estado == EstadoCita.CONFIRMADA.name
        val pesoNuevo = peso.replace(',', '.').toDoubleOrNull()

        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    MascotaAvatar(especie = m.especie, tamano = 64.dp)
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text(
                            "${m.nombre} · ${especieSegunSexo(m)} ${m.raza}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "${textoEdad(m.anioNacimiento)} · ${textoSexo(m.sexo)} · " +
                                    "Dueño: ${nombreCorto(c.mascota.dueno.nombre)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(iconoServicio(c.servicio.icono), contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(
                                "${c.servicio.nombre} · " +
                                        "${fechaLegible(c.cita.fecha).substringBeforeLast(" ")} · ${c.cita.hora}",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            Text(
                "Registro clínico",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 20.dp, bottom = 8.dp)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                VetTextField(
                    value = peso,
                    onValueChange = { peso = it },
                    label = "Peso (kg)",
                    keyboardType = KeyboardType.Decimal,
                    error = errores.peso,
                    modifier = Modifier.weight(1f)
                )
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 8.dp)
                ) {
                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Favorite, contentDescription = null,
                            tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text("Peso anterior", style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${m.peso} kg", style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            VetTextField(
                value = diagnostico,
                onValueChange = { diagnostico = it },
                label = "Diagnóstico",
                error = errores.diagnostico,
                singleLine = false,
                minLines = 3
            )
            Spacer(Modifier.height(8.dp))
            VetTextField(
                value = tratamiento,
                onValueChange = { tratamiento = it },
                label = "Tratamiento",
                singleLine = false,
                minLines = 3
            )
            Spacer(Modifier.height(12.dp))

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Check, contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(10.dp))
                    Text(
                        "Se actualizará el peso de ${m.nombre} a ${pesoNuevo ?: "—"} kg",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            mensaje?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium)
            }
            if (!atendible) {
                Spacer(Modifier.height(8.dp))
                Text(
                    "Esta cita está ${c.cita.estado.lowercase()} y ya no se puede atender",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(Modifier.height(20.dp))

            VetButton(
                texto = "Registrar atención",
                enabled = atendible,
                onClick = {
                    vm.registrar(diagnostico, tratamiento, pesoNuevo ?: 0.0)
                }
            )
            Spacer(Modifier.height(12.dp))
            OutlinedButton(
                onClick = { confirmarCancelar = true },
                enabled = atendible,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.error),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text("Cancelar cita", style = MaterialTheme.typography.titleMedium)
            }
            Spacer(Modifier.height(24.dp))
        }
    }

    if (confirmarCancelar) {
        AlertDialog(
            onDismissRequest = { confirmarCancelar = false },
            title = { Text("Cancelar cita") },
            text = { Text("¿Seguro que deseas cancelar esta cita?") },
            confirmButton = {
                TextButton(onClick = {
                    confirmarCancelar = false
                    vm.cancelar()
                }) {
                    Text("Sí, cancelar", color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmarCancelar = false }) { Text("No") }
            }
        )
    }
}