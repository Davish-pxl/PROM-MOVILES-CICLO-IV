package com.valcarcel.centrotareas.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.valcarcel.centrotareas.data.TareasRepository
import com.valcarcel.centrotareas.model.CargaUi
import com.valcarcel.centrotareas.model.PrimosUi
import com.valcarcel.centrotareas.model.RegistroTiempo
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TareasViewModel(private val repo: TareasRepository) : ViewModel() {

    private val _mensaje = MutableStateFlow("Listo")
    val mensaje: StateFlow<String> = _mensaje.asStateFlow()

    private val _progreso = MutableStateFlow(0f)
    val progreso: StateFlow<Float> = _progreso.asStateFlow()

    private val _corriendo = MutableStateFlow(false)
    val corriendo: StateFlow<Boolean> = _corriendo.asStateFlow()

    private var trabajo: Job? = null

    fun tareaLarga() {
        if (_corriendo.value) return
        _corriendo.value = true

        trabajo = viewModelScope.launch {
            try {
                _progreso.value = 0f
                _mensaje.value = "Trabajando..."
                Log.d("HILO", "Antes: ${Thread.currentThread().name}")

                withContext(Dispatchers.Default) {
                    Log.d("HILO", "Dentro: ${Thread.currentThread().name}")
                    for (i in 1..100) {
                        delay(40)
                        _progreso.value = i / 100f
                    }
                }

                Log.d("HILO", "Despues: ${Thread.currentThread().name}")
                _mensaje.value = "Terminó"
            } finally {
                _corriendo.value = false
            }
        }
    }

    fun cancelar() {
        trabajo?.cancel()
        _mensaje.value = "Cancelado"
    }
    private val _primos = MutableStateFlow<PrimosUi>(PrimosUi.Inicial)
    val primos: StateFlow<PrimosUi> = _primos.asStateFlow()
    private val _progresoPrimos = MutableStateFlow(0f)
    val progresoPrimos: StateFlow<Float> = _progresoPrimos.asStateFlow()
    private var trabajoPrimos: Job? = null

    fun contar(hasta: Int) {
        if (hasta < 2 || _primos.value is PrimosUi.Calculando) return

        trabajoPrimos = viewModelScope.launch {
            _primos.value = PrimosUi.Calculando
            _progresoPrimos.value = 0f
            val inicio = System.currentTimeMillis()
            try {
                val cantidad = repo.contarPrimos(hasta) { progreso ->
                    _progresoPrimos.value = progreso
                }
                val ms = System.currentTimeMillis() - inicio
                _primos.value = PrimosUi.Listo(cantidad, ms)
            } catch (e: CancellationException) {
                _primos.value = PrimosUi.Cancelado
                throw e
            }
        }
    }

    fun cancelarPrimos() {
        trabajoPrimos?.cancel()
    }
    private val _carga = MutableStateFlow<CargaUi>(CargaUi.Inicial)
    val carga: StateFlow<CargaUi> = _carga.asStateFlow()
    private val _intento = MutableStateFlow(0)
    val intento: StateFlow<Int> = _intento.asStateFlow()
    private val _simularError = MutableStateFlow(false)
    val simularError: StateFlow<Boolean> = _simularError.asStateFlow()
    private val _historialTiempos = MutableStateFlow<List<RegistroTiempo>>(emptyList())
    val historialTiempos: StateFlow<List<RegistroTiempo>> = _historialTiempos.asStateFlow()

    private suspend fun <T> conReintentos(
        intentos: Int = 3,
        esperaInicial: Long = 1000,
        bloque: suspend () -> T
    ): T {
        var intentoActual = 1
        var espera = esperaInicial
        while (true) {
            _intento.value = intentoActual
            try {
                return bloque()
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                if (intentoActual >= intentos) {
                    throw e
                }
                delay(espera)
                espera *= 2
                intentoActual++
            }
        }
    }

    val promediosPorModo: StateFlow<Map<String, Long>> = historialTiempos
        .map { lista ->
            lista.groupBy { it.modo }
                .mapValues { (_, registros) ->
                    registros.map { it.milisegundos }.average().toLong()
                }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )

    fun cambiarSimularError(valor: Boolean) {
        _simularError.value = valor
    }

    fun limpiarHistorial() {
        _historialTiempos.value = emptyList()
    }

    fun cargarUnaPorUna() {
        if (_carga.value is CargaUi.Cargando) return

        viewModelScope.launch {
            _carga.value = CargaUi.Cargando
            val inicio = System.currentTimeMillis()
            try {
                val perfil = repo.cargarPerfil()
                val pedidos = conReintentos { repo.cargarPedidos(_simularError.value) }

                val ms = System.currentTimeMillis() - inicio
                _carga.value = CargaUi.Exito("Una por una", perfil, pedidos.size, ms)
                _historialTiempos.value = (_historialTiempos.value + RegistroTiempo("Una por una", ms)).takeLast(10)
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                _carga.value = CargaUi.Error("No se pudo cargar tras 3 intentos: " + e.message)
            } finally {
                _intento.value = 0
            }
        }
    }
    fun cargarALaVez() {
        if (_carga.value is CargaUi.Cargando) return

        viewModelScope.launch {
            _carga.value = CargaUi.Cargando
            val inicio = System.currentTimeMillis()
            try {
                val (perfil, pedidos) = coroutineScope {
                    val a = async { repo.cargarPerfil() }
                    val b = async { conReintentos { repo.cargarPedidos(_simularError.value) } }
                    Pair(a.await(), b.await())
                }

                val ms = System.currentTimeMillis() - inicio
                _carga.value = CargaUi.Exito("A la vez", perfil, pedidos.size, ms)
                _historialTiempos.value = (_historialTiempos.value + RegistroTiempo("A la vez", ms)).takeLast(10)
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                _carga.value = CargaUi.Error("No se pudo cargar tras 3 intentos: " + e.message)
            } finally {
                _intento.value = 0
            }
        }
    }

}