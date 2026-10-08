package com.tuapp.saludpluscitas.data.repository

import com.tuapp.saludpluscitas.data.model.Cita
import com.tuapp.saludpluscitas.data.model.Especialidad
import com.tuapp.saludpluscitas.data.model.Medico
import com.tuapp.saludpluscitas.data.model.Usuario

object Repositorio {

    // Colección donde se guardarán los usuarios registrados
    private val listaUsuarios = mutableListOf<Usuario>()

    // Colección en memoria para las citas agendadas
    private val listaCitas = mutableListOf<Cita>()

    // Control para la sesión
    var usuarioActual: Usuario? = null
        private set

    // Registrar Usuario
    fun registrarUsuario(usuario: Usuario): Boolean {
        val existe = listaUsuarios.any { it.email == usuario.email }
        if (existe) {
            return false
        }
        listaUsuarios.add(usuario)
        usuarioActual = usuario
        return true
    }

    // Iniciar Sesión
    fun iniciarSesion(email: String, password: String): Boolean {
        val usuarioEncontrado = listaUsuarios.find { it.email == email && it.password == password }
        if (usuarioEncontrado != null) {
            usuarioActual = usuarioEncontrado
            return true
        }
        return false
    }

    // Cerrar Sesión
    fun cerrarSesion() {
        usuarioActual = null
    }

    // Especialidades
    private val listaEspecialidades = mutableListOf(
        Especialidad(1, "Medicina General", "Atención integral"),
        Especialidad(2, "Pediatría", "Niños y adolescentes"),
        Especialidad(3, "Ginecología", "Salud de la mujer"),
        Especialidad(4, "Cardiología", "Corazón y vasos sanguíneos"),
        Especialidad(5, "Dermatología", "Piel, cabello y uñas"),
        Especialidad(6, "Traumatología", "Huesos y articulaciones"),
        Especialidad(7, "Oftalmología", "Salud Visual")
    )

    fun obtenerEspecialidades(): List<Especialidad> {
        return listaEspecialidades
    }

    // Médicos y Especialidades
    private val listaMedicos = mutableListOf(
        Medico(1, "Dr. Carlos Pérez", 1, "Lun - Vie: 8:00 AM - 1:00 PM", "CMP: 45892"),
        Medico(2, "Dra. María Gómez", 1, "Lun - Vie: 2:00 PM - 6:00 PM", "CMP: 51204"),
        Medico(3, "Dr. Luis Ramirez", 3, "Mar - Jue: 9:00 AM - 1:00 PM", "CMP: 38910"),
        Medico(4, "Dra. Mariana Soto", 3, "Mar - Jue: 9:00 AM - 1:00 PM", "CMP: 62145"),
        Medico(5, "Dra. Claudia Rojas", 3, "Mar - Jue: 9:00 AM - 1:00 PM", "CMP: 47831"),
        Medico(6, "Dra. Ana Torres", 3, "Lun - Sáb: 8:00 AM - 12:00 PM", "CMP: 12345"),
        Medico(7, "Dr. Luis Mendoza", 4, "Mié - Vie: 10:00 AM - 4:00 PM", "CMP: 59012")
    )

    fun obtenerMedicos(): List<Medico> {
        return listaMedicos
    }

    fun obtenerMedicosPorEspecialidad(especialidadId: Int): List<Medico> {
        return listaMedicos.filter { it.especialidadId == especialidadId }
    }

    // Citas en memoria
    fun obtenerCitas(): List<Cita> {
        return listaCitas
    }

    fun agendarCita(cita: Cita): Boolean {
        return listaCitas.add(cita)
    }
}