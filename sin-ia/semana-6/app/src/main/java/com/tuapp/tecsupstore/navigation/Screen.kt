package com.tuapp.tecsupstore.navigation

sealed class Screen(val route: String) {
    object Home : Screen(route = "home")
    object List : Screen(route = "list")
    object Favoritos : Screen(route = "favoritos")
    object Profile : Screen(route = "profile")
    object Detail : Screen(route = "detail/{itemId}") {
        fun createRoute(itemId: Int): String = "detail/$itemId"
    }
}