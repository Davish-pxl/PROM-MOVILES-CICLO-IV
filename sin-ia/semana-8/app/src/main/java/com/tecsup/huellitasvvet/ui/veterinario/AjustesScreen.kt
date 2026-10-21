package com.tecsup.huellitasvvet.ui.veterinario

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tecsup.huellitasvvet.data.local.entity.Usuario
import com.tecsup.huellitasvvet.ui.components.AppTopBar
import com.tecsup.huellitasvvet.ui.dueno.BotonCerrarSesion
import com.tecsup.huellitasvvet.ui.dueno.FilaPreferencia
import com.tecsup.huellitasvvet.ui.dueno.GrupoPreferencias
import com.tecsup.huellitasvvet.ui.dueno.NotaDataStore
import com.tecsup.huellitasvvet.ui.dueno.TarjetaCambiarPerfil
import com.tecsup.huellitasvvet.ui.dueno.TarjetaUsuario
import com.tecsup.huellitasvvet.ui.dueno.TituloPreferencias

@Composable
fun AjustesScreen(
    usuario: Usuario,
    modoOscuro: Boolean,
    notificaciones: Boolean,
    aceptarCitas: Boolean,
    onModoOscuro: (Boolean) -> Unit,
    onNotificaciones: (Boolean) -> Unit,
    onAceptarCitas: (Boolean) -> Unit,
    onCambiarPerfil: () -> Unit,
    onCerrarSesion: () -> Unit,
    onAtras: () -> Unit
) {
    Scaffold(topBar = { AppTopBar("Ajustes", onAtras) }) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            TarjetaUsuario(usuario, etiqueta = "Perfil activo")
            Spacer(Modifier.height(12.dp))
            TarjetaCambiarPerfil("Modo práctica: ir a vista Dueño", onCambiarPerfil)

            TituloPreferencias()
            GrupoPreferencias {
                FilaPreferencia("Modo oscuro", "Cambia el tema de la app", modoOscuro, onModoOscuro)
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                FilaPreferencia("Notificaciones", "Avisar citas nuevas", notificaciones, onNotificaciones)
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                FilaPreferencia("Aceptar citas", "Abrir o cerrar la agenda", aceptarCitas, onAceptarCitas)
            }
            Spacer(Modifier.height(12.dp))
            NotaDataStore()
            Spacer(Modifier.height(24.dp))
            BotonCerrarSesion(onCerrarSesion)
            Spacer(Modifier.height(24.dp))
        }
    }
}