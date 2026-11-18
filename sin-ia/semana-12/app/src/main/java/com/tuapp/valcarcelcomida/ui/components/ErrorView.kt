package com.tuapp.valcarcelcomida.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tuapp.valcarcelcomida.ui.TipoError
import com.tuapp.valcarcelcomida.ui.UiState

@Composable
fun ErrorView(
    error: UiState.Error,
    onReintentar: () -> Unit,
    onCerrar: (() -> Unit)? = null,
    textoCerrar: String = "Cerrar",
    modifier: Modifier = Modifier
) {
    var ventanaCerrada by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = iconoDe(error.tipo),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(56.dp)
        )
        Spacer(Modifier.height(12.dp))
        Text(
            error.titulo,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(16.dp))
        Button(onClick = onReintentar) {
            Text("Reintentar")
        }
    }

    if (!ventanaCerrada) {
        AlertDialog(
            onDismissRequest = { ventanaCerrada = true },
            icon = {
                Icon(
                    imageVector = iconoDe(error.tipo),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = { Text(error.titulo, textAlign = TextAlign.Center) },
            text = { Text(error.mensaje, textAlign = TextAlign.Center) },
            confirmButton = {
                Button(onClick = onReintentar) {
                    Text("Reintentar")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    ventanaCerrada = true
                    onCerrar?.invoke()
                }) {
                    Text(textoCerrar)
                }
            }
        )
    }
}
private fun iconoDe(tipo: TipoError): ImageVector = when (tipo) {
    TipoError.SIN_CONEXION -> Icons.Filled.WifiOff
    TipoError.TIEMPO_AGOTADO -> Icons.Filled.HourglassEmpty
    TipoError.SERVIDOR -> Icons.Filled.CloudOff
    TipoError.NO_ENCONTRADO -> Icons.Filled.SearchOff
    TipoError.GENERAL -> Icons.Filled.Warning
}