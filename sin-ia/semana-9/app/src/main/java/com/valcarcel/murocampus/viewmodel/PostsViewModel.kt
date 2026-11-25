package com.valcarcel.murocampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.valcarcel.murocampus.data.PostRepository
import com.valcarcel.murocampus.mensajeDe
import com.valcarcel.murocampus.model.Comentario
import com.valcarcel.murocampus.model.Post
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface UiState<out T> {
    data object Cargando : UiState<Nothing>
    data class Exito<T>(val datos: T) : UiState<T>
    data class Error(val mensaje: String) : UiState<Nothing>
}

data class DetalleUi(
    val post: Post,
    val comentarios: List<Comentario>
)

class PostsViewModel(private val repo: PostRepository) : ViewModel() {
    private val _lista = MutableStateFlow<UiState<List<Post>>>(UiState.Cargando)
    val lista = _lista.asStateFlow()

    init { cargar() }

    fun cargar() {
        viewModelScope.launch {
            _lista.value = UiState.Cargando
            repo.listar()
                .onSuccess { _lista.value = UiState.Exito(it) }
                .onFailure { _lista.value = UiState.Error(mensajeDe(it)) }
        }
    }
    private val _detalle = MutableStateFlow<UiState<DetalleUi>>(UiState.Cargando)
    val detalle = _detalle.asStateFlow()

    private var trabajoDetalle: Job? = null

    fun abrir(id: Int) {
        trabajoDetalle?.cancel()
        trabajoDetalle = viewModelScope.launch {
            _detalle.value = UiState.Cargando

            val (resultadoPost, resultadoComentarios) = coroutineScope {
                val post = async { repo.detalle(id) }
                val comentarios = async { repo.comentarios(id) }
                post.await() to comentarios.await()
            }

            val post = resultadoPost.getOrElse {
                _detalle.value = UiState.Error(mensajeDe(it))
                return@launch
            }
            val comentarios = resultadoComentarios.getOrElse {
                _detalle.value = UiState.Error(mensajeDe(it))
                return@launch
            }

            _detalle.value = UiState.Exito(DetalleUi(post, comentarios))
        }
    }
}