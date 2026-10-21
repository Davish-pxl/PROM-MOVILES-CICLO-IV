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
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ErroresAtencion(
    val diagnostico: String? = null,
    val peso: String? = null
) {
    val hayErrores: Boolean get() = diagnostico != null || peso != null
}

class AtenderCitaViewModel(
    private val repo: VetRepository,
    citaId: Int
) : ViewModel() {

    val cita: StateFlow<CitaConDetalle?> =
        repo.observarCita(citaId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _errores = MutableStateFlow(ErroresAtencion())
    val errores: StateFlow<ErroresAtencion> = _errores.asStateFlow()

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()
    private val _terminado = MutableStateFlow(false)
    val terminado: StateFlow<Boolean> = _terminado.asStateFlow()

    fun registrar(diagnostico: String, tratamiento: String, peso: Double) {
        val c = cita.value?.cita ?: return

        if (!esAtendible(c)) {
            _mensaje.value = "Solo se puede atender una cita pendiente o confirmada"
            return
        }
        val e = ErroresAtencion(
            diagnostico = if (diagnostico.trim().length < 5) "Mínimo 5 caracteres" else null,
            peso = if (peso <= 0.0) "Debe ser mayor que 0" else null
        )
        _errores.value = e
        if (e.hayErrores) return

        viewModelScope.launch {
            repo.registrarAtencion(c.id, diagnostico.trim(), tratamiento.trim(), peso)
            _terminado.value = true
        }
    }

    fun cancelar() {
        val c = cita.value?.cita ?: return
        if (!esAtendible(c)) {
            _mensaje.value = "Esta cita ya no se puede cancelar"
            return
        }
        viewModelScope.launch {
            repo.cambiarEstadoCita(c.id, EstadoCita.CANCELADA)
            _terminado.value = true
        }
    }

    private fun esAtendible(c: Cita): Boolean =
        c.estado == EstadoCita.PENDIENTE.name || c.estado == EstadoCita.CONFIRMADA.name
}