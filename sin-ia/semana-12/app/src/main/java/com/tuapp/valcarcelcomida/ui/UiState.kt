package com.tuapp.valcarcelcomida.ui

import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

sealed interface UiState<out T> {
    data object Cargando : UiState<Nothing>
    data class Exito<T>(val datos: T) : UiState<T>

    data class Error(
        val mensaje: String,
        val titulo: String = "Algo salió mal",
        val emoji: String = "⚠️"
    ) : UiState<Nothing>
}

fun errorDe(e: Throwable): UiState.Error = when (e) {
    is SocketTimeoutException -> UiState.Error(
        titulo = "Tiempo agotado",
        mensaje = "El servidor tardó demasiado en responder. Puede ser una conexión lenta; intenta de nuevo.",
        emoji = "⏱️"
    )
    is IOException -> UiState.Error(
        titulo = "Sin conexión",
        mensaje = "No pudimos descargar las recetas. Revisa tu wifi o datos móviles e intenta de nuevo.",
        emoji = "📡"
    )
    is HttpException -> when (e.code()) {
        404 -> UiState.Error(
            titulo = "Receta no encontrada",
            mensaje = "No encontramos esta receta en el servidor. Puede que ya no exista.",
            emoji = "🔎"
        )
        in 500..599 -> UiState.Error(
            titulo = "Servidor no disponible",
            mensaje = "El servidor de recetas tiene problemas en este momento. Intenta en unos minutos.",
            emoji = "🛠️"
        )
        else -> UiState.Error(
            titulo = "Error del servidor",
            mensaje = "El servidor respondió con un error (código ${e.code()}). Intenta de nuevo.",
            emoji = "🛠️"
        )
    }
    is NoSuchElementException -> UiState.Error(
        titulo = "Receta no encontrada",
        mensaje = "No encontramos esta receta en el servidor. Puede que ya no exista.",
        emoji = "🔎"
    )
    else -> UiState.Error(
        titulo = "Algo salió mal",
        mensaje = "Ocurrió un error inesperado. Intenta de nuevo en un momento.",
        emoji = "⚠️"
    )
}