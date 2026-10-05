package com.tuapp.clinicasalud.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tuapp.clinicasalud.screens.*

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route
    ) {
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onOpenDrawer = {},
                onDoctorClick = { medicoId ->
                    navController.navigate(Screen.DetalleEspecialista.createRoute(medicoId))
                },
                onNavigateMisCitas = {
                    navController.navigate(Screen.MisConsultas.route)
                },
                onNavigateHistorial = {
                    navController.navigate(Screen.HistorialMedico.route)
                },
                onNavigatePerfil = {
                    navController.navigate(Screen.Perfil.route)
                }
            )
        }
        composable(
            route = Screen.DetalleEspecialista.route,
            arguments = listOf(navArgument("medicoId") { type = NavType.IntType })
        ) { backStackEntry ->
            val medicoId = backStackEntry.arguments?.getInt("medicoId") ?: 1
            DetalleEspecialistaScreen(
                medicoId = medicoId,
                onBackClick = { navController.popBackStack() },
                onBookClick = { id ->
                    navController.navigate(Screen.ReservarConsulta.createRoute(id))
                }
            )
        }
        composable(
            route = Screen.ReservarConsulta.route,
            arguments = listOf(navArgument("medicoId") { type = NavType.IntType })
        ) { backStackEntry ->
            val medicoId = backStackEntry.arguments?.getInt("medicoId") ?: 1
            ReservarConsultaScreen(
                medicoId = medicoId,
                onBackClick = { navController.popBackStack() },
                onConfirmBooking = { doctorName, fecha, hora ->
                    navController.navigate(Screen.ReservaExitosa.createRoute(doctorName, fecha, hora))
                }
            )
        }
        composable(
            route = Screen.ReservaExitosa.route,
            arguments = listOf(
                navArgument("doctorName") { type = NavType.StringType },
                navArgument("fecha") { type = NavType.StringType },
                navArgument("hora") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val doctorName = backStackEntry.arguments?.getString("doctorName") ?: ""
            val fecha = backStackEntry.arguments?.getString("fecha") ?: ""
            val hora = backStackEntry.arguments?.getString("hora") ?: ""
            val fechaInfoCompleta = "$fecha, $hora"

            ReservaExitosaScreen(
                doctorName = doctorName,
                fechaInfo = fechaInfoCompleta,
                onGoHome = {
                    navController.navigate(Screen.MisConsultas.route) {
                        popUpTo(Screen.Dashboard.route)
                    }
                }
            )
        }
        composable(Screen.MisConsultas.route) {
            MisConsultasScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(Screen.HistorialMedico.route) {
            HistorialMedicoScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(Screen.Perfil.route) {
            PerfilScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}