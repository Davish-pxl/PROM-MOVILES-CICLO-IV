package com.tuapp.valcarcelcomida.ui.lista

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
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
fun ListaScreen(
    estado: UiState<List<Item>>,
    aviso: String?,
    onReintentar: () -> Unit,
    onItemClick: (String) -> Unit,
    onFavoritoClick: (String) -> Unit
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Comidas · Chicken") }) }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            if (aviso != null) {
                Text(
                    text = aviso,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.errorContainer)
                        .padding(8.dp)
                )
            }
            when (estado) {
                is UiState.Cargando -> Cargando()
                is UiState.Error -> ErrorView(estado.mensaje, onReintentar)
                is UiState.Exito -> {
                    if (estado.datos.isEmpty()) {
                        ListaVacia()
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(estado.datos, key = { it.id }) { item ->
                                ItemFila(
                                    item = item,
                                    onClick = { onItemClick(item.id) },
                                    onFavoritoClick = { onFavoritoClick(item.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ItemFila(
    item: Item,
    onClick: () -> Unit,
    onFavoritoClick: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = item.imagenUrl,
                contentDescription = item.titulo,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(item.titulo, style = MaterialTheme.typography.titleMedium)
                Text(item.subtitulo, style = MaterialTheme.typography.bodySmall)
            }
            IconButton(onClick = onFavoritoClick) {
                Icon(
                    imageVector = if (item.esFavorito) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = if (item.esFavorito) "Quitar de favoritos" else "Agregar a favoritos"
                )
            }
        }
    }
}

@Composable
private fun ListaVacia() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("No hay comidas para mostrar")
    }
}

@Preview(showBackground = true, name = "Éxito")
@Composable
private fun PreviewExito() = ValcarcelComidaTheme {
    ListaScreen(UiState.Exito(itemsPreview), null, {}, {}, {})
}

@Preview(showBackground = true, name = "Cargando")
@Composable
private fun PreviewCargando() = ValcarcelComidaTheme {
    ListaScreen(UiState.Cargando, null, {}, {}, {})
}

@Preview(showBackground = true, name = "Error")
@Composable
private fun PreviewError() = ValcarcelComidaTheme {
    ListaScreen(UiState.Error("No hay conexión a internet"), null, {}, {}, {})
}

@Preview(showBackground = true, name = "Vacío")
@Composable
private fun PreviewVacio() = ValcarcelComidaTheme {
    ListaScreen(UiState.Exito(emptyList()), null, {}, {}, {})
}


private val itemsPreview = listOf(
    Item(id = "1", titulo = "Pollo de ejemplo 1", subtitulo = "Chicken", imagenUrl = null),
    Item(id = "2", titulo = "Pollo de ejemplo 2", subtitulo = "Chicken", imagenUrl = null, esFavorito = true),
    Item(id = "3", titulo = "Pollo de ejemplo 3", subtitulo = "Chicken", imagenUrl = null)
)