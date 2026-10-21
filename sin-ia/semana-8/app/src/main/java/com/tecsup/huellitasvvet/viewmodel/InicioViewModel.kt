package com.tecsup.huellitasvvet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.huellitasvvet.data.local.entity.Mascota
import com.tecsup.huellitasvvet.data.local.entity.Servicio
import com.tecsup.huellitasvvet.data.local.entity.Usuario
import com.tecsup.huellitasvvet.data.local.relation.CitaConDetalle
import com.tecsup.huellitasvvet.data.repository.VetRepository
import com.tecsup.huellitasvvet.model.EstadoCita
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit

data class Recordatorio(val mascota: String, val dias: Long)

class InicioViewModel(
    repo: VetRepository,
    dueno: Usuario
) : ViewModel() {

    val nombreDueno: String = dueno.nombre

    val mascotas: StateFlow<List<Mascota>> =
        repo.observarMascotasDeDueno(dueno.id)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val servicios: StateFlow<List<Servicio>> =
        repo.observarServiciosActivos()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val proximaCita: StateFlow<CitaConDetalle?> =
        repo.observarCitasDeDueno(dueno.id)
            .map { lista ->
                val ahora = LocalDateTime.now()
                lista
                    .filter {
                        it.cita.estado == EstadoCita.PENDIENTE.name ||
                                it.cita.estado == EstadoCita.CONFIRMADA.name
                    }
                    .filter { fechaHora(it) >= ahora }
                    .sortedBy { fechaHora(it) }
                    .firstOrNull()
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    val recordatorio: StateFlow<Recordatorio?> =
        repo.observarCitasDeDueno(dueno.id)
            .map { lista ->
                val hoy = LocalDate.now()
                lista
                    .filter {
                        it.cita.estado == EstadoCita.ATENDIDA.name &&
                                it.servicio.nombre.startsWith("Vacun", ignoreCase = true)
                    }
                    .groupBy { it.cita.mascotaId }
                    .mapNotNull { (_, vacunas) ->
                        val ultima = vacunas.maxBy { it.cita.fecha }
                        val refuerzo = LocalDate.parse(ultima.cita.fecha).plusYears(1)
                        val dias = ChronoUnit.DAYS.between(hoy, refuerzo)
                        if (dias in 0..30) Recordatorio(ultima.mascota.mascota.nombre, dias) else null
                    }
                    .minByOrNull { it.dias }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private fun fechaHora(c: CitaConDetalle): LocalDateTime =
        LocalDateTime.of(LocalDate.parse(c.cita.fecha), LocalTime.parse(c.cita.hora))
}