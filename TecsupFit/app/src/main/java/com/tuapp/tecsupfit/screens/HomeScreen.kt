package com.tuapp.tecsupfit.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.tuapp.tecsupfit.model.DatosFit
import com.tuapp.tecsupfit.navigation.Screen

val PrimaryGreen = Color(0xFF0A5C36)
val LightGreenBg = Color(0xFFE6F4ED)

@Composable
fun HomeScreen(navController: NavController) {
    var filtroSeleccionado by remember { mutableStateOf("Hoy") }

    val clasesFiltradas = if (filtroSeleccionado == "Hoy") {
        DatosFit.listaClases.filter { it.tipoFiltro == "Hoy" }
    } else {
        DatosFit.listaClases
    }

    Scaffold(
        bottomBar = { TecsupBottomBar(navController, Screen.Inicio.route) },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth().background(PrimaryGreen).padding(24.dp)
            ) {
                Column {
                    Text(text = "TECSUP Fit", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Hola, David", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                // Chips de filtro ("Hoy" y "Esta semana")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Hoy", "Esta semana").forEach { filtro ->
                        val isSelected = filtro == filtroSeleccionado
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) PrimaryGreen else Color(0xFFF0F2F1),
                            modifier = Modifier.height(36.dp).clickable { filtroSeleccionado = filtro }
                        ) {
                            Box(modifier = Modifier.padding(horizontal = 20.dp), contentAlignment = Alignment.Center) {
                                Text(
                                    text = filtro,
                                    color = if (isSelected) Color.White else Color.DarkGray,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                Text(text = "Clases disponibles", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.DarkGray)
                Spacer(modifier = Modifier.height(12.dp))

                // Lista de clases
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(clasesFiltradas) { clase ->
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F9F8)),
                            modifier = Modifier.fillMaxWidth().clickable {
                                navController.navigate(Screen.Detalle.createRoute(clase.id))
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier.size(48.dp).background(LightGreenBg, shape = RoundedCornerShape(12.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Box(modifier = Modifier.size(20.dp).background(PrimaryGreen))
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(text = clase.nombre, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = clase.horario, fontSize = 13.sp, color = Color.Gray)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}