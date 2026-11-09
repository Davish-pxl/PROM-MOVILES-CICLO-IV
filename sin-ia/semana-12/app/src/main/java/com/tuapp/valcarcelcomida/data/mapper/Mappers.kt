package com.tuapp.valcarcelcomida.data.mapper

import com.tuapp.valcarcelcomida.data.remote.ApiService
import com.tuapp.valcarcelcomida.data.remote.dto.ItemDto
import com.tuapp.valcarcelcomida.domain.model.Item

fun ItemDto.aModelo(esFavorito: Boolean = false): Item = Item(
    id = idMeal,
    titulo = strMeal,
    subtitulo = ApiService.CATEGORIA,
    imagenUrl = strMealThumb,
    esFavorito = esFavorito
)