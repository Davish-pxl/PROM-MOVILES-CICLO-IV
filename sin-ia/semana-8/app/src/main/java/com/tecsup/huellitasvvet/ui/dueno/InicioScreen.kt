package com.tecsup.huellitasvvet.ui.dueno

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Vaccines
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.tecsup.huellitasvvet.data.local.entity.Mascota
import com.tecsup.huellitasvvet.data.local.entity.Servicio
import com.tecsup.huellitasvvet.data.local.relation.CitaConDetalle
import com.tecsup.huellitasvvet.ui.components.MascotaAvatar
import com.tecsup.huellitasvvet.ui.components.VetButton
import com.tecsup.huellitasvvet.viewmodel.InicioViewModel

@Composable
fun InicioScreen(
    vm: InicioViewModel,
    onAbrirMascota: (Int) -> Unit,
    onAgregarMascota: () -> Unit,
    onVerTodas: () -> Unit,
    onPedirCita: () -> Unit
) {
    val mascotas by vm.mascotas.collectAsState()
    val proxima by vm.proximaCita.collectAsState()
    val servicios by vm.servicios.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(
                    "Hola, ${vm.nombreDueno.substringBefore(" ")}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "¿Cómo están tus mascotas hoy?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(Icons.Outlined.Notifications, contentDescription = "Notificaciones")
        }
        Spacer(Modifier.height(20.dp))

        TarjetaProximaCita(proxima)
        Spacer(Modifier.height(24.dp))

        TituloSeccion("Mis mascotas", accion = "Ver todas", onAccion = onVerTodas)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(mascotas, key = { it.id }) { m ->
                TarjetaMascota(m, onClick = { onAbrirMascota(m.id) })
            }
            item { TarjetaAgregar(onClick = onAgregarMascota) }
        }
        Spacer(Modifier.height(24.dp))

        TituloSeccion("Servicios")
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            servicios.forEach { s ->
                TarjetaServicio(s, onClick = onPedirCita, modifier = Modifier.weight(1f))
            }
        }
        Spacer(Modifier.height(28.dp))

        VetButton(texto = "Pedir una cita", onClick = onPedirCita)
    }
}

@Composable
private fun TituloSeccion(texto: String, accion: String? = null, onAccion: () -> Unit = {}) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            texto,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 8.dp)
        )
        if (accion != null) {
            TextButton(onClick = onAccion) {
                Text(accion, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun TarjetaProximaCita(c: CitaConDetalle?) {
    val enPrimario = MaterialTheme.colorScheme.onPrimary
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.fillMaxWidth()
    ) {
        if (c == null) {
            Column(Modifier.padding(20.dp)) {
                Text("Próxima cita", style = MaterialTheme.typography.labelLarge,
                    color = enPrimario.copy(alpha = 0.8f))
                Spacer(Modifier.height(4.dp))
                Text("Aún no tienes citas", style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold, color = enPrimario)
            }
        } else {
            Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Próxima cita", style = MaterialTheme.typography.labelLarge,
                        color = enPrimario.copy(alpha = 0.8f))
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "${c.mascota.mascota.nombre} · ${c.servicio.nombre}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = enPrimario
                    )
                    Spacer(Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Schedule, contentDescription = null,
                            tint = enPrimario, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(
                            "${fechaLegible(c.cita.fecha)} · ${c.cita.hora}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = enPrimario
                        )
                    }
                    Spacer(Modifier.height(10.dp))
                    Surface(shape = RoundedCornerShape(50), color = enPrimario) {
                        Text(
                            c.cita.estado.lowercase().replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)
                        )
                    }
                }
                Spacer(Modifier.width(12.dp))
                Surface(shape = RoundedCornerShape(14.dp), color = enPrimario) {
                    MascotaAvatar(
                        especie = c.mascota.mascota.especie,
                        tamano = 52.dp,
                        modifier = Modifier.padding(6.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun TarjetaMascota(m: Mascota, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier
            .width(104.dp)
            .height(132.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            MascotaAvatar(especie = m.especie, tamano = 60.dp)
            Spacer(Modifier.height(8.dp))
            Text(m.nombre, style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text("Ver ficha", style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun TarjetaAgregar(onClick: () -> Unit) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary),
        modifier = Modifier
            .width(104.dp)
            .height(132.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Filled.Add, contentDescription = null,
                tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
            Spacer(Modifier.height(8.dp))
            Text("Agregar", style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
private fun TarjetaServicio(s: Servicio, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primaryContainer),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(iconoServicio(s.icono), contentDescription = null,
                tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
            Spacer(Modifier.height(6.dp))
            Text(
                s.nombre.substringBefore(" "),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

internal fun iconoServicio(icono: String): ImageVector = when (icono) {
    "heart" -> Icons.Filled.Favorite
    "syringe" -> Icons.Filled.Vaccines
    "scissors" -> Icons.Filled.ContentCut
    else -> Icons.Filled.MedicalServices
}