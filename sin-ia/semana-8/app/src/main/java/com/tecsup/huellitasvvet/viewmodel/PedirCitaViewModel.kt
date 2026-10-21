package com.tecsup.huellitasvvet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.huellitasvvet.data.local.entity.Mascota
import com.tecsup.huellitasvvet.data.local.entity.Servicio
import com.tecsup.huellitasvvet.data.prefs.PreferenciasStore
import com.tecsup.huellitasvvet.data.repository.VetRepository
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
import java.time.LocalDate
import java.time.LocalTime

@OptIn(ExperimentalCoroutinesApi::class)
class PedirCitaViewModel(
    private val repo: VetRepository,
    prefs: PreferenciasStore,
    duenoId: Int,
    mascotaInicial: Int? = null
) : ViewModel() {

    val mascotas: StateFlow<List<Mascota>> =
        repo.observarMascotasDeDueno(duenoId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val servicios: StateFlow<List<Servicio>> =
        repo.observarServiciosActivos()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val aceptaCitas: StateFlow<Boolean> =
        prefs.aceptarCitas.stateIn(viewModelScope, SharingStarted.Eagerly, true)

    val fechas: List<LocalDate> = (0L..7L).map { LocalDate.now().plusDays(it) }

    private val _mascotaId = MutableStateFlow(mascotaInicial)
    val mascotaId: StateFlow<Int?> = _mascotaId.asStateFlow()

    private val _servicioId = MutableStateFlow<Int?>(null)
    val servicioId: StateFlow<Int?> = _servicioId.asStateFlow()

    private val _fecha = MutableStateFlow(LocalDate.now())
    val fecha: StateFlow<LocalDate> = _fecha.asStateFlow()

    private val _hora = MutableStateFlow<String?>(null)
    val hora: StateFlow<String?> = _hora.asStateFlow()

    val horasOcupadas: StateFlow<List<String>> =
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
        combine(_mascotaId, _servicioId, _hora, aceptaCitas) { m, s, h, acepta ->
            acepta && m != null && s != null && h != null
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    private val _citaCreada = MutableStateFlow(false)
    val citaCreada: StateFlow<Boolean> = _citaCreada.asStateFlow()

    fun elegirMascota(id: Int) { _mascotaId.value = id; _mensaje.value = null }

    fun elegirServicio(id: Int) { _servicioId.value = id; _mensaje.value = null }

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
        val m = _mascotaId.value
        val s = _servicioId.value
        val h = _hora.value
        val f = _fecha.value
        val hoy = LocalDate.now()

        when {
            !aceptaCitas.value ->
                _mensaje.value = "La clínica no está recibiendo citas"
            m == null || s == null || h == null ->
                _mensaje.value = "Elige mascota, servicio, fecha y hora"
            mascotas.value.none { it.id == m } ->
                _mensaje.value = "Esa mascota no te pertenece"
            servicios.value.none { it.id == s } ->
                _mensaje.value = "El servicio ya no está disponible"
            f.isBefore(hoy) || f.isAfter(hoy.plusDays(7)) ->
                _mensaje.value = "La fecha debe estar entre hoy y 7 días"
            h !in Horarios.todos ->
                _mensaje.value = "Horario no válido"
            f == hoy && !LocalTime.parse(h).isAfter(LocalTime.now()) -> {
                _mensaje.value = "Ese horario ya pasó"
                _hora.value = null
            }
            else -> viewModelScope.launch {
                val ocupadasAhora = repo.observarHorasOcupadas(f.toString()).first()
                if (h in ocupadasAhora) {
                    _mensaje.value = "Ese horario acaba de ocuparse. Elige otro."
                    _hora.value = null
                } else {
                    repo.crearCita(m, s, f.toString(), h, "")
                    _citaCreada.value = true
                }
            }
        }
    }
}