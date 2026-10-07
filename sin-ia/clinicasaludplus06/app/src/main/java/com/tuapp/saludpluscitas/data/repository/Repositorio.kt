package com.tuapp.saludpluscitas.data.repository
import com.tuapp.saludpluscitas.data.model.Usuario

object Repositorio {

    // Colección donde se guardaran para los usuarios registrados
    private val listaUsuarios = mutableListOf<Usuario>()

    // Control para la sesion
    var usuarioActual: Usuario? = null
        private set

    // Registrar Usuario
    fun registrarUsuario(usuario: Usuario): Boolean {
        val existe = listaUsuarios.any { it.email == usuario.email }
        if (existe) {
            return false
        }
        listaUsuarios.add(usuario)
        return true
    }

    // Iniciar Sesion
    fun iniciarSesion(email: String, password: String): Boolean {
        val usuarioEncontrado = listaUsuarios.find { it.email == email && it.password == password }
        if (usuarioEncontrado != null) {
            usuarioActual = usuarioEncontrado
            return true
        }
        return false
    }

    // Cerrar Sesion
    fun cerrarSesion() {
        usuarioActual = null
    }
}