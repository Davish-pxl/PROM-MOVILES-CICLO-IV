package com.tuapp.valcarcelcomida.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ItemListaResponse(
    val meals: List<ItemDto>? = null
)

@Serializable
data class ItemDto(
    val idMeal: String,
    val strMeal: String,
    val strMealThumb: String? = null
)

@Serializable
data class ItemDetalleResponse(
    val meals: List<ItemDetalleDto>? = null
)

@Serializable
data class ItemDetalleDto(
    val idMeal: String,
    val strMeal: String,
    val strCategory: String? = null,
    val strArea: String? = null,
    val strInstructions: String? = null,
    val strMealThumb: String? = null,
    val strIngredient1: String? = null,
    val strIngredient2: String? = null,
    val strIngredient3: String? = null,
    val strIngredient4: String? = null,
    val strIngredient5: String? = null,
    val strIngredient6: String? = null,
    val strIngredient7: String? = null,
    val strIngredient8: String? = null,
    val strIngredient9: String? = null,
    val strIngredient10: String? = null,
    val strIngredient11: String? = null,
    val strIngredient12: String? = null,
    val strIngredient13: String? = null,
    val strIngredient14: String? = null,
    val strIngredient15: String? = null,
    val strIngredient16: String? = null,
    val strIngredient17: String? = null,
    val strIngredient18: String? = null,
    val strIngredient19: String? = null,
    val strIngredient20: String? = null
)