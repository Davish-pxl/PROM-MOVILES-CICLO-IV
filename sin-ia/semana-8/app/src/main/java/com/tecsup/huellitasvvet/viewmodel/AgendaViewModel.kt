package com.tecsup.huellitasvvet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.huellitasvvet.data.local.entity.Cita
import com.tecsup.huellitasvvet.data.local.relation.CitaConDetalle
import com.tecsup.huellitasvvet.data.repository.VetRepository
import com.tecsup.huellitasvvet.model.EstadoCita
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class AgendaViewModel(private val repo: VetRepository) : ViewModel() {

    val fechas: List<LocalDate> = (0L..6L).map { LocalDate.now().plusDays(it) }

    private val _fechaSeleccionada = MutableStateFlow(LocalDate.now())
    val fechaSeleccionada: StateFlow<LocalDate> = _fechaSeleccionada.asStateFlow()

    val citas: StateFlow<List<CitaConDetalle>> =
        _fechaSeleccionada
            .flatMapLatest { repo.observarCitasPorFecha(it.toString()) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    fun elegirFecha(f: LocalDate) {
        _fechaSeleccionada.value = f
    }

    fun confirmar(c: Cita) {
        if (c.estado != EstadoCita.PENDIENTE.name) {
            _mensaje.value = "Solo se puede confirmar una cita pendiente"
            return
        }
        viewModelScope.launch {
            repo.cambiarEstadoCita(c.id, EstadoCita.CONFIRMADA)
            _mensaje.value = "Cita confirmada"
        }
    }

    fun cancelar(c: Cita) {
        val cancelable = c.estado == EstadoCita.PENDIENTE.name ||
                c.estado == EstadoCita.CONFIRMADA.name
        if (!cancelable) {
            _mensaje.value = "Esta cita ya no se puede cancelar"
            return
        }
        viewModelScope.launch {
            repo.cambiarEstadoCita(c.id, EstadoCita.CANCELADA)
            _mensaje.value = "Cita cancelada"
        }
    }

    fun limpiarMensaje() {
        _mensaje.value = null
    }
}