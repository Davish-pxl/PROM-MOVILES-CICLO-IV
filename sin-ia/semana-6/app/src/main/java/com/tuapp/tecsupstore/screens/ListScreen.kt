package com.tuapp.tecsupstore.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.ShoppingBag
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(navController: NavController) {
    val productos = listOf(
        Productos(1, "Audifonos", "Audio", 89.00, "TEC-001", 10, 4.8, "Audífonos inalámbricos"),
        Productos(2, "Smartwatch", "Tecnología", 199.00, "TEC-002", 25, 4.5, "Reloj inteligente"),
        Productos(3, "Funda celular", "Accesorios", 25.00, "TEC-003", 8, 4.7, "Funda protectora")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "TECSUP Store",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Mas vendidos",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF4A148C),
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        LazyColumn(
            contentPadding = padding,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(productos) { producto ->
                CardProductoItem(producto = producto, navController = navController)
            }
        }
    }
}

@Composable
fun CardProductoItem(producto: Productos, navController: NavController) {
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
                        contentDescription = "Opciones"
                    )
                }

                DropdownMenu(
                    expanded = menuDesplegado,
                    onDismissRequest = { menuDesplegado = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("♥ Favoritos") },
                        onClick = { menuDesplegado = false }
                    )
                    HorizontalDivider()
                    DropdownMenuItem(
                        text = { Text("↗ Compartir") },
                        onClick = { menuDesplegado = false }
                    )
                    HorizontalDivider()
                    DropdownMenuItem(
                        text = { Text("⚠ Reportar") },
                        onClick = { menuDesplegado = false }
                    )
                }
            }
        }
    }
}