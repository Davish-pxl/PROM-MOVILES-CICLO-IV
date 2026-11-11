package com.tuapp.valcarcelcomida.ui.lista

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    onFavoritoClick: (String) -> Unit,
    onVerFavoritos: () -> Unit,
    onCerrarSesion: () -> Unit,
    esAdmin: Boolean = false,
    onAbrirAdmin: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "¿Qué cocinamos hoy?",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Recetas con pollo",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    if (esAdmin) {
                        IconButton(onClick = onAbrirAdmin) {
                            Icon(Icons.Filled.Settings, contentDescription = "Panel Admin")
                        }
                    }
                    IconButton(onClick = onVerFavoritos) {
                        Icon(
                            Icons.Filled.Favorite,
                            contentDescription = "Ver favoritos",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = onCerrarSesion) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Cerrar sesión")
                    }
                }
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            if (aviso != null) {
                AvisoSinConexion(mensaje = aviso, onReintentar = onReintentar)
            }
            when (estado) {
                is UiState.Cargando -> Cargando(mensaje = "Buscando recetas…")
                is UiState.Error -> ErrorView(estado.mensaje, onReintentar)
                is UiState.Exito -> {
                    if (estado.datos.isEmpty()) {
                        MensajeVacio(emoji = "🍽️", texto = "No hay recetas para mostrar")
                    } else {
                        GrillaComidas(
                            items = estado.datos,
                            onItemClick = onItemClick,
                            onFavoritoClick = onFavoritoClick
                        )
                    }
                }
            }
        }
    }
}
@Composable
fun GrillaComidas(
    items: List<Item>,
    onItemClick: (String) -> Unit,
    onFavoritoClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 160.dp),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier.fillMaxSize()
    ) {
        items(items, key = { it.id }) { item ->
            ItemTarjeta(
                item = item,
                onClick = { onItemClick(item.id) },
                onFavoritoClick = { onFavoritoClick(item.id) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemTarjeta(
    item: Item,
    onClick: () -> Unit,
    onFavoritoClick: () -> Unit
) {
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Box {
            AsyncImage(
                model = item.imagenUrl,
                contentDescription = item.titulo,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            ) {
                IconButton(onClick = onFavoritoClick, modifier = Modifier.size(40.dp)) {
                    Icon(
                        imageVector = if (item.esFavorito) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = if (item.esFavorito) "Quitar de favoritos" else "Agregar a favoritos",
                        tint = if (item.esFavorito) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        Column(Modifier.padding(12.dp)) {
            Text(
                item.titulo,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                minLines = 2,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(4.dp))
            Text(
                item.subtitulo,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun AvisoSinConexion(mensaje: String, onReintentar: () -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.errorContainer,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.padding(start = 12.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onErrorContainer
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = mensaje,
                color = MaterialTheme.colorScheme.onErrorContainer,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            TextButton(onClick = onReintentar) {
                Text("Reintentar")
            }
        }
    }
}
@Composable
fun MensajeVacio(emoji: String, texto: String) {
    Column(
        Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(emoji, fontSize = 56.sp)
        Spacer(Modifier.height(12.dp))
        Text(
            texto,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}


@Preview(showBackground = true, name = "Éxito")
@Composable
private fun PreviewExito() = ValcarcelComidaTheme {
    ListaScreen(UiState.Exito(itemsPreview), null, {}, {}, {}, {}, {})
}

@Preview(showBackground = true, name = "Admin")
@Composable
private fun PreviewAdmin() = ValcarcelComidaTheme {
    ListaScreen(UiState.Exito(itemsPreview), null, {}, {}, {}, {}, {}, esAdmin = true)
}

@Preview(showBackground = true, name = "Sin conexión")
@Composable
private fun PreviewSinConexion() = ValcarcelComidaTheme {
    ListaScreen(UiState.Exito(itemsPreview), "Sin conexión: mostrando datos guardados", {}, {}, {}, {}, {})
}

@Preview(showBackground = true, name = "Cargando")
@Composable
private fun PreviewCargando() = ValcarcelComidaTheme {
    ListaScreen(UiState.Cargando, null, {}, {}, {}, {}, {})
}

@Preview(showBackground = true, name = "Error")
@Composable
private fun PreviewError() = ValcarcelComidaTheme {
    ListaScreen(UiState.Error("No hay conexión a internet"), null, {}, {}, {}, {}, {})
}

@Preview(showBackground = true, name = "Vacío")
@Composable
private fun PreviewVacio() = ValcarcelComidaTheme {
    ListaScreen(UiState.Exito(emptyList()), null, {}, {}, {}, {}, {})
}

private val itemsPreview = listOf(
    Item(id = "1", titulo = "Pollo de ejemplo 1", subtitulo = "Pollo", imagenUrl = null),
    Item(id = "2", titulo = "Pollo de ejemplo 2", subtitulo = "Pollo", imagenUrl = null, esFavorito = true),
    Item(id = "3", titulo = "Pollo de ejemplo 3", subtitulo = "Pollo", imagenUrl = null)
)