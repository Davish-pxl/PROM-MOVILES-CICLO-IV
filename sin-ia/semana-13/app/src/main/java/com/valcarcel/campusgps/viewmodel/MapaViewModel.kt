package com.valcarcel.campusgps.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.valcarcel.campusgps.data.LugaresRepository
import com.valcarcel.campusgps.data.UbicacionNoDisponibleException
import com.valcarcel.campusgps.data.UbicacionRepository
import com.valcarcel.campusgps.model.Lugar
import com.valcarcel.campusgps.model.LugarConDistancia
import com.valcarcel.campusgps.model.Posicion
import com.valcarcel.campusgps.model.TipoError
import com.valcarcel.campusgps.model.UbicacionUi
import com.valcarcel.campusgps.util.distanciaMetros
import com.valcarcel.campusgps.util.formatearDistancia
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MapaViewModel(
    private val ubicacionRepo: UbicacionRepository,
    private val lugaresRepo: LugaresRepository
) : ViewModel() {

    private val _estado = MutableStateFlow<UbicacionUi>(UbicacionUi.Inicial)
    val estado: StateFlow<UbicacionUi> = _estado.asStateFlow()

    private val _lugares = MutableStateFlow(
        lugaresRepo.obtenerLugares().map { LugarConDistancia(it, null) }
    )
    val lugares: StateFlow<List<LugarConDistancia>> = _lugares.asStateFlow()

    private val _categoria = MutableStateFlow<String?>(null)
    val categoria: StateFlow<String?> = _categoria.asStateFlow()
    val lugaresVisibles: StateFlow<List<LugarConDistancia>> =
        combine(lugares, _categoria) { lista, cat ->
            if (cat == null) lista
            else lista.filter { it.lugar.categoria == cat }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), _lugares.value)

    private val _seleccionado = MutableStateFlow<Int?>(null)
    val seleccionado: StateFlow<Int?> = _seleccionado.asStateFlow()

    val masCercano: StateFlow<Lugar?> = lugaresVisibles
        .map { lista -> lista.firstOrNull { it.metros != null }?.lugar }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    private val _radio = MutableStateFlow(200)
    val radio: StateFlow<Int> = _radio.asStateFlow()

    val idsDentroDelRadio: StateFlow<Set<Int>> =
        combine(lugares, radio) { lista, rad ->
            lista.filter { it.metros != null && it.metros <= rad }
                .map { it.lugar.id }
                .toSet()
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    fun filtrarCategoria(cat: String?) {
        _categoria.value = cat
        val actual = _seleccionado.value ?: return
        val sigueVisible = _lugares.value.any {
            it.lugar.id == actual && (cat == null || it.lugar.categoria == cat)
        }
        if (!sigueVisible) {
            _seleccionado.value = null
        }
    }
    fun cambiarRadio(metros: Int) {
        _radio.value = metros
    }

    fun buscar() {
        if (!ubicacionRepo.playServicesDisponible()) {
            _estado.value = UbicacionUi.Error(
                "Este dispositivo no tiene Google Play Services.",
                TipoError.SIN_PLAY_SERVICES
            )
            return
        }

        _estado.value = UbicacionUi.Buscando
        viewModelScope.launch {
            ubicacionRepo.obtenerPosicion()
                .onSuccess {
                    _estado.value = UbicacionUi.Lista(it)
                    actualizarDistancias(it)
                }
                .onFailure { _estado.value = errorDe(it) }
        }
    }

    fun permisoDenegado() {
        _estado.value = UbicacionUi.Error(
            "Sin permiso de ubicación no puedo mostrar dónde estás",
            TipoError.SIN_PERMISO
        )
    }
    fun seleccionar(id: Int?) {
        _seleccionado.value = id
    }
    private fun actualizarDistancias(pos: Posicion) {
        val ordenados = lugaresRepo.obtenerLugares()
            .map { lugar -> LugarConDistancia(lugar, distanciaMetros(pos, lugar)) }
            .sortedBy { it.metros }
        _lugares.value = ordenados

        ordenados.forEach {
            Log.d("CampusGPS", "${it.lugar.nombre}: ${formatearDistancia(it.metros ?: 0f)}")
        }
    }
    private fun errorDe(e: Throwable): UbicacionUi.Error = when (e) {
        is SecurityException -> UbicacionUi.Error(
            "Falta el permiso de ubicación",
            TipoError.SIN_PERMISO
        )
        is UbicacionNoDisponibleException -> UbicacionUi.Error(
            "No se pudo obtener la ubicación. Activa el GPS e intenta de nuevo",
            TipoError.GPS_APAGADO
        )
        else -> UbicacionUi.Error(
            e.message ?: "No se pudo obtener la ubicación",
            TipoError.OTRO
        )
    }
}