package com.tecsup.huellitasvvet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.huellitasvvet.data.local.entity.Mascota
import com.tecsup.huellitasvvet.data.local.relation.CitaConDetalle
import com.tecsup.huellitasvvet.data.repository.VetRepository
import com.tecsup.huellitasvvet.ui.components.graficos.DatoGrafico
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

data class ErroresMascota(
    val nombre: String? = null,
    val especie: String? = null,
    val sexo: String? = null,
    val anio: String? = null,
    val peso: String? = null
) {
    val hayErrores: Boolean
        get() = listOf(nombre, especie, sexo, anio, peso).any { it != null }
}

class MascotaViewModel(
    private val repo: VetRepository,
    private val duenoId: Int
) : ViewModel() {

    private val _errores = MutableStateFlow(ErroresMascota())
    val errores: StateFlow<ErroresMascota> = _errores.asStateFlow()

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    private val _guardado = MutableStateFlow(false)
    val guardado: StateFlow<Boolean> = _guardado.asStateFlow()

    private val _eliminado = MutableStateFlow(false)
    val eliminado: StateFlow<Boolean> = _eliminado.asStateFlow()

    private val mascotas = mutableMapOf<Int, StateFlow<Mascota?>>()
    private val historiales = mutableMapOf<Int, StateFlow<List<CitaConDetalle>>>()
    private val vacunas = mutableMapOf<Int, StateFlow<Boolean>>()
    private val pesosMap = mutableMapOf<Int, StateFlow<List<DatoGrafico>>>()

    fun mascota(id: Int): StateFlow<Mascota?> = mascotas.getOrPut(id) {
        repo.observarMascota(id)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    }

    fun historial(mascotaId: Int): StateFlow<List<CitaConDetalle>> = historiales.getOrPut(mascotaId) {
        repo.observarHistorial(mascotaId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    fun pesos(mascotaId: Int): StateFlow<List<DatoGrafico>> = pesosMap.getOrPut(mascotaId) {
        repo.observarPesos(mascotaId)
            .map { lista ->
                lista.map { p ->
                    DatoGrafico(etiqueta = p.fecha, valor = p.peso.toFloat())
                }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    fun vacunasAlDia(mascotaId: Int): StateFlow<Boolean> = vacunas.getOrPut(mascotaId) {
        historial(mascotaId)
            .map { lista ->
                lista
                    .filter { it.servicio.nombre.startsWith("Vacun", ignoreCase = true) }
                    .maxOfOrNull { LocalDate.parse(it.cita.fecha) }
                    ?.let { !it.plusYears(1).isBefore(LocalDate.now()) }
                    ?: false
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
    }

    fun guardar(mascota: Mascota) {
        val anioActual = LocalDate.now().year
        val e = ErroresMascota(
            nombre = if (mascota.nombre.trim().length < 2) "Mínimo 2 caracteres" else null,
            especie = if (mascota.especie.isBlank()) "Elige la especie" else null,
            sexo = if (mascota.sexo.isBlank()) "Elige el sexo" else null,
            anio = if (mascota.anioNacimiento !in 1990..anioActual)
                "Debe estar entre 1990 y $anioActual" else null,
            peso = if (mascota.peso <= 0.0) "Debe ser mayor que 0" else null
        )
        _errores.value = e
        if (e.hayErrores) return

        viewModelScope.launch {
            repo.guardarMascota(
                mascota.copy(
                    nombre = mascota.nombre.trim(),
                    raza = mascota.raza.trim(),
                    duenoId = if (mascota.id == 0) duenoId else mascota.duenoId
                )
            )
            _guardado.value = true
        }
    }

    fun eliminar(mascota: Mascota) {
        viewModelScope.launch {
            if (repo.mascotaTieneCitas(mascota.id)) {
                _mensaje.value = "No se puede eliminar: ${mascota.nombre} tiene citas registradas"
            } else {
                repo.eliminarMascota(mascota)
                _eliminado.value = true
            }
        }
    }
}
