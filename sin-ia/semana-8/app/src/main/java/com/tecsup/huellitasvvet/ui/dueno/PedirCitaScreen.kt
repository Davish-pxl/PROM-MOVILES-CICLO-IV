package com.tecsup.huellitasvvet.ui.dueno

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tecsup.huellitasvvet.data.local.entity.Mascota
import com.tecsup.huellitasvvet.data.local.entity.Servicio
import com.tecsup.huellitasvvet.model.Horarios
import com.tecsup.huellitasvvet.ui.components.AppTopBar
import com.tecsup.huellitasvvet.ui.components.FechaChip
import com.tecsup.huellitasvvet.ui.components.HoraChip
import com.tecsup.huellitasvvet.ui.components.MascotaAvatar
import com.tecsup.huellitasvvet.ui.components.VetButton
import com.tecsup.huellitasvvet.viewmodel.PedirCitaViewModel
import java.util.Locale

@Composable
fun PedirCitaScreen(
    vm: PedirCitaViewModel,
    onAtras: () -> Unit,
    onCitaCreada: () -> Unit
) {
    val mascotas by vm.mascotas.collectAsState()
    val servicios by vm.servicios.collectAsState()
    val mascotaId by vm.mascotaId.collectAsState()
    val servicioId by vm.servicioId.collectAsState()
    val fecha by vm.fecha.collectAsState()
    val hora by vm.hora.collectAsState()
    val noDisponibles by vm.horasNoDisponibles.collectAsState()
    val puedeConfirmar by vm.puedeConfirmar.collectAsState()
    val mensaje by vm.mensaje.collectAsState()
    val citaCreada by vm.citaCreada.collectAsState()

    LaunchedEffect(citaCreada) { if (citaCreada) onCitaCreada() }

    val mascota = mascotas.firstOrNull { it.id == mascotaId }
    val servicio = servicios.firstOrNull { it.id == servicioId }

    Scaffold(topBar = { AppTopBar("Pedir cita", onAtras) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {

            TituloSeccion("¿Quién viene?")
            if (mascotas.isEmpty()) {
                Text("Primero registra una mascota",
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(mascotas, key = { it.id }) { m ->
                        PildoraMascota(m, seleccionada = m.id == mascotaId) { vm.elegirMascota(m.id) }
                    }
                }
            }

            TituloSeccion("Servicio")
            servicios.forEach { s ->
                FilaServicio(s, seleccionado = s.id == servicioId) { vm.elegirServicio(s.id) }
                Spacer(Modifier.height(8.dp))
            }

            TituloSeccion("Fecha")
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(vm.fechas) { f ->
                    FechaChip(fecha = f, seleccionada = f == fecha, onClick = { vm.elegirFecha(f) })
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box1 { TituloSeccion("Hora") }
                Spacer(Modifier.weight(1f))
                Text("Ocupado = gris", style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
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
            Spacer(Modifier.height(8.dp))

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(14.dp)) {
                    Text(
                        "${mascota?.nombre ?: "Mascota"} · ${servicio?.nombre ?: "Servicio"}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        "${fechaLegible(fecha.toString())} · ${hora ?: "--:--"} · " +
                                (servicio?.let { "S/ " + String.format(Locale.US, "%.2f", it.precio) } ?: "S/ --"),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            mensaje?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(Modifier.height(16.dp))
            VetButton(texto = "Confirmar cita", onClick = vm::confirmar, enabled = puedeConfirmar)
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun Box1(contenido: @Composable () -> Unit) = contenido()

@Composable
private fun TituloSeccion(texto: String) {
    Text(
        texto,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
    )
}

@Composable
private fun PildoraMascota(m: Mascota, seleccionada: Boolean, onClick: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    val forma = RoundedCornerShape(50)
    Row(
        modifier = Modifier
            .clip(forma)
            .background(if (seleccionada) cs.primaryContainer else cs.surface)
            .border(1.5.dp, if (seleccionada) cs.primary else cs.outlineVariant, forma)
            .clickable(onClick = onClick)
            .padding(start = 6.dp, end = 18.dp, top = 6.dp, bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MascotaAvatar(especie = m.especie, tamano = 36.dp)
        Spacer(Modifier.width(10.dp))
        Text(
            m.nombre,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = if (seleccionada) FontWeight.Bold else FontWeight.Medium,
            color = if (seleccionada) cs.primary else cs.onSurfaceVariant
        )
    }
}

@Composable
private fun FilaServicio(s: Servicio, seleccionado: Boolean, onClick: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    val forma = RoundedCornerShape(14.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(forma)
            .background(if (seleccionado) cs.primaryContainer else cs.surface)
            .border(1.5.dp, if (seleccionado) cs.primary else cs.outlineVariant, forma)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            iconoServicio(s.icono),
            contentDescription = null,
            tint = if (seleccionado) cs.primary else cs.onSurfaceVariant
        )
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(s.nombre, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Text("${s.duracionMin} min", style = MaterialTheme.typography.labelSmall,
                color = cs.onSurfaceVariant)
        }
        Text(
            "S/ ${precioCorto(s.precio)}",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = cs.primary
        )
    }
}
private fun precioCorto(p: Double): String =
    if (p % 1.0 == 0.0) p.toInt().toString() else String.format(Locale.US, "%.2f", p)