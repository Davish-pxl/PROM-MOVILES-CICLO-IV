package com.tuapp.saludpluscitas.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tuapp.saludpluscitas.ui.screens.home.HomeScreen
import com.tuapp.saludpluscitas.ui.screens.citas.MisCitasScreen
import com.tuapp.saludpluscitas.ui.screens.resultados.ResultadosScreen
import com.tuapp.saludpluscitas.ui.screens.perfil.PerfilScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Rutas.Home.ruta
    ) {
        composable(Rutas.Home.ruta) {
            HomeScreen(
                onAgendarCitaClick = { /* Acción al agendar cita */ },
                onMisCitasClick = { navController.navigate(Rutas.Citas.ruta) },
                onMisDatosClick = { navController.navigate(Rutas.Perfil.ruta) },
                onResultadosClick = { navController.navigate(Rutas.Resultados.ruta) },
                onEspecialidadClick = { idEspecialidad ->
                    // Navegación para cuando toque médicos
                },
                onVerTodasClick = { /* Acción para ver todas */ }
            )
        }

        composable(Rutas.Citas.ruta) {
            MisCitasScreen(navController)
        }

        composable(Rutas.Resultados.ruta) {
            ResultadosScreen(navController)
        }

        composable(Rutas.Perfil.ruta) {
            PerfilScreen(navController)
        }
    }
}