package com.tuapp.tecsupfit.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.tuapp.tecsupfit.model.DatosFit
import com.tuapp.tecsupfit.navigation.Screen

@Composable
fun ReservasFitScreen(navController: NavController) {
    Scaffold(
        bottomBar = { TecsupBottomBar(navController, Screen.Reservas.route) },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(24.dp)
        ) {
            Text(text = "Mis reservas", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Spacer(modifier = Modifier.height(20.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(DatosFit.misReservasIniciales) { reserva ->
                    val isConfirmada = reserva.estado == "Confirmada"
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F9F8)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (isConfirmada) {
                                Box(modifier = Modifier.width(6.dp).height(80.dp).background(PrimaryGreen))
                            }
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(text = reserva.nombreClase, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = reserva.horarioInfo, fontSize = 13.sp, color = Color.Gray)
                                Spacer(modifier = Modifier.height(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isConfirmada) LightGreenBg else Color(0xFFE5E8E7)
                                ) {
                                    Box(modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)) {
                                        Text(
                                            text = reserva.estado,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = if (isConfirmada) PrimaryGreen else Color.DarkGray
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}