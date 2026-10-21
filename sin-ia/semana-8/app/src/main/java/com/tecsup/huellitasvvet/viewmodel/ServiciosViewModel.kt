package com.tecsup.huellitasvvet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.huellitasvvet.data.local.entity.Servicio
import com.tecsup.huellitasvvet.data.repository.VetRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ErroresServicio(
    val nombre: String? = null,
    val duracion: String? = null,
    val precio: String? = null
) {
    val hayErrores: Boolean get() = nombre != null || duracion != null || precio != null
}

class ServiciosViewModel(private val repo: VetRepository) : ViewModel() {

    val servicios: StateFlow<List<Servicio>> = repo.observarServicios()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _errores = MutableStateFlow(ErroresServicio())
    val errores: StateFlow<ErroresServicio> = _errores.asStateFlow()

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    private val _guardado = MutableStateFlow(false)
    val guardado: StateFlow<Boolean> = _guardado.asStateFlow()

    private val _eliminado = MutableStateFlow(false)
    val eliminado: StateFlow<Boolean> = _eliminado.asStateFlow()

    private val porId = mutableMapOf<Int, StateFlow<Servicio?>>()

    fun servicio(id: Int): StateFlow<Servicio?> = porId.getOrPut(id) {
        servicios.map { lista -> lista.find { it.id == id } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    }

    fun alternar(s: Servicio) {
        viewModelScope.launch { repo.cambiarActivo(s.id, !s.activo) }
    }

    fun guardar(servicio: Servicio) {
        val e = ErroresServicio(
            nombre = if (servicio.nombre.isBlank()) "El nombre es obligatorio" else null,
            duracion = if (servicio.duracionMin !in listOf(15, 30, 45, 60))
                "Elige 15, 30, 45 o 60 min" else null,
            precio = if (servicio.precio <= 0.0) "Debe ser mayor que 0" else null
        )
        _errores.value = e
        if (e.hayErrores) return

        viewModelScope.launch {
            repo.guardarServicio(
                servicio.copy(
                    nombre = servicio.nombre.trim(),
                    descripcion = servicio.descripcion.trim()
                )
            )
            _guardado.value = true
        }
    }

    fun eliminar(servicio: Servicio) {
        viewModelScope.launch {
            if (repo.servicioTieneCitas(servicio.id)) {
                _mensaje.value = "No se puede eliminar: ${servicio.nombre} tiene citas. " +
                        "Solo puedes desactivarlo."
            } else {
                repo.eliminarServicio(servicio)
                _eliminado.value = true
            }
        }
    }
}