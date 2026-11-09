package com.tuapp.valcarcelcomida.ui.detalle

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.tuapp.valcarcelcomida.domain.model.Item
import com.tuapp.valcarcelcomida.ui.UiState
import com.tuapp.valcarcelcomida.ui.components.Cargando
import com.tuapp.valcarcelcomida.ui.components.ErrorView
import com.tuapp.valcarcelcomida.ui.theme.ValcarcelComidaTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleScreen(
    estado: UiState<Item>,
    onVolver: () -> Unit,
    onReintentar: () -> Unit,
    onFavoritoClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val titulo = (estado as? UiState.Exito)?.datos?.titulo ?: "Detalle"
                    Text(titulo, maxLines = 1, overflow = TextOverflow.Ellipsis)
                },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    if (estado is UiState.Exito) {
                        IconButton(onClick = onFavoritoClick) {
                            Icon(
                                imageVector = if (estado.datos.esFavorito) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = if (estado.datos.esFavorito) "Quitar de favoritos" else "Agregar a favoritos"
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            when (estado) {
                is UiState.Cargando -> Cargando()
                is UiState.Error -> ErrorView(estado.mensaje, onReintentar)
                is UiState.Exito -> DetalleContenido(estado.datos)
            }
        }
    }
}

@Composable
private fun DetalleContenido(item: Item) {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        AsyncImage(
            model = item.imagenUrl,
            contentDescription = item.titulo,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        )
        Spacer(Modifier.height(16.dp))

        Text(item.titulo, style = MaterialTheme.typography.headlineSmall)
        Row(verticalAlignment = Alignment.CenterVertically) {
            val datos = listOfNotNull(item.subtitulo, item.area).joinToString(" · ")
            Text(datos, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
        }

        if (item.ingredientes.isNotEmpty()) {
            Spacer(Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(Modifier.height(16.dp))
            Text("Ingredientes", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            item.ingredientes.forEach { ingrediente ->
                Text("• $ingrediente", style = MaterialTheme.typography.bodyMedium)
            }
        }

        if (!item.instrucciones.isNullOrBlank()) {
            Spacer(Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(Modifier.height(16.dp))
            Text("Preparación", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Text(item.instrucciones, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewDetalle() = ValcarcelComidaTheme {
    DetalleScreen(
        estado = UiState.Exito(
            Item(
                id = "1",
                titulo = "Pollo de ejemplo",
                subtitulo = "Chicken",
                imagenUrl = null,
                area = "Peruvian",
                instrucciones = "Paso 1. Sazonar el pollo.\nPaso 2. Hornear 40 minutos.",
                ingredientes = listOf("Pollo", "Ajo", "Limón")
            )
        ),
        onVolver = {}, onReintentar = {}, onFavoritoClick = {}
    )
}