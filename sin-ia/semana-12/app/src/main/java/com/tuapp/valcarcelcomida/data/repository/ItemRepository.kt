package com.tuapp.valcarcelcomida.data.repository

import com.tuapp.valcarcelcomida.domain.model.Categoria
import com.tuapp.valcarcelcomida.domain.model.Item
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface ItemRepository {
    val items: Flow<List<Item>>
    val favoritos: Flow<List<Item>>
    suspend fun refrescar(): Result<Int>
    suspend fun obtenerDetalle(id: String): Result<Item>
    suspend fun alternarFavorito(id: String)
    suspend fun cantidadGuardada(): Int
    suspend fun cantidadFavoritos(): Int
    suspend fun limpiarCache()

    val categoriasDisponibles: List<Categoria>
    val categoria: StateFlow<String>
    fun seleccionarCategoria(clave: String)
}
interface Traductor {
    suspend fun traducir(texto: String): String
}