package com.tuapp.valcarcelcomida.data.mapper

import com.tuapp.valcarcelcomida.data.local.ItemEntity
import com.tuapp.valcarcelcomida.data.remote.ApiService
import com.tuapp.valcarcelcomida.data.remote.dto.ItemDetalleDto
import com.tuapp.valcarcelcomida.data.remote.dto.ItemDto
import com.tuapp.valcarcelcomida.domain.model.Item

fun ItemDto.aEntity(esFavorito: Boolean = false, titulo: String = strMeal): ItemEntity = ItemEntity(
    id = idMeal,
    titulo = titulo,
    subtitulo = categoriaEnEspanol(ApiService.CATEGORIA),
    imagenUrl = strMealThumb,
    esFavorito = esFavorito
)

fun ItemEntity.aModelo(): Item = Item(
    id = id,
    titulo = titulo,
    subtitulo = subtitulo,
    imagenUrl = imagenUrl,
    esFavorito = esFavorito
)

fun ItemDetalleDto.aModelo(esFavorito: Boolean = false): Item = Item(
    id = idMeal,
    titulo = strMeal,
    subtitulo = categoriaEnEspanol(strCategory ?: ApiService.CATEGORIA),
    imagenUrl = strMealThumb,
    esFavorito = esFavorito,
    area = strArea?.let { areaEnEspanol(it) },
    instrucciones = strInstructions,
    ingredientes = ingredientes()
)

private fun ItemDetalleDto.ingredientes(): List<String> = listOf(
    strIngredient1, strIngredient2, strIngredient3, strIngredient4, strIngredient5,
    strIngredient6, strIngredient7, strIngredient8, strIngredient9, strIngredient10,
    strIngredient11, strIngredient12, strIngredient13, strIngredient14, strIngredient15,
    strIngredient16, strIngredient17, strIngredient18, strIngredient19, strIngredient20
).mapNotNull { it?.trim()?.takeIf { texto -> texto.isNotEmpty() } }

fun categoriaEnEspanol(categoria: String): String = when (categoria) {
    "Chicken" -> "Pollo"
    "Beef" -> "Res"
    "Pork" -> "Cerdo"
    "Lamb" -> "Cordero"
    "Goat" -> "Cabra"
    "Seafood" -> "Mariscos"
    "Pasta" -> "Pasta"
    "Vegetarian" -> "Vegetariana"
    "Vegan" -> "Vegana"
    "Dessert" -> "Postres"
    "Breakfast" -> "Desayuno"
    "Side" -> "Acompañamiento"
    "Starter" -> "Entrada"
    "Miscellaneous" -> "Varios"
    else -> categoria
}

private fun areaEnEspanol(area: String): String = when (area) {
    "American" -> "Estadounidense"
    "British" -> "Británica"
    "Canadian" -> "Canadiense"
    "Chinese" -> "China"
    "Croatian" -> "Croata"
    "Dutch" -> "Neerlandesa"
    "Egyptian" -> "Egipcia"
    "Filipino" -> "Filipina"
    "French" -> "Francesa"
    "Greek" -> "Griega"
    "Indian" -> "India"
    "Irish" -> "Irlandesa"
    "Italian" -> "Italiana"
    "Jamaican" -> "Jamaiquina"
    "Japanese" -> "Japonesa"
    "Kenyan" -> "Keniana"
    "Malaysian" -> "Malaya"
    "Mexican" -> "Mexicana"
    "Moroccan" -> "Marroquí"
    "Polish" -> "Polaca"
    "Portuguese" -> "Portuguesa"
    "Russian" -> "Rusa"
    "Spanish" -> "Española"
    "Thai" -> "Tailandesa"
    "Tunisian" -> "Tunecina"
    "Turkish" -> "Turca"
    "Ukrainian" -> "Ucraniana"
    "Vietnamese" -> "Vietnamita"
    "Unknown" -> "Internacional"
    else -> area
}