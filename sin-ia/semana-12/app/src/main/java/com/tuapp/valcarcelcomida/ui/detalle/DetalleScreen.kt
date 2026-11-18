package com.tuapp.valcarcelcomida.ui.detalle

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.tuapp.valcarcelcomida.domain.model.Item
import com.tuapp.valcarcelcomida.ui.UiState
import com.tuapp.valcarcelcomida.ui.components.Cargando
import com.tuapp.valcarcelcomida.ui.components.ErrorView
import com.tuapp.valcarcelcomida.ui.lista.avisarFavoritoQuitado
import com.tuapp.valcarcelcomida.ui.theme.ValcarcelComidaTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleScreen(
    estado: UiState<Item>,
    onVolver: () -> Unit,
    onReintentar: () -> Unit,
    onFavoritoClick: () -> Unit
) {
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val alTocarFavorito: () -> Unit = {
        val item = (estado as? UiState.Exito)?.datos
        onFavoritoClick()
        if (item?.esFavorito == true) {
            scope.avisarFavoritoQuitado(snackbar, item.titulo) { onFavoritoClick() }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            TopAppBar(
                title = {
                    val titulo = (estado as? UiState.Exito)?.datos?.titulo ?: "Receta"
                    Text(titulo, maxLines = 1, overflow = TextOverflow.Ellipsis)
                },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    if (estado is UiState.Exito) {
                        IconButton(onClick = alTocarFavorito) {
                            Icon(
                                imageVector = if (estado.datos.esFavorito) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = if (estado.datos.esFavorito) "Quitar de favoritos" else "Agregar a favoritos",
                                tint = if (estado.datos.esFavorito) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            when (estado) {
                is UiState.Cargando -> Cargando(mensaje = "Preparando la receta…")
                is UiState.Error -> ErrorView(
                    error = estado,
                    onReintentar = onReintentar,
                    onCerrar = onVolver,
                    textoCerrar = "Volver"
                )
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
    ) {
        AsyncImage(
            model = item.imagenUrl,
            contentDescription = item.titulo,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(4f / 3f)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        )

        Column(Modifier.padding(20.dp)) {
            Text(
                item.titulo,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Etiqueta(item.subtitulo, MaterialTheme.colorScheme.primaryContainer)
                item.area?.let { Etiqueta(it, MaterialTheme.colorScheme.tertiaryContainer) }
            }

            if (item.ingredientes.isNotEmpty()) {
                Spacer(Modifier.height(24.dp))
                TituloSeccion("Ingredientes", "${item.ingredientes.size}")
                Spacer(Modifier.height(8.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        item.ingredientes.forEach { ingrediente ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    Modifier
                                        .size(6.dp)
                                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                                )
                                Spacer(Modifier.width(12.dp))
                                Text(ingrediente, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }

            val pasos = pasosDe(item.instrucciones)
            if (pasos.isNotEmpty()) {
                Spacer(Modifier.height(24.dp))
                TituloSeccion("Preparación", "${pasos.size} pasos")
                Spacer(Modifier.height(8.dp))
                pasos.forEachIndexed { indice, paso ->
                    Row(Modifier.padding(vertical = 6.dp)) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    "${indice + 1}",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                        Spacer(Modifier.width(12.dp))
                        Text(paso, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}
private fun pasosDe(instrucciones: String?): List<String> =
    instrucciones.orEmpty()
        .split("\r\n", "\n")
        .map { it.trim() }
        .filter { it.isNotEmpty() && !it.matches(Regex("(?i)^(step|paso)\\s*\\d+[.:]?$")) }

@Composable
private fun Etiqueta(texto: String, fondo: androidx.compose.ui.graphics.Color) {
    Surface(shape = CircleShape, color = fondo) {
        Text(
            texto,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun TituloSeccion(titulo: String, detalle: String) {
    Row(verticalAlignment = Alignment.Bottom) {
        Text(titulo, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(Modifier.width(8.dp))
        Text(
            detalle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
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
                subtitulo = "Pollo",
                imagenUrl = null,
                area = "Peruana",
                instrucciones = "Sazonar el pollo.\nHornear 40 minutos.\nServir caliente.",
                ingredientes = listOf("Pollo", "Ajo", "Limón")
            )
        ),
        onVolver = {}, onReintentar = {}, onFavoritoClick = {}
    )
}