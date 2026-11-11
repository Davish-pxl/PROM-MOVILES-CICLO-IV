package com.tuapp.valcarcelcomida.ui.admin

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tuapp.valcarcelcomida.ui.UiState
import com.tuapp.valcarcelcomida.ui.components.Cargando
import com.tuapp.valcarcelcomida.ui.theme.ValcarcelComidaTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    estado: UiState<ResumenAdmin>,
    mensaje: String?,
    ocupado: Boolean,
    onVolver: () -> Unit,
    onSincronizar: () -> Unit,
    onLimpiarCache: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel Admin", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            when (estado) {
                is UiState.Cargando -> Cargando()
                is UiState.Error -> SinPermiso(estado.mensaje, onVolver)
                is UiState.Exito -> PanelContenido(
                    resumen = estado.datos,
                    mensaje = mensaje,
                    ocupado = ocupado,
                    onSincronizar = onSincronizar,
                    onLimpiarCache = onLimpiarCache
                )
            }
        }
    }
}

@Composable
private fun PanelContenido(
    resumen: ResumenAdmin,
    mensaje: String?,
    ocupado: Boolean,
    onSincronizar: () -> Unit,
    onLimpiarCache: () -> Unit
) {
    var confirmarLimpieza by rememberSaveable { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Total("🍽️", "Recetas guardadas", resumen.totalGuardado,
                MaterialTheme.colorScheme.primaryContainer, Modifier.weight(1f))
            Total("💛", "Favoritos", resumen.totalFavoritos,
                MaterialTheme.colorScheme.tertiaryContainer, Modifier.weight(1f))
        }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = onSincronizar,
            enabled = !ocupado,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (ocupado) {
                CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                Spacer(Modifier.width(8.dp))
            }
            Text("Sincronizar")
        }

        Spacer(Modifier.height(12.dp))

        OutlinedButton(
            onClick = { confirmarLimpieza = true },
            enabled = !ocupado,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Limpiar caché")
        }

        if (mensaje != null) {
            Spacer(Modifier.height(16.dp))
            Text(mensaje, style = MaterialTheme.typography.bodyMedium)
        }
    }

    if (confirmarLimpieza) {
        AlertDialog(
            onDismissRequest = { confirmarLimpieza = false },
            title = { Text("¿Limpiar caché?") },
            text = { Text("Se borrarán todas las comidas guardadas en el celular, incluidos los favoritos. Podrás volver a descargarlas con Sincronizar.") },
            confirmButton = {
                TextButton(onClick = {
                    confirmarLimpieza = false
                    onLimpiarCache()
                }) { Text("Limpiar") }
            },
            dismissButton = {
                TextButton(onClick = { confirmarLimpieza = false }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun Total(emoji: String, titulo: String, valor: Int, fondo: Color, modifier: Modifier = Modifier) {
    Card(modifier, colors = CardDefaults.cardColors(containerColor = fondo)) {
        Column(Modifier.fillMaxWidth().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(emoji, style = MaterialTheme.typography.headlineSmall)
            Text(valor.toString(), style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold)
            Text(titulo, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun SinPermiso(mensaje: String, onVolver: () -> Unit) {
    Column(
        Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(mensaje, textAlign = TextAlign.Center)
        Spacer(Modifier.height(16.dp))
        Button(onClick = onVolver) { Text("Volver") }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewAdmin() = ValcarcelComidaTheme {
    AdminScreen(
        estado = UiState.Exito(ResumenAdmin(totalGuardado = 30, totalFavoritos = 4)),
        mensaje = "Se guardaron 30 elementos",
        ocupado = false,
        onVolver = {}, onSincronizar = {}, onLimpiarCache = {}
    )
}