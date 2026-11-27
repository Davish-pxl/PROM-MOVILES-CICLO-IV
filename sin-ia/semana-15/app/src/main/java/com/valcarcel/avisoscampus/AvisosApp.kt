package com.valcarcel.avisoscampus

import android.app.Application
import com.valcarcel.avisoscampus.notificacion.Notificador

class AvisosApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Notificador.crearCanal(this)
    }
}
