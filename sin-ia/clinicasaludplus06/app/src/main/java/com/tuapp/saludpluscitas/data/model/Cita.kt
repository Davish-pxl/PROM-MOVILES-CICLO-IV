package com.tuapp.saludpluscitas.data.model

data class Cita(
    val id: Int,
    val usuarioEmail: String,
    val medicoId: Int,
    val fecha: String,
    val hora: String
)