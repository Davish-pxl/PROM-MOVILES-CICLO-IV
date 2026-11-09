package com.tuapp.valcarcelcomida.data.repository

import com.tuapp.valcarcelcomida.data.local.ItemDao
import com.tuapp.valcarcelcomida.data.mapper.aEntity
import com.tuapp.valcarcelcomida.data.mapper.aModelo
import com.tuapp.valcarcelcomida.data.remote.ApiService
import com.tuapp.valcarcelcomida.domain.model.Item
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import java.io.IOException

class ItemRepositoryImpl(
    private val api: ApiService,
    private val dao: ItemDao
) : ItemRepository {

    override val items: Flow<List<Item>> =
        dao.todos().map { lista -> lista.map { it.aModelo() } }

    override val favoritos: Flow<List<Item>> =
        dao.favoritos().map { lista -> lista.map { it.aModelo() } }

    override suspend fun refrescar(): Result<Int> = try {

        val idsFavoritos = dao.idsFavoritos().toSet()
        val nuevos = api.listar().meals.orEmpty()
            .map { it.aEntity(esFavorito = it.idMeal in idsFavoritos) }
        dao.guardarTodos(nuevos)
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

            val esFavorito = id in dao.idsFavoritos()
            Result.success(dto.aModelo(esFavorito))
        } catch (e: IOException) {
            Result.failure(e)
        } catch (e: HttpException) {
            Result.failure(e)
        } catch (e: SerializationException) {
            Result.failure(e)
        }
    }

    override suspend fun alternarFavorito(id: String) = dao.alternarFavorito(id)

    override suspend fun cantidadGuardada(): Int = dao.contar()

    override suspend fun cantidadFavoritos(): Int = dao.contarFavoritos()

    override suspend fun limpiarCache() = dao.borrarTodo()
}