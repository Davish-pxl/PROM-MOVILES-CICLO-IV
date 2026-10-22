package com.tecsup.huellitasvvet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.huellitasvvet.data.local.entity.Cita
import com.tecsup.huellitasvvet.data.local.relation.CitaConDetalle
import com.tecsup.huellitasvvet.data.repository.VetRepository
import com.tecsup.huellitasvvet.model.EstadoCita
import com.tecsup.huellitasvvet.model.Horarios
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@OptIn(ExperimentalCoroutinesApi::class)
class ReprogramarCitaViewModel(
    private val repo: VetRepository,
    citaId: Int
) : ViewModel() {

    val cita: StateFlow<CitaConDetalle?> =
        repo.observarCita(citaId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val fechas: List<LocalDate> = (0L..7L).map { LocalDate.now().plusDays(it) }

    private val _fecha = MutableStateFlow(LocalDate.now())
    val fecha: StateFlow<LocalDate> = _fecha.asStateFlow()

    private val _hora = MutableStateFlow<String?>(null)
    val hora: StateFlow<String?> = _hora.asStateFlow()

    private val horasOcupadas: StateFlow<List<String>> =
        _fecha.flatMapLatest { repo.observarHorasOcupadas(it.toString()) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val horasNoDisponibles: StateFlow<Set<String>> =
        combine(_fecha, horasOcupadas) { f, ocupadas ->
            val pasadas = if (f == LocalDate.now()) {
                Horarios.todos.filter { !LocalTime.parse(it).isAfter(LocalTime.now()) }
            } else emptyList()
            (ocupadas + pasadas).toSet()
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    val puedeConfirmar: StateFlow<Boolean> =
        combine(_fecha, _hora, cita, horasNoDisponibles) { f, h, c, noDisp ->
            if (c == null || h == null) return@combine false
            val esDistinta = !(f.toString() == c.cita.fecha && h == c.cita.hora)
            val esValida = puedeReprogramar(c.cita)
            esDistinta && esValida && h !in noDisp
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    private val _reprogramada = MutableStateFlow(false)
    val reprogramada: StateFlow<Boolean> = _reprogramada.asStateFlow()

    fun elegirFecha(f: LocalDate) {
        if (f != _fecha.value) {
            _fecha.value = f
            _hora.value = null
        }
        _mensaje.value = null
    }

    fun elegirHora(h: String) {
        if (h !in horasNoDisponibles.value) {
            _hora.value = h
            _mensaje.value = null
        }
    }

    fun confirmar() {
        val c = cita.value ?: return
        val f = _fecha.value
        val h = _hora.value
        val hoy = LocalDate.now()

        when {
            !puedeReprogramar(c.cita) -> {
                _mensaje.value = "Solo se puede reprogramar con más de 2 horas de anticipación"
            }
            h == null -> {
                _mensaje.value = "Elige un horario"
            }
            f.isBefore(hoy) || f.isAfter(hoy.plusDays(7)) -> {
                _mensaje.value = "La fecha debe estar entre hoy y 7 días"
            }
            h !in Horarios.todos -> {
                _mensaje.value = "Horario no válido"
            }
            f == hoy && !LocalTime.parse(h).isAfter(LocalTime.now()) -> {
                _mensaje.value = "Ese horario ya pasó"
                _hora.value = null
            }
            f.toString() == c.cita.fecha && h == c.cita.hora -> {
                _mensaje.value = "La nueva fecha y hora deben ser distintas de las actuales"
            }
            else -> viewModelScope.launch {
                val ocupadasAhora = repo.observarHorasOcupadas(f.toString()).first()
                if (h in ocupadasAhora) {
                    _mensaje.value = "Ese horario acaba de ocuparse. Elige otro."
                    _hora.value = null
                } else {
                    repo.reprogramarCita(c.cita.id, f.toString(), h)
                    _reprogramada.value = true
                }
            }
        }
    }

    fun limpiarMensaje() {
        _mensaje.value = null
    }

    private fun puedeReprogramar(c: Cita): Boolean {
        val activa = c.estado == EstadoCita.PENDIENTE.name ||
                c.estado == EstadoCita.CONFIRMADA.name
        val fechaHoraCita = LocalDateTime.of(LocalDate.parse(c.fecha), LocalTime.parse(c.hora))
        val minutosQueFaltan = Duration.between(LocalDateTime.now(), fechaHoraCita).toMinutes()
        return activa && minutosQueFaltan > 120
    }
}
