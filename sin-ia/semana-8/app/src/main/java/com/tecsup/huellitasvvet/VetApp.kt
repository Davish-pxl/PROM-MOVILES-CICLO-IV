package com.tecsup.huellitasvvet

import android.app.Application
import com.tecsup.huellitasvvet.data.local.DatosIniciales
import com.tecsup.huellitasvvet.data.local.VetDatabase
import com.tecsup.huellitasvvet.data.repository.VetRepository
import com.tecsup.huellitasvvet.data.repository.VetRepositoryRoom
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class VetApp : Application() {
    val database: VetDatabase by lazy { VetDatabase.obtener(this) }
    val repository: VetRepository by lazy { VetRepositoryRoom(database)}
    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    override fun onCreate(){
        super.onCreate()
        appScope.launch {
            DatosIniciales.sembrar(database)
        }
    }
}