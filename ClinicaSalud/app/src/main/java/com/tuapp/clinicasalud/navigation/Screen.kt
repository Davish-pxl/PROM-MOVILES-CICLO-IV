package com.tuapp.clinicasalud.navigation

sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")

    object DetalleEspecialista : Screen("detalle_especialista/{medicoId}") {
        fun createRoute(medicoId: Int) = "detalle_especialista/$medicoId"
    }

    object ReservarConsulta : Screen("reservar_consulta/{medicoId}") {
        fun createRoute(medicoId: Int) = "reservar_consulta/$medicoId"
    }

    object ReservaExitosa : Screen("reserva_exitosa/{doctorName}/{fecha}/{hora}") {
        fun createRoute(doctorName: String, fecha: String, hora: String) =
            "reserva_exitosa/$doctorName/$fecha/$hora"
    }

    object MisConsultas : Screen("mis_consultas")
    object HistorialMedico : Screen("historial_medico")
    object Perfil : Screen("perfil")
}