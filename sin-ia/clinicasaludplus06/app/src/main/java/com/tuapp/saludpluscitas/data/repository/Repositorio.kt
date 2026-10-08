package com.tuapp.saludpluscitas.data.repository
import com.tuapp.saludpluscitas.data.model.Especialidad
import com.tuapp.saludpluscitas.data.model.Medico
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
        usuarioActual = usuario
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
    // Especialidades
    private val listaEspecialidades = mutableListOf(
        Especialidad(1, "Medicina General", "Atención integral"),
        Especialidad(2, "Pediatria", "Niños y adolescentes"),
        Especialidad(3, "Ginecologia", "Salud de la mujer"),
        Especialidad(4, "Cardiologia", "Corazon y vasos sanguineos"),
        Especialidad(5, "Dermatologia", "Piel, cabello y uñas"),
        Especialidad(6, "Traumotologia", "Huesos y articulaciones"),
        Especialidad(7, "Oftalmologia", "Salud Visual")

    )

    fun obtenerEspecialidades(): List<Especialidad> {
        return listaEspecialidades
    }

    // Medicos y Especialidades
    private val listaMedicos = mutableListOf(
        Medico(1, "Dr. Carlos Pérez", 1, "Lun - Vie: 8:00 AM - 1:00 PM"),
        Medico(2, "Dra. María Gómez", 1, "Lun - Vie: 2:00 PM - 6:00 PM"),
        Medico(3, "Dr. Luis Ramirez", 3, "Mar - Jue: 9:00 AM - 1:00 PM"),
        Medico(4, "Dra. Mariana Soto", 3, "Mar - Jue: 9:00 AM - 1:00 PM"),
        Medico(5, "Dra. Claudia Rojas", 3, "Mar - Jue: 9:00 AM - 1:00 PM"),
        Medico(6, "Dra. Ana Torres", 3, "Lun - Sáb: 8:00 AM - 12:00 PM"),
        Medico(7, "Dr. Luis Mendoza", 4, "Mié - Vie: 10:00 AM - 4:00 PM")
    )

    fun obtenerMedicos(): List<Medico> {
        return listaMedicos
    }

    fun obtenerMedicosPorEspecialidad(especialidadId: Int): List<Medico> {
        return listaMedicos.filter { it.especialidadId == especialidadId }
    }
}