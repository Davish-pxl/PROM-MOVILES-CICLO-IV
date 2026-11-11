package com.tuapp.valcarcelcomida.ui.lista

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tuapp.valcarcelcomida.AppContainer
import com.tuapp.valcarcelcomida.data.auth.AuthRepository
import com.tuapp.valcarcelcomida.data.repository.ItemRepository
import com.tuapp.valcarcelcomida.domain.model.Categoria
import com.tuapp.valcarcelcomida.domain.model.Item
import com.tuapp.valcarcelcomida.domain.model.Rol
import com.tuapp.valcarcelcomida.notification.Notificador
import com.tuapp.valcarcelcomida.ui.UiState
import com.tuapp.valcarcelcomida.ui.errorDe
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.Normalizer

class ListaViewModel(
    private val repo: ItemRepository,
    private val auth: AuthRepository,
    private val notificador: Notificador
) : ViewModel() {

    private val _estado = MutableStateFlow<UiState<List<Item>>>(UiState.Cargando)

    val estado: StateFlow<UiState<List<Item>>> = _estado.asStateFlow()

    private val _aviso = MutableStateFlow<String?>(null)
    val aviso: StateFlow<String?> = _aviso.asStateFlow()

    private val _esAdmin = MutableStateFlow(false)
    val esAdmin: StateFlow<Boolean> = _esAdmin.asStateFlow()

    val categorias: List<Categoria> = repo.categoriasDisponibles
    val categoriaSeleccionada: StateFlow<String> = repo.categoria

    private val _busqueda = MutableStateFlow("")
    val busqueda: StateFlow<String> = _busqueda.asStateFlow()

    private var refrescoTerminado = false

    private var trabajoRefresco: Job? = null

    init {
        viewModelScope.launch {
            _esAdmin.value = auth.sesionActual()?.rol == Rol.ADMIN
        }
        viewModelScope.launch {
            combine(repo.items, _busqueda) { lista, texto -> lista to texto }
                .collect { (lista, texto) ->
                    if (lista.isNotEmpty() || refrescoTerminado) {
                        _estado.value = UiState.Exito(filtrar(lista, texto))
                    }
                }
        }
        refrescar()
    }

    fun refrescar() {
        if (_estado.value is UiState.Error) _estado.value = UiState.Cargando

        trabajoRefresco?.cancel()
        trabajoRefresco = viewModelScope.launch {
            val resultado = repo.refrescar()
            refrescoTerminado = true
            resultado
                .onSuccess { n ->
                    if (_estado.value is UiState.Cargando) _estado.value = UiState.Exito(emptyList())
                    _aviso.value = null
                    notificador.mostrar("Datos actualizados", "Se guardaron $n elementos")
                }
                .onFailure { e ->
                    if (repo.items.first().isEmpty()) _estado.value = errorDe(e)
                    else _aviso.value = "Sin conexión: mostrando datos guardados"
                }
        }
    }

    fun seleccionarCategoria(clave: String) {
        if (clave == repo.categoria.value) return
        refrescoTerminado = false
        _aviso.value = null
        _busqueda.value = ""
        _estado.value = UiState.Cargando
        repo.seleccionarCategoria(clave)
        refrescar()
    }

    fun buscar(texto: String) {
        _busqueda.value = texto
    }

    fun alternarFavorito(id: String) {
        viewModelScope.launch { repo.alternarFavorito(id) }
    }

    fun cerrarSesion() = auth.cerrarSesion()

    private fun filtrar(lista: List<Item>, texto: String): List<Item> {
        if (texto.isBlank()) return lista
        val buscado = normalizar(texto.trim())
        return lista.filter { normalizar(it.titulo).contains(buscado) }
    }

    private fun normalizar(texto: String): String =
        Normalizer.normalize(texto, Normalizer.Form.NFD)
            .replace(Regex("\\p{Mn}+"), "")
            .lowercase()

    class Factory(private val container: AppContainer) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            ListaViewModel(container.itemRepository, container.authRepository, container.notificador) as T
    }
}