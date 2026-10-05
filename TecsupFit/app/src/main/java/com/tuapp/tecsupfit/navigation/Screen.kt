package com.tuapp.tecsupfit.navigation

sealed class Screen(val route: String) {
    object Inicio : Screen("inicio_fit")
    object Detalle : Screen("detalle/{claseId}") {
        fun createRoute(claseId: Int) = "detalle/$claseId"
    }
    object Confirmacion : Screen("confirmacion/{nombre}/{horario}") {
        fun createRoute(nombre: String, horario: String) = "confirmacion/$nombre/$horario"
    }
    object Reservas : Screen("reservas_fit")
    object Rutinas : Screen("rutinas_fit")
    object Perfil : Screen("perfil_fit")
}