package com.tecsup.huellitasvvet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.huellitasvvet.data.local.entity.Cita
import com.tecsup.huellitasvvet.data.local.relation.CitaConDetalle
import com.tecsup.huellitasvvet.data.repository.VetRepository
import com.tecsup.huellitasvvet.model.EstadoCita
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class MisCitasViewModel(
    private val repo: VetRepository,
    duenoId: Int
) : ViewModel() {

    private val citas = repo.observarCitasDeDueno(duenoId)

    val proximas: StateFlow<List<CitaConDetalle>> =
        citas.map { lista ->
            lista.filter { esProxima(it.cita) }.sortedBy { fechaHora(it.cita) }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val historial: StateFlow<List<CitaConDetalle>> =
        citas.map { lista ->
            lista.filterNot { esProxima(it.cita) }.sortedByDescending { fechaHora(it.cita) }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val atenciones: StateFlow<Int> =
        citas.map { lista -> lista.count { it.cita.estado == EstadoCita.ATENDIDA.name } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()
    fun puedeCancelar(c: Cita): Boolean {
        val activa = c.estado == EstadoCita.PENDIENTE.name ||
                c.estado == EstadoCita.CONFIRMADA.name
        val minutosQueFaltan = Duration.between(LocalDateTime.now(), fechaHora(c)).toMinutes()
        return activa && minutosQueFaltan > 120
    }

    fun cancelar(c: Cita) {
        if (!puedeCancelar(c)) {
            _mensaje.value = "Ya no puedes cancelar esta cita"
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

    private fun esProxima(c: Cita): Boolean =
        (c.estado == EstadoCita.PENDIENTE.name || c.estado == EstadoCita.CONFIRMADA.name) &&
                !fechaHora(c).isBefore(LocalDateTime.now())

    private fun fechaHora(c: Cita): LocalDateTime =
        LocalDateTime.of(LocalDate.parse(c.fecha), LocalTime.parse(c.hora))
}