package com.tecsup.huellitasvvet.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tecsup.huellitasvvet.model.Rol
import com.tecsup.huellitasvvet.ui.dueno.LoginScreen
import com.tecsup.huellitasvvet.ui.dueno.SplashScreen
import com.tecsup.huellitasvvet.viewmodel.SesionViewModel

@Composable
fun NavVet(sesionVm: SesionViewModel) {
    val nav = rememberNavController()
    val perfil by sesionVm.perfilActivo.collectAsState()

    NavHost(navController = nav, startDestination = Rutas.SPLASH) {

        composable(Rutas.SPLASH) {
            SplashScreen(
                perfil = perfil,
                onIrLogin = { nav.irLimpio(Rutas.LOGIN) },
                onIrInicio = { nav.irLimpio(Rutas.INICIO) },
                onIrPanel = { nav.irLimpio(Rutas.PANEL) }
            )
        }

        composable(Rutas.LOGIN) {
            LoginScreen(vm = sesionVm) { rol ->
                nav.irLimpio(if (rol == Rol.ADMIN) Rutas.PANEL else Rutas.INICIO)
            }
        }
        composable(Rutas.INICIO) {
            PantallaTemporal("Inicio del dueño") {
                sesionVm.cerrarSesion()
                nav.irLimpio(Rutas.LOGIN)
            }
        }
        composable(Rutas.PANEL) {
            PantallaTemporal("Panel del veterinario") {
                sesionVm.cerrarSesion()
                nav.irLimpio(Rutas.LOGIN)
            }
        }
    }
}
private fun NavHostController.irLimpio(ruta: String) {
    navigate(ruta) {
        popUpTo(graph.id) { inclusive = true }
    }
}

@Composable
private fun PantallaTemporal(titulo: String, onCerrarSesion: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(titulo, style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))
        OutlinedButton(onClick = onCerrarSesion) { Text("Cerrar sesión") }
    }
}