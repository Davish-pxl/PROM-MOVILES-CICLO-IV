package com.tecsup.huellitasvvet.ui.dueno

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tecsup.huellitasvvet.data.local.entity.Mascota
import com.tecsup.huellitasvvet.model.Especie
import com.tecsup.huellitasvvet.model.Sexo
import com.tecsup.huellitasvvet.ui.components.AppTopBar
import com.tecsup.huellitasvvet.ui.components.HistorialItem
import com.tecsup.huellitasvvet.ui.components.MascotaAvatar
import com.tecsup.huellitasvvet.viewmodel.MascotaViewModel

@Composable
fun FichaMascotaScreen(
    vm: MascotaViewModel,
    mascotaId: Int,
    onAtras: () -> Unit,
    onEditar: (() -> Unit)? = null,
    onPedirCita: (() -> Unit)? = null
) {
    val mascota by remember(mascotaId) { vm.mascota(mascotaId) }.collectAsState()
    val historial by remember(mascotaId) { vm.historial(mascotaId) }.collectAsState()
    var pestana by rememberSaveable { mutableIntStateOf(1) }

    Scaffold(
        topBar = { AppTopBar(mascota?.nombre ?: "", onAtras) }
    ) { padding ->
        val m = mascota
        if (m == null) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
        } else {
            Column(
                modifier = Modifier
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
                        MascotaAvatar(especie = m.especie, tamano = 80.dp)
                        Spacer(Modifier.width(16.dp))
                        Column {
                            Text(m.nombre, style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(2.dp))
                            Text(
                                "${especieSegunSexo(m)} ${m.raza} · ${textoEdad(m.anioNacimiento)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                "${textoSexo(m.sexo)} · ${m.peso} kg",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))

                if (onPedirCita != null && onEditar != null) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(
                            onClick = onPedirCita,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                        ) {
                            Icon(Icons.Outlined.CalendarMonth, contentDescription = null,
                                modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Pedir cita", fontWeight = FontWeight.SemiBold)
                        }
                        OutlinedButton(
                            onClick = onEditar,
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                        ) {
                            Icon(Icons.Outlined.Edit, contentDescription = null,
                                modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Editar", fontWeight = FontWeight.SemiBold)
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                }

                SelectorPestanas(pestana) { pestana = it }
                Spacer(Modifier.height(16.dp))

                if (pestana == 0) {
                    DatosMascota(m)
                } else if (historial.isEmpty()) {
                    Text(
                        "Aún no tiene atenciones",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 24.dp)
                    )
                } else {
                    historial.forEachIndexed { i, c ->
                        HistorialItem(c, esUltimo = i == historial.lastIndex)
                    }
                }
            }
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
        listOf("Datos", "Historial clínico").forEachIndexed { i, texto ->
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

@Composable
private fun DatosMascota(m: Mascota) {
    Column {
        FilaDato("Especie", nombreEspecie(m.especie))
        FilaDato("Raza", m.raza.ifBlank { "—" })
        FilaDato("Sexo", textoSexo(m.sexo))
        FilaDato("Año de nacimiento", m.anioNacimiento.toString())
        FilaDato("Edad", textoEdad(m.anioNacimiento))
        FilaDato("Peso actual", "${m.peso} kg")
    }
}

@Composable
private fun FilaDato(etiqueta: String, valor: String) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Text(etiqueta, color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f))
        Text(valor, fontWeight = FontWeight.SemiBold)
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
}

internal fun especieSegunSexo(m: Mascota): String {
    val hembra = m.sexo == Sexo.HEMBRA.name
    return when (m.especie) {
        Especie.PERRO.name -> if (hembra) "Perra" else "Perro"
        Especie.GATO.name -> if (hembra) "Gata" else "Gato"
        else -> "Otro"
    }
}
internal fun textoSexo(sexo: String): String =
    if (sexo == Sexo.HEMBRA.name) "Hembra" else "Macho"