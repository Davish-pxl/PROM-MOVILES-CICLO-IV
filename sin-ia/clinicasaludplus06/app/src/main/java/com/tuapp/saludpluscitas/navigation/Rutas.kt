package com.tuapp.saludpluscitas.navigation

sealed class Rutas(val ruta: String) {
    object Splash : Rutas("splash")
    object Login : Rutas("login")
    object Registro : Rutas("registro")
    object Home : Rutas("home")
    object Citas : Rutas("citas")
    object Resultados : Rutas("resultados")
    object Perfil : Rutas("perfil")
    object Especialidades : Rutas("especialidades")
    object Medicos : Rutas("medicos")
    object Terminos : Rutas("terminos")
    object FechaHora : Rutas("fecha_hora")
    object ConfirmarCita : Rutas("confirmar_cita")
}
