package com.tuapp.valcarcelcomida.ui.favoritos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tuapp.valcarcelcomida.domain.model.Item
import com.tuapp.valcarcelcomida.ui.UiState
import com.tuapp.valcarcelcomida.ui.components.Cargando
import com.tuapp.valcarcelcomida.ui.components.ErrorView
import com.tuapp.valcarcelcomida.ui.lista.ItemFila
import com.tuapp.valcarcelcomida.ui.theme.ValcarcelComidaTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritosScreen(
    estado: UiState<List<Item>>,
    onVolver: () -> Unit,
    onItemClick: (String) -> Unit,
    onFavoritoClick: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Favoritos") },
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
                is UiState.Error -> ErrorView(estado.mensaje, onReintentar = onVolver)
                is UiState.Exito -> {
                    if (estado.datos.isEmpty()) {
                        SinFavoritos()
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
private fun SinFavoritos() {
    Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Text(
            "Aún no tienes favoritos.\nToca el corazón de una comida para guardarla aquí.",
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true, name = "Con favoritos")
@Composable
private fun PreviewFavoritos() = ValcarcelComidaTheme {
    FavoritosScreen(
        estado = UiState.Exito(
            listOf(Item(id = "1", titulo = "Pollo de ejemplo", subtitulo = "Chicken", imagenUrl = null, esFavorito = true))
        ),
        onVolver = {}, onItemClick = {}, onFavoritoClick = {}
    )
}

@Preview(showBackground = true, name = "Sin favoritos")
@Composable
private fun PreviewSinFavoritos() = ValcarcelComidaTheme {
    FavoritosScreen(UiState.Exito(emptyList()), {}, {}, {})
}