package com.tecsup.huellitasvvet.data.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.tecsup.huellitasvvet.model.Rol
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore("ajustes")

class PreferenciasStore(private val context: Context) {
    private companion object {
        val PERFIL_ACTIVO = stringPreferencesKey("perfilActivo")
        val USUARIO_ID = intPreferencesKey("usuarioId")
        val MODO_OSCURO = booleanPreferencesKey("modoOscuro")
        val NOTIFICACIONES = booleanPreferencesKey("notificaciones")
        val ACEPTAR_CITAS = booleanPreferencesKey("aceptarCitas")
    }

    val perfilActivo: Flow<String> =
        context.dataStore.data.map { it[PERFIL_ACTIVO] ?: "" }

    val usuarioId: Flow<Int> =
        context.dataStore.data.map { it[USUARIO_ID] ?: 0 }

    val modoOscuro: Flow<Boolean> =
        context.dataStore.data.map { it[MODO_OSCURO] ?: false }

    val notificaciones: Flow<Boolean> =
        context.dataStore.data.map { it[NOTIFICACIONES] ?: true }

    val aceptarCitas: Flow<Boolean> =
        context.dataStore.data.map { it[ACEPTAR_CITAS] ?: true }

    suspend fun guardarSesion(rol: Rol, usuarioId: Int) {
        context.dataStore.edit {
            it[PERFIL_ACTIVO] = rol.name
            it[USUARIO_ID] = usuarioId
        }
    }

    suspend fun guardarModoOscuro(b: Boolean) {
        context.dataStore.edit { it[MODO_OSCURO] = b }
    }

    suspend fun guardarNotificaciones(b: Boolean) {
        context.dataStore.edit { it[NOTIFICACIONES] = b }
    }

    suspend fun guardarAceptarCitas(b: Boolean) {
        context.dataStore.edit { it[ACEPTAR_CITAS] = b }
    }

    suspend fun cerrarSesion() {
        context.dataStore.edit {
            it.remove(PERFIL_ACTIVO)
            it.remove(USUARIO_ID)
        }
    }
}