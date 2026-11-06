package com.tuapp.authfirebaseappvalcarcel

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tuapp.authfirebaseappvalcarcel.ui.theme.AuthFireBaseAppValcarcelTheme

object Destinations {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val CRUD = "crud"
}

@Composable
fun AuthApp() {
    AuthFireBaseAppValcarcelTheme {
        val navController = rememberNavController()
        AuthNavGraph(navController)
    }
}

@Composable
fun AuthNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Destinations.LOGIN
    ) {
        composable(Destinations.LOGIN) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Destinations.REGISTER)
                },
                onLoginSuccess = {
                    navController.navigate(Destinations.HOME) {
                        popUpTo(Destinations.LOGIN) { inclusive = true }
                    }
                }
            )
        }
        composable(Destinations.REGISTER) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Destinations.HOME) {
                        popUpTo(Destinations.LOGIN) { inclusive = true }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(Destinations.HOME) {
            HomeScreen(
                onNavigateToCrud = {
                    navController.navigate(Destinations.CRUD)
                },
                onLogout = {
                    navController.navigate(Destinations.LOGIN) {
                        popUpTo(Destinations.HOME) { inclusive = true }
                    }
                }
            )
        }
        composable(Destinations.CRUD) {
            CrudScreen(
                onBackToHome = {
                    navController.popBackStack()
                }
            )
        }
    }
}