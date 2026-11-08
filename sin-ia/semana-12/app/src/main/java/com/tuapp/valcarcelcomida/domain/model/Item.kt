package com.tuapp.valcarcelcomida.domain.model

data class Item(
    val id: String,
    val titulo: String,
    val subtitulo: String,
    val imagenUrl: String?,
    val esFavorito: Boolean = false,
    val area: String? = null,
    val instrucciones: String? = null,
    val ingredientes: List<String> = emptyList()
)