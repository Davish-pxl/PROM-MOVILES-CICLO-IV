package com.tuapp.tecsupfit.model

data class ClaseFit(
    val id: Int,
    val nombre: String,
    val horario: String,
    val sala: String,
    val duracion: String,
    val descripcion: String,
    val cuposDisponibles: String,
    val tipoFiltro: String
)

data class ReservaFit(
    val id: Int,
    val nombreClase: String,
    val horarioInfo: String,
    val estado: String
)

object DatosFit {
    val listaClases = listOf(
        ClaseFit(1, "Yoga funcional", "7:00 am · Sala 2", "Sala 2", "45 min", "Clase enfocada en la flexibilidad y control postural.", "6 de 15 cupos disponibles", "Hoy"),
        ClaseFit(2, "Cross Training", "6:00 pm · Sala 1", "Sala 1", "45 min", "Entrenamiento funcional de alta intensidad.", "8 de 12 cupos disponibles", "Hoy"),
        ClaseFit(3, "Spinning", "7:30 pm · Sala 3", "Sala 3", "50 min", "Cardio intenso sobre bicicleta estática.", "4 de 20 cupos disponibles", "Esta semana")
    )

    val misReservasIniciales = mutableListOf(
        ReservaFit(1, "Cross Training", "Hoy, 6:00 pm", "Confirmada"),
        ReservaFit(2, "Yoga funcional", "Ayer, 7:00 am", "Completada")
    )
}