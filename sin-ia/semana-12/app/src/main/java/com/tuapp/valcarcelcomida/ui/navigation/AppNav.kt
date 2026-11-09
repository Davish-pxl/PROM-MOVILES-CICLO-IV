package com.tuapp.valcarcelcomida.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tuapp.valcarcelcomida.AppContainer
import com.tuapp.valcarcelcomida.ui.UiState
import com.tuapp.valcarcelcomida.ui.admin.AdminScreen
import com.tuapp.valcarcelcomida.ui.admin.AdminViewModel
import com.tuapp.valcarcelcomida.ui.detalle.DetalleScreen
import com.tuapp.valcarcelcomida.ui.detalle.DetalleViewModel
import com.tuapp.valcarcelcomida.ui.favoritos.FavoritosScreen
import com.tuapp.valcarcelcomida.ui.favoritos.FavoritosViewModel
import com.tuapp.valcarcelcomida.ui.lista.ListaScreen
import com.tuapp.valcarcelcomida.ui.lista.ListaViewModel
import com.tuapp.valcarcelcomida.ui.login.LoginScreen
import com.tuapp.valcarcelcomida.ui.login.LoginViewModel

private const val RUTA_LOGIN = "login"
private const val RUTA_LISTA = "lista"
private const val RUTA_DETALLE = "detalle/{id}"
private const val RUTA_FAVORITOS = "favoritos"
private const val RUTA_ADMIN = "admin"

@Composable
fun AppNav(container: AppContainer) {
    val nav = rememberNavController()

    NavHost(navController = nav, startDestination = RUTA_LOGIN) {

        composable(RUTA_LOGIN) {
            val vm: LoginViewModel = viewModel(factory = LoginViewModel.Factory(container))
            val estado by vm.estado.collectAsStateWithLifecycle()

            val sesion = (estado as? UiState.Exito)?.datos
            LaunchedEffect(sesion) {
                if (sesion != null) {
                    nav.navigate(RUTA_LISTA) {
                        popUpTo(RUTA_LOGIN) { inclusive = true }
                    }
                }
            }

            LoginScreen(estado = estado, onIngresar = vm::ingresar)
        }

        composable(RUTA_LISTA) {
            val vm: ListaViewModel = viewModel(factory = ListaViewModel.Factory(container))
            val estado by vm.estado.collectAsStateWithLifecycle()
            val aviso by vm.aviso.collectAsStateWithLifecycle()
            val esAdmin by vm.esAdmin.collectAsStateWithLifecycle()

            ListaScreen(
                estado = estado,
                aviso = aviso,
                onReintentar = vm::refrescar,
                onItemClick = { id -> nav.navigate("detalle/$id") },
                onFavoritoClick = vm::alternarFavorito,
                onVerFavoritos = { nav.navigate(RUTA_FAVORITOS) },
                onCerrarSesion = {
                    vm.cerrarSesion()
                    // Volver al login borrando toda la pila de pantallas
                    nav.navigate(RUTA_LOGIN) {
                        popUpTo(nav.graph.id) { inclusive = true }
                    }
                },
                esAdmin = esAdmin,
                onAbrirAdmin = { nav.navigate(RUTA_ADMIN) }
            )
        }

        composable(
            route = RUTA_DETALLE,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { entrada ->
            val id = entrada.arguments?.getString("id").orEmpty()
            val vm: DetalleViewModel = viewModel(factory = DetalleViewModel.Factory(container, id))
            val estado by vm.estado.collectAsStateWithLifecycle()

            DetalleScreen(
                estado = estado,
                onVolver = { nav.popBackStack() },
                onReintentar = vm::cargar,
                onFavoritoClick = vm::alternarFavorito
            )
        }

        composable(RUTA_FAVORITOS) {
            val vm: FavoritosViewModel = viewModel(factory = FavoritosViewModel.Factory(container))
            val estado by vm.estado.collectAsStateWithLifecycle()

            FavoritosScreen(
                estado = estado,
                onVolver = { nav.popBackStack() },
                onItemClick = { id -> nav.navigate("detalle/$id") },
                onFavoritoClick = vm::alternarFavorito
            )
        }

        composable(RUTA_ADMIN) {
            val vm: AdminViewModel = viewModel(factory = AdminViewModel.Factory(container))
            val estado by vm.estado.collectAsStateWithLifecycle()
            val mensaje by vm.mensaje.collectAsStateWithLifecycle()
            val ocupado by vm.ocupado.collectAsStateWithLifecycle()

            AdminScreen(
                estado = estado,
                mensaje = mensaje,
                ocupado = ocupado,
                onVolver = { nav.popBackStack() },
                onSincronizar = vm::sincronizar,
                onLimpiarCache = vm::limpiarCache
            )
        }
    }
}