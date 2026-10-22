package com.tecsup.huellitasvvet.ui.navigation

object Rutas {
    const val SPLASH = "splash"
    const val LOGIN = "login"

    const val INICIO = "inicio"
    const val MASCOTAS = "mascotas"
    const val MIS_CITAS = "mis_citas"
    const val PERFIL = "perfil"
    const val FICHA_MASCOTA = "ficha_mascota/{id}"
    const val FORM_MASCOTA = "form_mascota?id={id}"
    const val PEDIR_CITA = "pedir_cita?mascotaId={mascotaId}&servicioId={servicioId}"
    const val REPROGRAMAR_CITA = "reprogramar_cita/{id}"

    fun fichaMascota(id: Int) = "ficha_mascota/$id"
    fun formMascota(id: Int? = null) =
        if (id == null) "form_mascota" else "form_mascota?id=$id"
    fun pedirCita(mascotaId: Int? = null, servicioId: Int? = null): String {
        val parametros = buildList {
            mascotaId?.let { add("mascotaId=$it") }
            servicioId?.let { add("servicioId=$it") }
        }
        return if (parametros.isEmpty()) "pedir_cita"
        else "pedir_cita?" + parametros.joinToString("&")
    }
    fun reprogramarCita(id: Int) = "reprogramar_cita/$id"

    const val PANEL = "panel"
    const val AGENDA = "agenda"
    const val PACIENTES = "pacientes"
    const val SERVICIOS = "servicios"
    const val AJUSTES = "ajustes"
    const val ATENDER_CITA = "atender_cita/{id}"
    const val FICHA_PACIENTE = "ficha_paciente/{id}"
    const val FORM_SERVICIO = "form_servicio?id={id}"

    fun atenderCita(id: Int) = "atender_cita/$id"
    fun fichaPaciente(id: Int) = "ficha_paciente/$id"
    fun formServicio(id: Int? = null) =
        if (id == null) "form_servicio" else "form_servicio?id=$id"
}
