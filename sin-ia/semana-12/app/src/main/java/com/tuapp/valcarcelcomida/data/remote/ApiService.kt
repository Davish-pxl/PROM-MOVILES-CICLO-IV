package com.tuapp.valcarcelcomida.data.remote

import com.tuapp.valcarcelcomida.data.remote.dto.ItemDetalleResponse
import com.tuapp.valcarcelcomida.data.remote.dto.ItemListaResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("filter.php")
    suspend fun listar(@Query("c") categoria: String = CATEGORIA): ItemListaResponse

    @GET("lookup.php")
    suspend fun detalle(@Query("i") id: String): ItemDetalleResponse

    companion object {

        const val CATEGORIA = "Chicken"

        val CATEGORIAS = listOf(
            "Chicken", "Beef", "Pork", "Lamb", "Seafood",
            "Pasta", "Vegetarian", "Dessert", "Breakfast", "Side"
        )
    }
}