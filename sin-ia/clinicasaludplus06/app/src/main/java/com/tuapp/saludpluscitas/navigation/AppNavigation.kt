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
                    // "Ya tengo cuenta" -> Va a Iniciar Sesión (Login)
                    navController.navigate(Rutas.Registro.ruta)
                },
                onNavigateToRegister = {
                    // "Comenzar" / "Registrarme" -> Va a Registro
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
                    // Tras un login correcto, manda a Home limpiando autenticación
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
    }
}