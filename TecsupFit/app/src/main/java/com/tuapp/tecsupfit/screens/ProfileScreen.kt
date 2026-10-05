package com.tuapp.tecsupfit.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
fun ProfileScreen(navController: NavController) {
    Scaffold(
        bottomBar = { TecsupBottomBar(navController, Screen.Perfil.route) },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Mi perfil", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier.align(Alignment.Start))
            Spacer(modifier = Modifier.height(32.dp))

            Box(
                modifier = Modifier.size(88.dp).clip(CircleShape).background(LightGreenBg),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "DV", color = PrimaryGreen, fontWeight = FontWeight.Bold, fontSize = 22.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "David Valcarcel", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black)
            Text(text = "Plan Premium", fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(32.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F2F1)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(20.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "14", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = Color.Black)
                        Text(text = "Clases", fontSize = 13.sp, color = Color.Gray)
                    }
                }

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F2F1)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(20.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "3", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = Color.Black)
                        Text(text = "Rachas", fontSize = 13.sp, color = Color.Gray)
                    }
                }
            }
        }
    }
}