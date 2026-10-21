package com.tecsup.huellitasvvet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.huellitasvvet.data.local.relation.MascotaConDueno
import com.tecsup.huellitasvvet.data.repository.VetRepository
import com.tecsup.huellitasvvet.model.Especie
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class)
class PacientesViewModel(private val repo: VetRepository) : ViewModel() {

    private val todas = repo.observarTodasLasMascotas()

    private val _texto = MutableStateFlow("")
    val texto: StateFlow<String> = _texto.asStateFlow()

    private val _especie = MutableStateFlow<Especie?>(null)
    val especie: StateFlow<Especie?> = _especie.asStateFlow()

    val pacientes: StateFlow<List<MascotaConDueno>> =
        combine(todas, _texto, _especie) { lista, texto, especie ->
            val t = texto.trim()
            lista.filter { p ->
                val coincideEspecie = especie == null || p.mascota.especie == especie.name
                val coincideTexto = t.isEmpty() ||
                        p.mascota.nombre.contains(t, ignoreCase = true) ||
                        p.dueno.nombre.contains(t, ignoreCase = true)
                coincideEspecie && coincideTexto
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val ultimaVisita: StateFlow<Map<Int, String>> =
        todas.flatMapLatest { lista ->
            if (lista.isEmpty()) {
                flowOf(emptyMap())
            } else {
                combine(
                    lista.map { p ->
                        repo.observarHistorial(p.mascota.id)
                            .map { h -> p.mascota.id to h.firstOrNull()?.cita?.fecha }
                    }
                ) { pares ->
                    pares.mapNotNull { (id, fecha) -> fecha?.let { id to it } }.toMap()
                }
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    fun buscar(texto: String) {
        _texto.value = texto
    }

    fun filtrarEspecie(especie: Especie?) {
        _especie.value = especie
    }
}