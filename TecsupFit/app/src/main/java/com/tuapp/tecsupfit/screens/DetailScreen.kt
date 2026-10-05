package com.tuapp.tecsupfit.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.tuapp.tecsupfit.model.DatosFit
import com.tuapp.tecsupfit.navigation.Screen
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun DetailScreen(claseId: Int, navController: NavController) {
    val clase = DatosFit.listaClases.find { it.id == claseId } ?: DatosFit.listaClases.first()

    Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { navController.popBackStack() }
                ) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Atrás", tint = Color.Black)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Detalle de clase", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }

                Spacer(modifier = Modifier.height(24.dp))
                Box(
                    modifier = Modifier.fillMaxWidth().height(140.dp).clip(RoundedCornerShape(16.dp)).background(LightGreenBg),
                    contentAlignment = Alignment.Center
                ) {
                    Box(modifier = Modifier.size(50.dp).background(PrimaryGreen))
                }

                Spacer(modifier = Modifier.height(24.dp))
                Text(text = clase.nombre, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = clase.horario, fontSize = 14.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Entrenamiento funcional de alta intensidad. Cupos limitados.",
                    fontSize = 14.sp,
                    color = Color.DarkGray,
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(text = clase.cuposDisponibles, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = PrimaryGreen)
            }

            Button(
                onClick = {
                    val nombreEncoded = URLEncoder.encode(clase.nombre, StandardCharsets.UTF_8.toString())
                    val horarioEncoded = URLEncoder.encode(clase.horario, StandardCharsets.UTF_8.toString())
                    navController.navigate(Screen.Confirmacion.createRoute(nombreEncoded, horarioEncoded))
                },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                modifier = Modifier.fillMaxWidth().height(54.dp)
            ) {
                Text(text = "Reservar cupo", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}