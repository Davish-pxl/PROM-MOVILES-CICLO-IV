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
    const val PEDIR_CITA = "pedir_cita"

    fun fichaMascota(id: Int) = "ficha_mascota/$id"
    fun formMascota(id: Int? = null) =
        if (id == null) "form_mascota" else "form_mascota?id=$id"

    const val PANEL = "panel"
}