package com.tecsup.huellitasvvet.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tecsup.huellitasvvet.VetApp
import com.tecsup.huellitasvvet.data.local.entity.Usuario
import com.tecsup.huellitasvvet.model.Rol
import com.tecsup.huellitasvvet.ui.dueno.FichaMascotaScreen
import com.tecsup.huellitasvvet.ui.dueno.FormularioMascotaScreen
import com.tecsup.huellitasvvet.ui.dueno.InicioScreen
import com.tecsup.huellitasvvet.ui.dueno.LoginScreen
import com.tecsup.huellitasvvet.ui.dueno.MascotasScreen
import com.tecsup.huellitasvvet.ui.dueno.MisCitasScreen
import com.tecsup.huellitasvvet.ui.dueno.PedirCitaScreen
import com.tecsup.huellitasvvet.ui.dueno.PerfilScreen
import com.tecsup.huellitasvvet.ui.dueno.SplashScreen
import com.tecsup.huellitasvvet.ui.veterinario.AgendaScreen
import com.tecsup.huellitasvvet.ui.veterinario.AtenderCitaScreen
import com.tecsup.huellitasvvet.ui.veterinario.PacientesScreen
import com.tecsup.huellitasvvet.ui.veterinario.PanelScreen
import com.tecsup.huellitasvvet.viewmodel.AgendaViewModel
import com.tecsup.huellitasvvet.viewmodel.AtenderCitaViewModel
import com.tecsup.huellitasvvet.viewmodel.InicioViewModel
import com.tecsup.huellitasvvet.viewmodel.MascotaViewModel
import com.tecsup.huellitasvvet.viewmodel.MisCitasViewModel
import com.tecsup.huellitasvvet.viewmodel.PacientesViewModel
import com.tecsup.huellitasvvet.viewmodel.PanelViewModel
import com.tecsup.huellitasvvet.viewmodel.PedirCitaViewModel
import com.tecsup.huellitasvvet.viewmodel.SesionViewModel
import com.tecsup.huellitasvvet.viewmodel.VetViewModelFactory

@Composable
fun NavVet(sesionVm: SesionViewModel) {
    val nav = rememberNavController()
    val perfil by sesionVm.perfilActivo.collectAsState()
    val usuario by sesionVm.usuarioActual.collectAsState()
    val app = LocalContext.current.applicationContext as VetApp

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
            ConBarraDueno(nav) {
                ConUsuario(usuario) { u ->
                    val vm: InicioViewModel = viewModel(
                        factory = VetViewModelFactory { InicioViewModel(app.repository, u) }
                    )
                    InicioScreen(
                        vm = vm,
                        onAbrirMascota = { id -> nav.navigate(Rutas.fichaMascota(id)) },
                        onAgregarMascota = { nav.navigate(Rutas.formMascota()) },
                        onVerTodas = { nav.irPestana(Rutas.MASCOTAS, Rutas.INICIO) },
                        onPedirCita = { nav.navigate(Rutas.pedirCita()) }
                    )
                }
            }
        }

        composable(Rutas.MASCOTAS) {
            ConBarraDueno(nav) {
                ConUsuario(usuario) { u ->
                    val vm: InicioViewModel = viewModel(
                        factory = VetViewModelFactory { InicioViewModel(app.repository, u) }
                    )
                    MascotasScreen(
                        vm = vm,
                        onAbrirMascota = { id -> nav.navigate(Rutas.fichaMascota(id)) },
                        onAgregar = { nav.navigate(Rutas.formMascota()) }
                    )
                }
            }
        }

        composable(Rutas.MIS_CITAS) {
            ConBarraDueno(nav) {
                ConUsuario(usuario) { u ->
                    val vm: MisCitasViewModel = viewModel(
                        factory = VetViewModelFactory { MisCitasViewModel(app.repository, u.id) }
                    )
                    MisCitasScreen(
                        vm = vm,
                        onAtras = { nav.irPestana(Rutas.INICIO, Rutas.INICIO) },
                        onPedirCita = { nav.navigate(Rutas.pedirCita()) }
                    )
                }
            }
        }

        composable(Rutas.PERFIL) {
            ConBarraDueno(nav) {
                ConUsuario(usuario) { u ->
                    PerfilScreen(
                        usuario = u,
                        onCerrarSesion = {
                            sesionVm.cerrarSesion()
                            nav.irLimpio(Rutas.LOGIN)
                        }
                    )
                }
            }
        }

        composable(
            Rutas.FICHA_MASCOTA,
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { entry ->
            val id = entry.arguments?.getInt("id") ?: 0
            ConBarraDueno(nav) {
                ConUsuario(usuario) { u ->
                    val vm: MascotaViewModel = viewModel(
                        factory = VetViewModelFactory { MascotaViewModel(app.repository, u.id) }
                    )
                    FichaMascotaScreen(
                        vm = vm,
                        mascotaId = id,
                        onAtras = { nav.popBackStack() },
                        onEditar = { nav.navigate(Rutas.formMascota(id)) },
                        onPedirCita = { nav.navigate(Rutas.pedirCita(id)) }
                    )
                }
            }
        }

        composable(
            Rutas.FORM_MASCOTA,
            arguments = listOf(navArgument("id") { type = NavType.IntType; defaultValue = 0 })
        ) { entry ->
            val id = entry.arguments?.getInt("id") ?: 0
            ConUsuario(usuario) { u ->
                val vm: MascotaViewModel = viewModel(
                    factory = VetViewModelFactory { MascotaViewModel(app.repository, u.id) }
                )
                FormularioMascotaScreen(
                    vm = vm,
                    mascotaId = id,
                    onAtras = { nav.popBackStack() },
                    onGuardado = { nav.popBackStack() },
                    onEliminada = { nav.popBackStack(Rutas.FICHA_MASCOTA, inclusive = true) }
                )
            }
        }

        composable(
            Rutas.PEDIR_CITA,
            arguments = listOf(navArgument("mascotaId") { type = NavType.IntType; defaultValue = 0 })
        ) { entry ->
            val mascotaId = entry.arguments?.getInt("mascotaId") ?: 0
            ConUsuario(usuario) { u ->
                val vm: PedirCitaViewModel = viewModel(
                    factory = VetViewModelFactory {
                        PedirCitaViewModel(app.repository, u.id, mascotaId.takeIf { it > 0 })
                    }
                )
                PedirCitaScreen(
                    vm = vm,
                    onAtras = { nav.popBackStack() },
                    onCitaCreada = {
                        nav.navigate(Rutas.MIS_CITAS) {
                            popUpTo(Rutas.INICIO)
                            launchSingleTop = true
                        }
                    }
                )
            }
        }

        composable(Rutas.PANEL) {
            ConBarraVet(nav) {
                val vm: PanelViewModel = viewModel(
                    factory = VetViewModelFactory { PanelViewModel(app.repository) }
                )
                PanelScreen(
                    vm = vm,
                    onVerAgenda = { nav.irPestana(Rutas.AGENDA, Rutas.PANEL) }
                )
            }
        }

        composable(Rutas.AGENDA) {
            ConBarraVet(nav) {
                val vm: AgendaViewModel = viewModel(
                    factory = VetViewModelFactory { AgendaViewModel(app.repository) }
                )
                AgendaScreen(
                    vm = vm,
                    onAtras = { nav.irPestana(Rutas.PANEL, Rutas.PANEL) },
                    onAtender = { id -> nav.navigate(Rutas.atenderCita(id)) }
                )
            }
        }

        composable(Rutas.PACIENTES) {
            ConBarraVet(nav) {
                val vm: PacientesViewModel = viewModel(
                    factory = VetViewModelFactory { PacientesViewModel(app.repository) }
                )
                PacientesScreen(
                    vm = vm,
                    onAtras = { nav.irPestana(Rutas.PANEL, Rutas.PANEL) },
                    onAbrirPaciente = { id -> nav.navigate(Rutas.fichaPaciente(id)) }
                )
            }
        }

        composable(
            Rutas.FICHA_PACIENTE,
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { entry ->
            val id = entry.arguments?.getInt("id") ?: 0
            ConBarraVet(nav) {
                val vm: MascotaViewModel = viewModel(
                    factory = VetViewModelFactory { MascotaViewModel(app.repository, 0) }
                )
                FichaMascotaScreen(
                    vm = vm,
                    mascotaId = id,
                    onAtras = { nav.popBackStack() }
                )
            }
        }

        composable(Rutas.SERVICIOS) {
            ConBarraVet(nav) { PantallaPendiente("Servicios\n(Paso 13)") }
        }

        composable(Rutas.AJUSTES) {
            ConBarraVet(nav) {
                PantallaTemporal("Ajustes\n(Paso 13)") {
                    sesionVm.cerrarSesion()
                    nav.irLimpio(Rutas.LOGIN)
                }
            }
        }

        composable(
            Rutas.ATENDER_CITA,
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { entry ->
            val id = entry.arguments?.getInt("id") ?: 0
            val vm: AtenderCitaViewModel = viewModel(
                factory = VetViewModelFactory { AtenderCitaViewModel(app.repository, id) }
            )
            AtenderCitaScreen(
                vm = vm,
                onAtras = { nav.popBackStack() },
                onTerminado = { nav.popBackStack() }
            )
        }
    }
}

private data class ItemBarra(val ruta: String, val titulo: String, val icono: ImageVector)

private val itemsDueno = listOf(
    ItemBarra(Rutas.INICIO, "Inicio", Icons.Outlined.Home),
    ItemBarra(Rutas.MASCOTAS, "Mascotas", Icons.Outlined.Pets),
    ItemBarra(Rutas.MIS_CITAS, "Citas", Icons.Outlined.CalendarMonth),
    ItemBarra(Rutas.PERFIL, "Perfil", Icons.Outlined.Person)
)

private val itemsVet = listOf(
    ItemBarra(Rutas.PANEL, "Panel", Icons.Outlined.BarChart),
    ItemBarra(Rutas.AGENDA, "Agenda", Icons.Outlined.CalendarMonth),
    ItemBarra(Rutas.PACIENTES, "Pacientes", Icons.Outlined.Pets),
    ItemBarra(Rutas.SERVICIOS, "Servicios", Icons.Outlined.LocalOffer),
    ItemBarra(Rutas.AJUSTES, "Ajustes", Icons.Outlined.Settings)
)

@Composable
private fun ConBarraDueno(nav: NavHostController, contenido: @Composable () -> Unit) =
    ConBarra(nav, itemsDueno, Rutas.INICIO, contenido)

@Composable
private fun ConBarraVet(nav: NavHostController, contenido: @Composable () -> Unit) =
    ConBarra(nav, itemsVet, Rutas.PANEL, contenido)
@Composable
private fun ConBarra(
    nav: NavHostController,
    items: List<ItemBarra>,
    raiz: String,
    contenido: @Composable () -> Unit
) {
    val rutaActual = nav.currentBackStackEntryAsState().value?.destination?.route
    Scaffold(
        bottomBar = {
            NavigationBar {
                items.forEach { item ->
                    NavigationBarItem(
                        selected = rutaActual == item.ruta ||
                                (item.ruta == Rutas.MASCOTAS && rutaActual == Rutas.FICHA_MASCOTA) ||
                                (item.ruta == Rutas.PACIENTES && rutaActual == Rutas.FICHA_PACIENTE),
                        onClick = { nav.irPestana(item.ruta, raiz) },
                        icon = { Icon(item.icono, contentDescription = null) },
                        label = { Text(item.titulo) }
                    )
                }
            }
        }
    ) { padding ->
        Box(Modifier.padding(padding)) { contenido() }
    }
}
@Composable
private fun ConUsuario(usuario: Usuario?, contenido: @Composable (Usuario) -> Unit) {
    if (usuario == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        contenido(usuario)
    }
}

private fun NavHostController.irLimpio(ruta: String) {
    navigate(ruta) {
        popUpTo(graph.id) { inclusive = true }
    }
}
private fun NavHostController.irPestana(ruta: String, raiz: String) {
    if (ruta == raiz) {
        if (!popBackStack(raiz, inclusive = false)) {
            navigate(raiz)
        }
        return
    }
    navigate(ruta) {
        popUpTo(raiz) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
private fun PantallaPendiente(titulo: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            titulo,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun PantallaTemporal(titulo: String, onCerrarSesion: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(titulo, style = MaterialTheme.typography.headlineSmall, textAlign = TextAlign.Center)
        Spacer(Modifier.height(16.dp))
        OutlinedButton(onClick = onCerrarSesion) { Text("Cerrar sesión") }
    }
}