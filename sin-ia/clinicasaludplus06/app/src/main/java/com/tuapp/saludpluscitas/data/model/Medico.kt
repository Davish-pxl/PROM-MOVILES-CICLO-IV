package com.tuapp.saludpluscitas.data.model

data class Medico(
    val id: Int,
    val nombre: String,
    val especialidadId: Int,
    val horario: String,
    val cmp: String
)