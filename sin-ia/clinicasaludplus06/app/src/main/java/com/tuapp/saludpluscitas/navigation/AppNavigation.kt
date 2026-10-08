package com.tuapp.saludpluscitas.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tuapp.saludpluscitas.ui.screens.auth.SplashScreen
import com.tuapp.saludpluscitas.ui.screens.auth.RegistroScreen
import com.tuapp.saludpluscitas.ui.screens.auth.LoginScreen
import com.tuapp.saludpluscitas.ui.screens.home.HomeScreen
import com.tuapp.saludpluscitas.ui.screens.citas.MisCitasScreen
import com.tuapp.saludpluscitas.ui.screens.resultados.ResultadosScreen
import com.tuapp.saludpluscitas.ui.screens.perfil.PerfilScreen
import com.tuapp.saludpluscitas.ui.screens.agendamiento.EspecialidadesScreen
import com.tuapp.saludpluscitas.ui.screens.agendamiento.MedicosScreen
import com.tuapp.saludpluscitas.ui.screens.agendamiento.FechaHoraScreen
import com.tuapp.saludpluscitas.ui.screens.agendamiento.CitaExitosaScreen
import com.tuapp.saludpluscitas.ui.screens.agendamiento.ConfirmarCitaScreen
import com.tuapp.saludpluscitas.ui.screens.citas.DetalleCitaScreen
import com.tuapp.saludpluscitas.ui.screens.resultados.ResultadosScreen
import com.tuapp.saludpluscitas.ui.screens.notificaciones.NotificacionesScreen
import com.tuapp.saludpluscitas.ui.screens.auth.TerminosScreen
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Rutas.Splash.ruta
    ) {
        //Pantalla Splash
        composable(Rutas.Splash.ruta) {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(Rutas.Registro.ruta)
                },
                onNavigateToRegister = {
                    navController.navigate(Rutas.Login.ruta)
                }
            )
        }
        //Pantalla de Registro
        composable(Rutas.Registro.ruta) {
            RegistroScreen(
                onRegistroExitoso = {
                    navController.navigate(Rutas.Login.ruta) {
                        popUpTo(Rutas.Registro.ruta) { inclusive = true }
                    }
                },
                onBackToLogin = {
                    navController.navigate(Rutas.Login.ruta) {
                        popUpTo(Rutas.Registro.ruta) { inclusive = true }
                    }
                },
                onTerminosClick = {
                    navController.navigate(Rutas.Terminos.ruta)
                }
            )
        }
        //Pantalla de Iniciar Sesión
        composable(Rutas.Login.ruta) {
            LoginScreen(
                onLoginExitoso = {
                    navController.navigate(Rutas.Home.ruta) {
                        popUpTo(Rutas.Splash.ruta) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Rutas.Registro.ruta)
                }
            )
        }
        //Pantalla Principal
        composable(Rutas.Home.ruta) {
            HomeScreen(
                onAgendarCitaClick = { navController.navigate(Rutas.Especialidades.ruta) },
                onMisCitasClick = { navController.navigate(Rutas.Citas.ruta) },
                onMisDatosClick = { navController.navigate(Rutas.Perfil.ruta) },
                onResultadosClick = { navController.navigate(Rutas.Resultados.ruta) },
                onNotificacionesClick = { navController.navigate(Rutas.Notificaciones.ruta) },
                onEspecialidadClick = { idEspecialidad ->
                    navController.navigate("${Rutas.Medicos.ruta}/$idEspecialidad")
                },
                onVerTodasClick = { navController.navigate(Rutas.Especialidades.ruta) }
            )
        }
        //Resto de pantallas
        composable(Rutas.Citas.ruta) {
            MisCitasScreen(navController)
        }

        composable(Rutas.Resultados.ruta) {
            ResultadosScreen(navController)
        }

        composable(Rutas.Perfil.ruta) {
            PerfilScreen(navController)
        }

        composable(Rutas.Especialidades.ruta) {
            EspecialidadesScreen(navController)
        }
        composable(
            route = "${Rutas.Medicos.ruta}/{especialidadId}",
            arguments = listOf(
                navArgument("especialidadId") {
                    type = NavType.IntType
                    defaultValue = 0
                }
            )
        ) { backStackEntry ->
            val especialidadId = backStackEntry.arguments?.getInt("especialidadId") ?: 0
            MedicosScreen(navController, especialidadId)
        }
        composable(
            route = "${Rutas.FechaHora.ruta}/{medicoId}",
            arguments = listOf(
                navArgument("medicoId") {
                    type = NavType.IntType
                    defaultValue = 0
                }
            )
        ) { backStackEntry ->
            val medicoId = backStackEntry.arguments?.getInt("medicoId") ?: 0
            FechaHoraScreen(navController, medicoId)
        }
        composable(
            route = "${Rutas.ConfirmarCita.ruta}/{medicoId}/{fecha}/{hora}",
            arguments = listOf(
                navArgument("medicoId") { type = NavType.IntType },
                navArgument("fecha") { type = NavType.StringType },
                navArgument("hora") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val medicoId = backStackEntry.arguments?.getInt("medicoId") ?: 0
            val fecha = backStackEntry.arguments?.getString("fecha") ?: ""
            val hora = backStackEntry.arguments?.getString("hora") ?: ""

            ConfirmarCitaScreen(navController, medicoId, fecha, hora)
        }
        // Pantalla de Cita Exitosa
        composable(Rutas.CitaExitosa.ruta) {
            CitaExitosaScreen(navController)
        }
        composable(
            route = "${Rutas.DetalleCita.ruta}/{citaId}",
            arguments = listOf(
                navArgument("citaId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val citaId = backStackEntry.arguments?.getInt("citaId") ?: 0
            DetalleCitaScreen(navController, citaId)
        }

        // Reto Extra 13
        composable(Rutas.Resultados.ruta) {
            ResultadosScreen(navController)
        }

        // Reto Extra 14
        composable(Rutas.Notificaciones.ruta) {
            NotificacionesScreen(navController)
        }

        // Reto Extra 15
        composable(Rutas.Terminos.ruta) {
            TerminosScreen(navController)
        }
    }
}