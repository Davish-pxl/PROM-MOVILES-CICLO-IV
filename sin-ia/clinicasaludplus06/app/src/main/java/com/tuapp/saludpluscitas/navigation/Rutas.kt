package com.tuapp.saludpluscitas.navigation

sealed class Rutas(val ruta: String) {
    object Login : Rutas("login")
    object Registro : Rutas("registro")
    object Home : Rutas("home")
    object Citas : Rutas("citas")
    object Resultados : Rutas("resultados")
    object Perfil : Rutas("perfil")
}
