package com.tuapp.valcarcelcomida.ui.favoritos

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.tuapp.valcarcelcomida.domain.model.Item
import com.tuapp.valcarcelcomida.ui.UiState
import com.tuapp.valcarcelcomida.ui.components.Cargando
import com.tuapp.valcarcelcomida.ui.components.ErrorView
import com.tuapp.valcarcelcomida.ui.lista.GrillaComidas
import com.tuapp.valcarcelcomida.ui.lista.MensajeVacio
import com.tuapp.valcarcelcomida.ui.lista.avisarFavoritoQuitado
import com.tuapp.valcarcelcomida.ui.theme.ValcarcelComidaTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritosScreen(
    estado: UiState<List<Item>>,
    onVolver: () -> Unit,
    onItemClick: (String) -> Unit,
    onFavoritoClick: (String) -> Unit
) {
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val alTocarFavorito: (String) -> Unit = { id ->
        val item = (estado as? UiState.Exito)?.datos?.firstOrNull { it.id == id }
        onFavoritoClick(id)
        if (item != null) {
            scope.avisarFavoritoQuitado(snackbar, item.titulo) { onFavoritoClick(id) }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            TopAppBar(
                title = { Text("Mis favoritos", fontWeight = FontWeight.Bold) },
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
                is UiState.Error -> ErrorView(
                    error = estado,
                    onReintentar = onVolver,
                    onCerrar = onVolver,
                    textoCerrar = "Volver"
                )
                is UiState.Exito -> {
                    if (estado.datos.isEmpty()) {
                        MensajeVacio(
                            emoji = "💛",
                            texto = "Aún no tienes favoritos.\nToca el corazón de una receta para guardarla aquí."
                        )
                    } else {
                        GrillaComidas(
                            items = estado.datos,
                            onItemClick = onItemClick,
                            onFavoritoClick = alTocarFavorito
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Con favoritos")
@Composable
private fun PreviewFavoritos() = ValcarcelComidaTheme {
    FavoritosScreen(
        estado = UiState.Exito(
            listOf(Item(id = "1", titulo = "Pollo de ejemplo", subtitulo = "Pollo", imagenUrl = null, esFavorito = true))
        ),
        onVolver = {}, onItemClick = {}, onFavoritoClick = {}
    )
}

@Preview(showBackground = true, name = "Sin favoritos")
@Composable
private fun PreviewSinFavoritos() = ValcarcelComidaTheme {
    FavoritosScreen(UiState.Exito(emptyList()), {}, {}, {})
}