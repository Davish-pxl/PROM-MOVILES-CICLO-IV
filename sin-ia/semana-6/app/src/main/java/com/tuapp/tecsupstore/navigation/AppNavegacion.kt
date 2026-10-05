package com.tuapp.tecsupstore.navigation

import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.*
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tuapp.tecsupstore.screens.*
import kotlinx.coroutines.launch

@Composable
fun AppNavegacion() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Estado global reactivo para la lista de IDs de productos favoritos
    val favoritosList = remember { mutableStateListOf<Int>() }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    AppDrawer(
        drawerState = drawerState,
        scope = scope,
        navController = navController,
        currentRoute = currentRoute,
        favoritosCount = favoritosList.size
    ) {
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    navController = navController,
                    onOpenDrawer = { scope.launch { drawerState.open() } }
                )
            }
            composable(Screen.List.route) {
                ListScreen(
                    navController = navController,
                    favoritosList = favoritosList,
                    onOpenDrawer = { scope.launch { drawerState.open() } }
                )
            }
            composable(Screen.Favoritos.route) {
                FavoritosScreen(
                    navController = navController,
                    favoritosList = favoritosList,
                    onOpenDrawer = { scope.launch { drawerState.open() } }
                )
            }
            composable(Screen.Profile.route) {
                ProfileScreen(
                    navController = navController,
                    onOpenDrawer = { scope.launch { drawerState.open() } }
                )
            }
            composable(
                route = Screen.Detail.route,
                arguments = listOf(
                    navArgument("itemId") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val itemId = backStackEntry.arguments?.getInt("itemId") ?: 1
                DetailScreen(navController = navController, itemId = itemId)
            }
        }
    }
}