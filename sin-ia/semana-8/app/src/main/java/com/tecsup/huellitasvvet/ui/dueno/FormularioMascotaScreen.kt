package com.tecsup.huellitasvvet.ui.dueno

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.tecsup.huellitasvvet.data.local.entity.Mascota
import com.tecsup.huellitasvvet.model.Especie
import com.tecsup.huellitasvvet.model.Sexo
import com.tecsup.huellitasvvet.ui.components.AppTopBar
import com.tecsup.huellitasvvet.ui.components.MascotaAvatar
import com.tecsup.huellitasvvet.ui.components.VetButton
import com.tecsup.huellitasvvet.ui.components.VetTextField
import com.tecsup.huellitasvvet.viewmodel.MascotaViewModel

@Composable
fun FormularioMascotaScreen(
    vm: MascotaViewModel,
    mascotaId: Int,
    onAtras: () -> Unit,
    onGuardado: () -> Unit,
    onEliminada: () -> Unit
) {
    val esEdicion = mascotaId > 0
    val existente by remember(mascotaId) { vm.mascota(mascotaId) }.collectAsState()
    val errores by vm.errores.collectAsState()
    val mensaje by vm.mensaje.collectAsState()
    val guardado by vm.guardado.collectAsState()
    val eliminado by vm.eliminado.collectAsState()

    var nombre by rememberSaveable { mutableStateOf("") }
    var raza by rememberSaveable { mutableStateOf("") }
    var anio by rememberSaveable { mutableStateOf("") }
    var peso by rememberSaveable { mutableStateOf("") }
    var especie by rememberSaveable { mutableStateOf("") }
    var sexo by rememberSaveable { mutableStateOf("") }
    var cargado by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(existente) {
        val m = existente
        if (esEdicion && m != null && !cargado) {
            nombre = m.nombre
            raza = m.raza
            anio = m.anioNacimiento.toString()
            peso = m.peso.toString()
            especie = m.especie
            sexo = m.sexo
            cargado = true
        }
    }
    LaunchedEffect(guardado) { if (guardado) onGuardado() }
    LaunchedEffect(eliminado) { if (eliminado) onEliminada() }

    Scaffold(
        topBar = { AppTopBar(if (esEdicion) "Editar mascota" else "Nueva mascota", onAtras) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Box(Modifier.size(104.dp)) {
                    MascotaAvatar(
                        especie = especie.ifBlank { Especie.OTRO.name },
                        tamano = 96.dp,
                        modifier = Modifier.align(Alignment.Center)
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                            .border(2.dp, MaterialTheme.colorScheme.background, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(18.dp))
                    }
                }
            }
            Spacer(Modifier.height(20.dp))

            VetTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = "Nombre",
                error = errores.nombre
            )

            Etiqueta("Especie")
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                listOf(Especie.PERRO to "Perro", Especie.GATO to "Gato", Especie.OTRO to "Otro")
                    .forEach { (e, texto) ->
                        Opcion(
                            texto = texto,
                            seleccionado = especie == e.name,
                            relleno = true,
                            onClick = { especie = e.name },
                            modifier = Modifier.weight(1f)
                        )
                    }
            }
            ErrorCampo(errores.especie)
            Spacer(Modifier.height(12.dp))

            VetTextField(value = raza, onValueChange = { raza = it }, label = "Raza")
            Spacer(Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                VetTextField(
                    value = anio,
                    onValueChange = { anio = it },
                    label = "Año de nacimiento",
                    keyboardType = KeyboardType.Number,
                    error = errores.anio,
                    modifier = Modifier.weight(1f)
                )
                VetTextField(
                    value = peso,
                    onValueChange = { peso = it },
                    label = "Peso (kg)",
                    keyboardType = KeyboardType.Decimal,
                    error = errores.peso,
                    modifier = Modifier.weight(1f)
                )
            }

            Etiqueta("Sexo")
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                listOf(Sexo.MACHO to "Macho", Sexo.HEMBRA to "Hembra")
                    .forEach { (s, texto) ->
                        Opcion(
                            texto = texto,
                            seleccionado = sexo == s.name,
                            relleno = false,
                            onClick = { sexo = s.name },
                            modifier = Modifier.weight(1f)
                        )
                    }
            }
            ErrorCampo(errores.sexo)
            Spacer(Modifier.height(28.dp))

            VetButton(
                texto = "Guardar mascota",
                onClick = {
                    // La pantalla solo arma la Mascota; las validaciones las hace el ViewModel
                    vm.guardar(
                        Mascota(
                            id = mascotaId,
                            duenoId = existente?.duenoId ?: 0,
                            nombre = nombre,
                            especie = especie,
                            raza = raza,
                            anioNacimiento = anio.toIntOrNull() ?: 0,
                            peso = peso.replace(',', '.').toDoubleOrNull() ?: 0.0,
                            sexo = sexo
                        )
                    )
                }
            )
            Spacer(Modifier.height(12.dp))
            OutlinedButton(
                onClick = onAtras,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text("Cancelar", style = MaterialTheme.typography.titleMedium)
            }

            if (esEdicion) {
                Spacer(Modifier.height(8.dp))
                TextButton(
                    onClick = { existente?.let { vm.eliminar(it) } },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Eliminar mascota", color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.SemiBold)
                }
                mensaje?.let {
                    Text(it, color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium)
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun Etiqueta(texto: String) {
    Text(
        texto,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
    )
}

@Composable
private fun ErrorCampo(error: String?) {
    if (error != null) {
        Text(
            error,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
private fun Opcion(
    texto: String,
    seleccionado: Boolean,
    relleno: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cs = MaterialTheme.colorScheme
    val fondo = when {
        !seleccionado -> cs.surface
        relleno -> cs.primary
        else -> cs.primaryContainer
    }
    val borde = if (seleccionado) cs.primary else cs.outlineVariant
    val colorTexto = when {
        !seleccionado -> cs.onSurfaceVariant
        relleno -> cs.onPrimary
        else -> cs.primary
    }
    val forma = RoundedCornerShape(12.dp)
    Box(
        modifier = modifier
            .height(48.dp)
            .clip(forma)
            .background(fondo)
            .border(1.dp, borde, forma)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            texto,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Medium,
            color = colorTexto
        )
    }
}