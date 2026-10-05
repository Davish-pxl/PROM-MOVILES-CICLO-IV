package com.tuapp.clinicasalud.model

data class Especialista(
    val id: Int,
    val nombreCompleto: String,
    val areaMedica: String,
    val anosExperiencia: Int,
    val puntaje: Double,
    val totalReseñas: Int,
    val bio: String
)

data class OpcionFecha(
    val diaSemana: String,
    val numeroDia: String
)

data class Consulta(
    val id: Int,
    val nombreDoctor: String,
    val fechaHoraInfo: String,
    val estadoActual: String // "Confirmada" o "Completada"
)

object DatosLocales {
    val categorias = listOf("General", "Cardiología", "Pediatría", "Dermatología")

    val listaEspecialistas = listOf(
        Especialista(
            id = 1,
            nombreCompleto = "Dra. Ana Torres",
            areaMedica = "Cardiología",
            anosExperiencia = 12,
            puntaje = 4.9,
            totalReseñas = 128,
            bio = "Especialista en arritmias e hipertensión, formación en la Clínica Mayo."
        ),
        Especialista(
            id = 2,
            nombreCompleto = "Dr. Luis Vega",
            areaMedica = "Pediatría",
            anosExperiencia = 8,
            puntaje = 4.7,
            totalReseñas = 95,
            bio = "Especialista en desarrollo infantil y nutrición pediátrica."
        ),
        Especialista(
            id = 3,
            nombreCompleto = "Dra. Rosa Díaz",
            areaMedica = "Dermatología",
            anosExperiencia = 10,
            puntaje = 4.8,
            totalReseñas = 110,
            bio = "Especialista en dermatología clínica y estética avanzada."
        )
    )

    val fechasDisponibles = listOf(
        OpcionFecha("Jue", "26"),
        OpcionFecha("Vie", "27"),
        OpcionFecha("Sáb", "28")
    )

    val horasDisponibles = listOf("9:00", "10:30", "3:00")

    val consultasIniciales = mutableListOf(
        Consulta(1, "Dra. Ana Torres", "Viernes 27, 10:30 am", "Confirmada"),
        Consulta(2, "Dr. Luis Vega", "Miércoles 15, 3:00 pm", "Completada")
    )
}