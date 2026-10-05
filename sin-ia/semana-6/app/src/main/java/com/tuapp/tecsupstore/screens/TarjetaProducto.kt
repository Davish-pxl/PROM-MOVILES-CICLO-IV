package com.tuapp.tecsupstore.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.tuapp.tecsupstore.model.Productos
import com.tuapp.tecsupstore.navigation.Screen

@Composable
fun TarjetaProducto(
    producto: Productos,
    navController: NavController,
    isFavorito: Boolean = false,
    onToggleFavorito: () -> Unit = {}
) {
    var menuDesplegado by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F2F9)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navController.navigate(Screen.Detail.createRoute(producto.id))
            }
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFE1D5E7)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingBag,
                    contentDescription = null,
                    tint = Color(0xFF4A148C)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = producto.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "S/ ${String.format("%.2f", producto.precio)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }

            Box {
                IconButton(onClick = { menuDesplegado = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Opciones del producto"
                    )
                }

                DropdownMenu(
                    expanded = menuDesplegado,
                    onDismissRequest = { menuDesplegado = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(if (isFavorito) "Quitar de Favoritos" else "Favoritos") },
                        leadingIcon = {
                            Icon(
                                imageVector = if (isFavorito) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favoritos",
                                tint = if (isFavorito) Color.Red else MaterialTheme.colorScheme.onSurface
                            )
                        },
                        onClick = {
                            onToggleFavorito()
                            menuDesplegado = false
                        }
                    )
                    HorizontalDivider()
                    DropdownMenuItem(
                        text = { Text("Compartir") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Compartir"
                            )
                        },
                        onClick = {
                            menuDesplegado = false
                        }
                    )
                    HorizontalDivider()
                    DropdownMenuItem(
                        text = { Text("Reportar") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Reportar"
                            )
                        },
                        onClick = {
                            menuDesplegado = false
                        }
                    )
                }
            }
        }
    }
}