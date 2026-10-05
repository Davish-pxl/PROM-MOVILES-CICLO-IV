package com.tuapp.clinicasalud.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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

@Composable
fun PerfilScreen(
    onBackClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickableWithNoIndication(onClick = onBackClick)
            ) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Atrás", tint = Color.Black)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Perfil", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            }

            Spacer(modifier = Modifier.height(32.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(LightPurpleCard),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "DV", color = PrimaryPurple, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "David Valcarcel", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black)
                Text(text = "david.valcarcel@tecsup.edu.pe", fontSize = 14.sp, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(32.dp))

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = LightPurpleCard),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Información Personal", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.Black)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Teléfono: +51 987 654 321", fontSize = 14.sp, color = Color.DarkGray)
                    Text(text = "Tipo de Sangre: O+", fontSize = 14.sp, color = Color.DarkGray)
                    Text(text = "Seguro Médico: Rimac Salud", fontSize = 14.sp, color = Color.DarkGray)
                }
            }
        }
    }
}