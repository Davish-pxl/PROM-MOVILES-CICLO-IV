package com.tuapp.valcarcelcomida.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tuapp.valcarcelcomida.AppContainer
import com.tuapp.valcarcelcomida.data.auth.AuthRepository
import com.tuapp.valcarcelcomida.data.repository.ItemRepository
import com.tuapp.valcarcelcomida.domain.model.Rol
import com.tuapp.valcarcelcomida.ui.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ResumenAdmin(
    val totalGuardado: Int,
    val totalFavoritos: Int
)

class AdminViewModel(
    private val repo: ItemRepository,
    private val auth: AuthRepository
) : ViewModel() {

    private val _estado = MutableStateFlow<UiState<ResumenAdmin>>(UiState.Cargando)
    val estado: StateFlow<UiState<ResumenAdmin>> = _estado.asStateFlow()

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    private val _ocupado = MutableStateFlow(false)
    val ocupado: StateFlow<Boolean> = _ocupado.asStateFlow()

    init {
        viewModelScope.launch {
            if (auth.sesionActual()?.rol != Rol.ADMIN) {
                _estado.value = UiState.Error("Solo el administrador puede ver este panel")
            } else {
                cargarTotales()
            }
        }
    }

    fun sincronizar() {
        viewModelScope.launch {
            _ocupado.value = true
            repo.refrescar()
                .onSuccess { n -> _mensaje.value = "Se guardaron $n elementos" }
                .onFailure { _mensaje.value = "No se pudo sincronizar: revisa tu conexión" }
            cargarTotales()
            _ocupado.value = false
        }
    }

    fun limpiarCache() {
        viewModelScope.launch {
            _ocupado.value = true
            repo.limpiarCache()
            _mensaje.value = "Caché eliminada"
            cargarTotales()
            _ocupado.value = false
        }
    }

    private suspend fun cargarTotales() {
        _estado.value = UiState.Exito(
            ResumenAdmin(
                totalGuardado = repo.cantidadGuardada(),
                totalFavoritos = repo.cantidadFavoritos()
            )
        )
    }

    class Factory(private val container: AppContainer) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            AdminViewModel(container.itemRepository, container.authRepository) as T
    }
}