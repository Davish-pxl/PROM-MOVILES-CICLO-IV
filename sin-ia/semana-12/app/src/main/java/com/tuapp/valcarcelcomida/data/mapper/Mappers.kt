package com.tuapp.valcarcelcomida.data.mapper

import com.tuapp.valcarcelcomida.data.remote.ApiService
import com.tuapp.valcarcelcomida.data.remote.dto.ItemDetalleDto
import com.tuapp.valcarcelcomida.data.remote.dto.ItemDto
import com.tuapp.valcarcelcomida.domain.model.Item

fun ItemDto.aModelo(esFavorito: Boolean = false): Item = Item(
    id = idMeal,
    titulo = strMeal,
    subtitulo = ApiService.CATEGORIA,
    imagenUrl = strMealThumb,
    esFavorito = esFavorito
)

fun ItemDetalleDto.aModelo(esFavorito: Boolean = false): Item = Item(
    id = idMeal,
    titulo = strMeal,
    subtitulo = strCategory ?: ApiService.CATEGORIA,
    imagenUrl = strMealThumb,
    esFavorito = esFavorito,
    area = strArea,
    instrucciones = strInstructions,
    ingredientes = ingredientes()
)

private fun ItemDetalleDto.ingredientes(): List<String> = listOf(
    strIngredient1, strIngredient2, strIngredient3, strIngredient4, strIngredient5,
    strIngredient6, strIngredient7, strIngredient8, strIngredient9, strIngredient10,
    strIngredient11, strIngredient12, strIngredient13, strIngredient14, strIngredient15,
    strIngredient16, strIngredient17, strIngredient18, strIngredient19, strIngredient20
).mapNotNull { it?.trim()?.takeIf { texto -> texto.isNotEmpty() } }