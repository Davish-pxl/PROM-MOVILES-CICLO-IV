package com.tuapp.valcarcelcomida.ui.detalle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tuapp.valcarcelcomida.AppContainer
import com.tuapp.valcarcelcomida.data.repository.ItemRepository
import com.tuapp.valcarcelcomida.domain.model.Item
import com.tuapp.valcarcelcomida.ui.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class DetalleViewModel(
    private val repo: ItemRepository,
    private val id: String
) : ViewModel() {

    private val _estado = MutableStateFlow<UiState<Item>>(UiState.Cargando)
    val estado: StateFlow<UiState<Item>> = _estado.asStateFlow()

    init {
        cargar()
    }

    fun cargar() {
        _estado.value = UiState.Cargando
        viewModelScope.launch {
            repo.obtenerDetalle(id)
                .onSuccess { item -> _estado.value = UiState.Exito(item) }
                .onFailure { e -> _estado.value = UiState.Error(mensajeDe(e)) }
        }
    }

    fun alternarFavorito() {
        viewModelScope.launch {
            repo.alternarFavorito(id)
            val actual = _estado.value
            if (actual is UiState.Exito) {
                _estado.value = UiState.Exito(actual.datos.copy(esFavorito = !actual.datos.esFavorito))
            }
        }
    }

    private fun mensajeDe(e: Throwable): String = when (e) {
        is IOException -> "No hay conexión a internet"
        is HttpException -> "El servidor respondió con error ${e.code()}"
        is NoSuchElementException -> "No se encontró la comida"
        else -> "Ocurrió un error inesperado"
    }

    class Factory(
        private val container: AppContainer,
        private val id: String
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            DetalleViewModel(container.itemRepository, id) as T
    }
}