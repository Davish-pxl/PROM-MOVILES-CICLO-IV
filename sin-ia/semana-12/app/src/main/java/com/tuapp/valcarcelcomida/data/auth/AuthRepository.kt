package com.tuapp.valcarcelcomida.data.auth

import com.tuapp.valcarcelcomida.domain.model.Sesion

interface AuthRepository {
    suspend fun  iniciarSesion(correo: String, clave: String): Result<Sesion>
    suspend fun  sesionActual(): Sesion?
    fun cerrarSesion()
}