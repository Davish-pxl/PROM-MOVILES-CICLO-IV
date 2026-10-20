package com.tecsup.huellitasvvet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.huellitasvvet.data.repository.VetRepository
import com.tecsup.huellitasvvet.model.EstadoCita
import com.tecsup.huellitasvvet.ui.components.graficos.DatoGrafico
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate

class PanelViewModel(repo: VetRepository) : ViewModel() {
    val hoy: LocalDate = LocalDate.now()

    val citasHoy: StateFlow<Int> =
        repo.observarCitasDeFecha(hoy.toString())
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val pendientes: StateFlow<Int> =
        repo.observarCantidadPorEstado(EstadoCita.PENDIENTE)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val atendidas: StateFlow<Int> =
        repo.observarCantidadPorEstado(EstadoCita.ATENDIDA)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val ingresosMes: StateFlow<Double> =
        repo.observarIngresosDesde(hoy.withDayOfMonth(1).toString())
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val porEstado: StateFlow<List<DatoGrafico>> =
        combine(
            repo.observarCantidadPorEstado(EstadoCita.PENDIENTE),
            repo.observarCantidadPorEstado(EstadoCita.CONFIRMADA),
            repo.observarCantidadPorEstado(EstadoCita.ATENDIDA),
            repo.observarCantidadPorEstado(EstadoCita.CANCELADA)
        ) { p, c, a, x ->
            listOf(
                DatoGrafico("Pendiente", p.toFloat()),
                DatoGrafico("Confirmada", c.toFloat()),
                DatoGrafico("Atendida", a.toFloat()),
                DatoGrafico("Cancelada", x.toFloat())
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val porServicio: StateFlow<List<DatoGrafico>> =
        repo.observarCitasPorServicio()
            .map { lista ->
                lista.map { DatoGrafico(it.nombre.substringBefore(" "), it.cantidad.toFloat()) }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}