package com.tuapp.tecsupfit.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.tuapp.tecsupfit.navigation.Screen

@Composable
fun RutinasFitScreen(navController: NavController) {
    Scaffold(
        bottomBar = { TecsupBottomBar(navController, Screen.Rutinas.route) },
        containerColor = Color.White
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Rutinas asignadas próximamente", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.Gray)
        }
    }
}