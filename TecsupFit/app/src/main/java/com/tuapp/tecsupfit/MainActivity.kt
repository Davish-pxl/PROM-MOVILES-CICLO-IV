package com.tuapp.tecsupfit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tuapp.tecsupfit.navigation.Screen
import com.tuapp.tecsupfit.screens.*
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = Screen.Inicio.route) {
                        composable(Screen.Inicio.route) { HomeScreen(navController) }
                        composable(
                            route = Screen.Detalle.route,
                            arguments = listOf(navArgument("claseId") { type = NavType.IntType })
                        ) { backStackEntry ->
                            DetailScreen(claseId = backStackEntry.arguments?.getInt("claseId") ?: 1, navController = navController)
                        }
                        composable(
                            route = Screen.Confirmacion.route,
                            arguments = listOf(
                                navArgument("nombre") { type = NavType.StringType },
                                navArgument("horario") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val nombreEnc = backStackEntry.arguments?.getString("nombre") ?: ""
                            val horarioEnc = backStackEntry.arguments?.getString("horario") ?: ""
                            val nombre = URLDecoder.decode(nombreEnc, StandardCharsets.UTF_8.toString())
                            val horario = URLDecoder.decode(horarioEnc, StandardCharsets.UTF_8.toString())
                            ConfirmacionScreen(nombreClase = nombre, horario = horario, navController = navController)
                        }
                        composable(Screen.Reservas.route) { ReservasFitScreen(navController) }
                        composable(Screen.Rutinas.route) { RutinasFitScreen(navController) }
                        composable(Screen.Perfil.route) { ProfileScreen(navController) }
                    }
                }
            }
        }
    }
}