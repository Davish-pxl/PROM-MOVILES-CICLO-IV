package com.tuapp.tecsupfit.screens

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.tuapp.tecsupfit.navigation.Screen

@Composable
fun TecsupBottomBar(navController: NavController, currentRoute: String) {
    NavigationBar(containerColor = Color.White) {
        listOf("Inicio" to Screen.Inicio.route, "Reservas" to Screen.Reservas.route, "Rutinas" to Screen.Rutinas.route, "Perfil" to Screen.Perfil.route).forEach { (label, route) ->
            NavigationBarItem(
                selected = currentRoute == route,
                onClick = { if (currentRoute != route) navController.navigate(route) },
                icon = { RadioButton(selected = currentRoute == route, onClick = null) },
                label = { Text(label) }
            )
        }
    }
}