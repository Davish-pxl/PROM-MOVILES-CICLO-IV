package com.tecsup.huellitasvvet.ui.veterinario

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tecsup.huellitasvvet.data.local.relation.MascotaConDueno
import com.tecsup.huellitasvvet.model.Especie
import com.tecsup.huellitasvvet.ui.components.AppTopBar
import com.tecsup.huellitasvvet.ui.components.MascotaAvatar
import com.tecsup.huellitasvvet.ui.dueno.fechaCorta
import com.tecsup.huellitasvvet.ui.dueno.textoEdad
import com.tecsup.huellitasvvet.viewmodel.PacientesViewModel

@Composable
fun PacientesScreen(
    vm: PacientesViewModel,
    onAtras: () -> Unit,
    onAbrirPaciente: (Int) -> Unit
) {
    val pacientes by vm.pacientes.collectAsState()
    val texto by vm.texto.collectAsState()
    val especie by vm.especie.collectAsState()
    val hayFiltros by vm.hayFiltros.collectAsState()
    val ultimas by vm.ultimaVisita.collectAsState()

    Scaffold(topBar = { AppTopBar("Pacientes", onAtras) }) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            OutlinedTextField(
                value = texto,
                onValueChange = vm::buscar,
                placeholder = { Text("Buscar por mascota, dueño o raza...") },
                leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
                trailingIcon = {
                    if (texto.isNotEmpty()) {
                        IconButton(onClick = { vm.buscar("") }) {
                            Icon(Icons.Filled.Close, contentDescription = "Limpiar búsqueda")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val filtros = listOf(
                    null to "Todos",
                    Especie.PERRO to "Perros",
                    Especie.GATO to "Gatos",
                    Especie.OTRO to "Otros"
                )
                items(filtros) { (e, nombre) ->
                    ChipFiltro(nombre, seleccionado = especie == e) { vm.filtrarEspecie(e) }
                }
            }

            val cantidad = pacientes.size
            Text(
                text = if (cantidad == 1) "1 paciente encontrado" else "$cantidad pacientes encontrados",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
            )

            if (pacientes.isEmpty()) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (hayFiltros) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                "No hay pacientes que coincidan con la búsqueda",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            TextButton(onClick = vm::limpiarFiltros) {
                                Text("Limpiar filtros")
                            }
                        }
                    } else {
                        Text(
                            "No se encontraron pacientes",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 20.dp, top = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(pacientes, key = { it.mascota.id }) { p ->
                        TarjetaPaciente(p, ultimas[p.mascota.id]) { onAbrirPaciente(p.mascota.id) }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChipFiltro(texto: String, seleccionado: Boolean, onClick: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    val forma = RoundedCornerShape(50)
    Box(
        Modifier
            .clip(forma)
            .background(if (seleccionado) cs.primary else cs.surface)
            .border(1.dp, if (seleccionado) cs.primary else cs.outlineVariant, forma)
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 8.dp)
    ) {
        Text(
            texto,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Medium,
            color = if (seleccionado) cs.onPrimary else cs.onSurfaceVariant
        )
    }
}

@Composable
private fun TarjetaPaciente(p: MascotaConDueno, ultimaVisita: String?, onClick: () -> Unit) {
    val m = p.mascota
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            MascotaAvatar(especie = m.especie, tamano = 56.dp)
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(m.nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("${m.raza} · ${textoEdad(m.anioNacimiento)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Dueño: ${nombreCorto(p.dueno.nombre)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    ultimaVisita?.let { "Última visita: ${fechaCorta(it)}" } ?: "Sin visitas",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(Icons.Filled.ChevronRight, contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
