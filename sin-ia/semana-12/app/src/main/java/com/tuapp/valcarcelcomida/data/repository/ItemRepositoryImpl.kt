package com.tuapp.valcarcelcomida.data.repository

import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import com.tuapp.valcarcelcomida.data.local.ItemDao
import com.tuapp.valcarcelcomida.data.mapper.aEntity
import com.tuapp.valcarcelcomida.data.mapper.aModelo
import com.tuapp.valcarcelcomida.data.mapper.categoriaEnEspanol
import com.tuapp.valcarcelcomida.data.remote.ApiService
import com.tuapp.valcarcelcomida.domain.model.Categoria
import com.tuapp.valcarcelcomida.domain.model.Item
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException

class ItemRepositoryImpl(
    private val api: ApiService,
    private val dao: ItemDao,
    private val traductor: Traductor
) : ItemRepository {

    override val categoriasDisponibles: List<Categoria> =
        ApiService.CATEGORIAS.map { Categoria(clave = it, nombre = categoriaEnEspanol(it)) }

    private val _categoria = MutableStateFlow(ApiService.CATEGORIA)
    override val categoria: StateFlow<String> = _categoria.asStateFlow()

    override fun seleccionarCategoria(clave: String) {
        _categoria.value = clave
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val items: Flow<List<Item>> =
        _categoria.flatMapLatest { clave -> dao.porCategoria(clave) }
            .map { lista -> lista.map { it.aModelo() } }

    override val favoritos: Flow<List<Item>> =
        dao.favoritos().map { lista -> lista.map { it.aModelo() } }

    override suspend fun refrescar(): Result<Int> = try {
        val idsFavoritos = dao.idsFavoritos().toSet()
        val clave = _categoria.value
        val nuevos = api.listar(clave).meals.orEmpty()
            .map { dto ->
                dto.aEntity(
                    esFavorito = dto.idMeal in idsFavoritos,
                    titulo = traductor.traducir(dto.strMeal),
                    categoria = clave
                )
            }
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
            val item = dto.aModelo(esFavorito)
            Result.success(
                item.copy(
                    titulo = traductor.traducir(item.titulo),
                    ingredientes = item.ingredientes.map { traductor.traducir(it) },
                    instrucciones = item.instrucciones?.let { traducirPorLineas(it) }
                )
            )
        } catch (e: IOException) {
            Result.failure(e)
        } catch (e: HttpException) {
            Result.failure(e)
        } catch (e: SerializationException) {
            Result.failure(e)
        }
    }

    private suspend fun traducirPorLineas(texto: String): String =
        texto.split("\r\n", "\n")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .map { traductor.traducir(it) }
            .joinToString("\n")

    override suspend fun alternarFavorito(id: String) = dao.alternarFavorito(id)

    override suspend fun cantidadGuardada(): Int = dao.contar()

    override suspend fun cantidadFavoritos(): Int = dao.contarFavoritos()

    override suspend fun limpiarCache() = dao.borrarTodo()
}

class TraductorMlKit : Traductor {

    private val cliente = Translation.getClient(
        TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(TranslateLanguage.SPANISH)
            .build()
    )

    private val mutex = Mutex()
    private var modeloListo = false

    private suspend fun asegurarModelo(): Boolean = mutex.withLock {
        if (!modeloListo) {
            modeloListo = try {
                cliente.downloadModelIfNeeded(DownloadConditions.Builder().build()).await()
                true
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                false
            }
        }
        modeloListo
    }

    override suspend fun traducir(texto: String): String {
        if (texto.isBlank() || !asegurarModelo()) return texto
        return try {
            cliente.translate(texto).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            texto
        }
    }
}