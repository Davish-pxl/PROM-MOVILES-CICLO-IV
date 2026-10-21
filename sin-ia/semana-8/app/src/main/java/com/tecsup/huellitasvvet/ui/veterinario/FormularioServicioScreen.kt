package com.tecsup.huellitasvvet.ui.veterinario

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.tecsup.huellitasvvet.data.local.entity.Servicio
import com.tecsup.huellitasvvet.ui.components.AppTopBar
import com.tecsup.huellitasvvet.ui.components.VetButton
import com.tecsup.huellitasvvet.ui.components.VetTextField
import com.tecsup.huellitasvvet.ui.dueno.iconoServicio
import com.tecsup.huellitasvvet.viewmodel.ServiciosViewModel

private val DURACIONES = listOf(15, 30, 45, 60)
private val ICONOS = listOf("heart", "syringe", "scissors")

@Composable
fun FormularioServicioScreen(
    vm: ServiciosViewModel,
    servicioId: Int,
    onAtras: () -> Unit,
    onGuardado: () -> Unit,
    onEliminado: () -> Unit
) {
    val esEdicion = servicioId > 0
    val existente by remember(servicioId) { vm.servicio(servicioId) }.collectAsState()
    val errores by vm.errores.collectAsState()
    val mensaje by vm.mensaje.collectAsState()
    val guardado by vm.guardado.collectAsState()
    val eliminado by vm.eliminado.collectAsState()

    var nombre by rememberSaveable { mutableStateOf("") }
    var duracion by rememberSaveable { mutableIntStateOf(30) }
    var precio by rememberSaveable { mutableStateOf("") }
    var descripcion by rememberSaveable { mutableStateOf("") }
    var icono by rememberSaveable { mutableStateOf("heart") }
    var activo by rememberSaveable { mutableStateOf(true) }
    var cargado by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(existente) {
        val s = existente
        if (esEdicion && s != null && !cargado) {
            nombre = s.nombre
            duracion = s.duracionMin
            precio = s.precio.toString()
            descripcion = s.descripcion
            icono = s.icono
            activo = s.activo
            cargado = true
        }
    }
    LaunchedEffect(guardado) { if (guardado) onGuardado() }
    LaunchedEffect(eliminado) { if (eliminado) onEliminado() }

    Scaffold(
        topBar = { AppTopBar(if (esEdicion) "Editar servicio" else "Nuevo servicio", onAtras) }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            VetTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = "Nombre del servicio",
                error = errores.nombre
            )

            Etiqueta("Duración")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                DURACIONES.forEach { d ->
                    val elegida = d == duracion
                    val cs = MaterialTheme.colorScheme
                    val forma = RoundedCornerShape(50)
                    Box(
                        Modifier
                            .weight(1f)
                            .height(44.dp)
                            .clip(forma)
                            .background(if (elegida) cs.primary else cs.surface)
                            .border(1.dp, if (elegida) cs.primary else cs.outlineVariant, forma)
                            .clickable { duracion = d },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "$d min",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = if (elegida) FontWeight.Bold else FontWeight.Medium,
                            color = if (elegida) cs.onPrimary else cs.onSurfaceVariant
                        )
                    }
                }
            }
            errores.duracion?.let {
                Text(it, color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall)
            }
            Spacer(Modifier.height(12.dp))

            VetTextField(
                value = precio,
                onValueChange = { precio = it },
                label = "Precio (S/)",
                keyboardType = KeyboardType.Decimal,
                error = errores.precio
            )
            Spacer(Modifier.height(8.dp))
            VetTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = "Descripción",
                singleLine = false,
                minLines = 2
            )

            Etiqueta("Ícono")
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ICONOS.forEach { nombreIcono ->
                    val elegido = nombreIcono == icono
                    val cs = MaterialTheme.colorScheme
                    val forma = RoundedCornerShape(14.dp)
                    Box(
                        Modifier
                            .weight(1f)
                            .height(64.dp)
                            .clip(forma)
                            .background(if (elegido) cs.primaryContainer else cs.surface)
                            .border(if (elegido) 1.5.dp else 1.dp,
                                if (elegido) cs.primary else cs.outlineVariant, forma)
                            .clickable { icono = nombreIcono },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            iconoServicio(nombreIcono),
                            contentDescription = null,
                            tint = if (elegido) cs.primary else cs.onSurfaceVariant
                        )
                    }
                }
            }
            Spacer(Modifier.height(16.dp))

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("Servicio activo", style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold)
                        Text("Los dueños podrán pedirlo", style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(checked = activo, onCheckedChange = { activo = it })
                }
            }
            Spacer(Modifier.height(24.dp))

            VetButton(
                texto = "Guardar servicio",
                onClick = {
                    vm.guardar(
                        Servicio(
                            id = servicioId,
                            nombre = nombre,
                            descripcion = descripcion,
                            duracionMin = duracion,
                            precio = precio.replace(',', '.').toDoubleOrNull() ?: 0.0,
                            activo = activo,
                            icono = icono
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
            ) { Text("Cancelar", style = MaterialTheme.typography.titleMedium) }

            if (esEdicion) {
                Spacer(Modifier.height(8.dp))
                TextButton(
                    onClick = { existente?.let { vm.eliminar(it) } },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Eliminar servicio", color = MaterialTheme.colorScheme.error,
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