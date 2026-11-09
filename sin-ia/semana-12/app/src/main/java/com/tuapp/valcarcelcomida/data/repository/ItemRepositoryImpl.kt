package com.tuapp.valcarcelcomida.data.repository

import com.tuapp.valcarcelcomida.data.mapper.aModelo
import com.tuapp.valcarcelcomida.data.remote.ApiService
import com.tuapp.valcarcelcomida.domain.model.Item
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import java.io.IOException

class ItemRepositoryImpl(
    private val api: ApiService
) : ItemRepository {
    private val memoria = MutableStateFlow<List<Item>>(emptyList())

    override val items: Flow<List<Item>> = memoria
    override val favoritos: Flow<List<Item>> = memoria.map { lista -> lista.filter { it.esFavorito } }

    override suspend fun refrescar(): Result<Int> = try {
        val idsFavoritos = memoria.value.filter { it.esFavorito }.map { it.id }.toSet()
        val nuevos = api.listar().meals.orEmpty()
            .map { it.aModelo(esFavorito = it.idMeal in idsFavoritos) }
        memoria.value = nuevos
        Result.success(nuevos.size)
    } catch (e: IOException) {
        Result.failure(e)
    } catch (e: HttpException) {
        Result.failure(e)
    } catch (e: SerializationException) {
        Result.failure(e)
    }

    override suspend fun obtenerDetalle(id: String): Result<Item> {
        return try {
            val dto = api.detalle(id).meals?.firstOrNull()
                ?: return Result.failure(NoSuchElementException("No se encontró la comida"))
            val esFavorito = memoria.value.any { it.id == id && it.esFavorito }
            Result.success(dto.aModelo(esFavorito))
        } catch (e: IOException) {
            Result.failure(e)
        } catch (e: HttpException) {
            Result.failure(e)
        } catch (e: SerializationException) {
            Result.failure(e)
        }
    }

    override suspend fun alternarFavorito(id: String) {
        memoria.update { lista ->
            lista.map { if (it.id == id) it.copy(esFavorito = !it.esFavorito) else it }
        }
    }
    override suspend fun cantidadGuardada(): Int = memoria.value.size

    override suspend fun cantidadFavoritos(): Int = memoria.value.count { it.esFavorito }

    override suspend fun limpiarCache() {
        memoria.value = emptyList()
    }
}