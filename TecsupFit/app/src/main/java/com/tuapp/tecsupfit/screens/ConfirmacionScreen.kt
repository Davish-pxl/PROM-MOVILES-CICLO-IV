package com.tuapp.tecsupfit.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.tuapp.tecsupfit.navigation.Screen

@Composable
fun ConfirmacionScreen(nombreClase: String, horario: String, navController: NavController) {
    Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier.size(96.dp).clip(CircleShape).background(LightGreenBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = "Éxito", tint = PrimaryGreen, modifier = Modifier.size(48.dp))
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text(text = "¡Cupo reservado!", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = nombreClase, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.DarkGray)
                Text(text = horario, fontSize = 14.sp, color = Color.Gray)
            }

            Button(
                onClick = {
                    navController.navigate(Screen.Reservas.route) {
                        popUpTo(Screen.Inicio.route)
                    }
                },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF0F2F1)),
                modifier = Modifier.fillMaxWidth().height(54.dp)
            ) {
                Text(text = "Ver mis reservas", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = PrimaryGreen)
            }
        }
    }
}