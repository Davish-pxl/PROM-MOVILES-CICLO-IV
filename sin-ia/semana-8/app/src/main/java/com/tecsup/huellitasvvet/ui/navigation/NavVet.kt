package com.tecsup.huellitasvvet.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
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
import com.tecsup.huellitasvvet.data.prefs.PreferenciasStore
import com.tecsup.huellitasvvet.model.Rol
import com.tecsup.huellitasvvet.ui.dueno.FichaMascotaScreen
import com.tecsup.huellitasvvet.ui.dueno.FormularioMascotaScreen
import com.tecsup.huellitasvvet.ui.dueno.InicioScreen
import com.tecsup.huellitasvvet.ui.dueno.LoginScreen
import com.tecsup.huellitasvvet.ui.dueno.MascotasScreen
import com.tecsup.huellitasvvet.ui.dueno.MisCitasScreen
import com.tecsup.huellitasvvet.ui.dueno.PedirCitaScreen
import com.tecsup.huellitasvvet.ui.dueno.PerfilScreen
import com.tecsup.huellitasvvet.ui.dueno.ReprogramarCitaScreen
import com.tecsup.huellitasvvet.ui.dueno.SplashScreen
import com.tecsup.huellitasvvet.ui.veterinario.AgendaScreen
import com.tecsup.huellitasvvet.ui.veterinario.AjustesScreen
import com.tecsup.huellitasvvet.ui.veterinario.AtenderCitaScreen
import com.tecsup.huellitasvvet.ui.veterinario.FormularioServicioScreen
import com.tecsup.huellitasvvet.ui.veterinario.PacientesScreen
import com.tecsup.huellitasvvet.ui.veterinario.PanelScreen
import com.tecsup.huellitasvvet.ui.veterinario.ServiciosScreen
import com.tecsup.huellitasvvet.viewmodel.AgendaViewModel
import com.tecsup.huellitasvvet.viewmodel.AtenderCitaViewModel
import com.tecsup.huellitasvvet.viewmodel.InicioViewModel
import com.tecsup.huellitasvvet.viewmodel.MascotaViewModel
import com.tecsup.huellitasvvet.viewmodel.MisCitasViewModel
import com.tecsup.huellitasvvet.viewmodel.PacientesViewModel
import com.tecsup.huellitasvvet.viewmodel.PanelViewModel
import com.tecsup.huellitasvvet.viewmodel.PedirCitaViewModel
import com.tecsup.huellitasvvet.viewmodel.ReprogramarCitaViewModel
import com.tecsup.huellitasvvet.viewmodel.ServiciosViewModel
import com.tecsup.huellitasvvet.viewmodel.SesionViewModel
import com.tecsup.huellitasvvet.viewmodel.VetViewModelFactory

@Composable
fun NavVet(sesionVm: SesionViewModel) {
    val nav = rememberNavController()
    val perfil by sesionVm.perfilActivo.collectAsState()
    val usuario by sesionVm.usuarioActual.collectAsState()
    val modoOscuro by sesionVm.modoOscuro.collectAsState()
    val notificaciones by sesionVm.notificaciones.collectAsState()
    val aceptarCitas by sesionVm.aceptarCitas.collectAsState()
    val app = LocalContext.current.applicationContext as VetApp

    val cambiarPerfil: () -> Unit = {
        sesionVm.cambiarPerfil { rol ->
            nav.irLimpio(if (rol == Rol.ADMIN) Rutas.PANEL else Rutas.INICIO)
        }
    }
    val cerrarSesion: () -> Unit = {
        sesionVm.cerrarSesion()
        nav.irLimpio(Rutas.LOGIN)
    }

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
                        onPedirCita = { nav.navigate(Rutas.pedirCita()) },
                        onElegirServicio = { id -> nav.navigate(Rutas.pedirCita(servicioId = id)) }
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
                        onPedirCita = { nav.navigate(Rutas.pedirCita()) },
                        onReprogramar = { id -> nav.navigate(Rutas.reprogramarCita(id)) }
                    )
                }
            }
        }

        composable(Rutas.PERFIL) {
            ConBarraDueno(nav) {
                ConUsuario(usuario) { u ->
                    val inicioVm: InicioViewModel = viewModel(
                        factory = VetViewModelFactory { InicioViewModel(app.repository, u) }
                    )
                    val citasVm: MisCitasViewModel = viewModel(
                        factory = VetViewModelFactory { MisCitasViewModel(app.repository, u.id) }
                    )
                    val mascotas by inicioVm.mascotas.collectAsState()
                    val proximas by citasVm.proximas.collectAsState()
                    val atenciones by citasVm.atenciones.collectAsState()

                    PerfilScreen(
                        usuario = u,
                        cantidadMascotas = mascotas.size,
                        citasProximas = proximas.size,
                        atenciones = atenciones,
                        modoOscuro = modoOscuro,
                        recordatorios = notificaciones,
                        onModoOscuro = sesionVm::cambiarModoOscuro,
                        onRecordatorios = sesionVm::cambiarNotificaciones,
                        onCambiarPerfil = cambiarPerfil,
                        onCerrarSesion = cerrarSesion,
                        onAtras = { nav.irPestana(Rutas.INICIO, Rutas.INICIO) }
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
                        onPedirCita = { nav.navigate(Rutas.pedirCita(mascotaId = id)) }
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
            arguments = listOf(
                navArgument("mascotaId") { type = NavType.IntType; defaultValue = 0 },
                navArgument("servicioId") { type = NavType.IntType; defaultValue = 0 }
            )
        ) { entry ->
            val mascotaId = entry.arguments?.getInt("mascotaId") ?: 0
            val servicioId = entry.arguments?.getInt("servicioId") ?: 0
            ConUsuario(usuario) { u ->
                val vm: PedirCitaViewModel = viewModel(
                    factory = VetViewModelFactory {
                        PedirCitaViewModel(
                            app.repository,
                            PreferenciasStore(app),
                            u.id,
                            mascotaId.takeIf { it > 0 },
                            servicioId.takeIf { it > 0 }
                        )
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

        composable(
            Rutas.REPROGRAMAR_CITA,
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { entry ->
            val id = entry.arguments?.getInt("id") ?: 0
            val vm: ReprogramarCitaViewModel = viewModel(
                factory = VetViewModelFactory { ReprogramarCitaViewModel(app.repository, id) }
            )
            ReprogramarCitaScreen(
                vm = vm,
                onAtras = { nav.popBackStack() },
                onReprogramada = { nav.popBackStack() }
            )
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
            ConBarraVet(nav) {
                val vm: ServiciosViewModel = viewModel(
                    factory = VetViewModelFactory { ServiciosViewModel(app.repository) }
                )
                ServiciosScreen(
                    vm = vm,
                    onAtras = { nav.irPestana(Rutas.PANEL, Rutas.PANEL) },
                    onNuevo = { nav.navigate(Rutas.formServicio()) },
                    onEditar = { id -> nav.navigate(Rutas.formServicio(id)) }
                )
            }
        }

        composable(
            Rutas.FORM_SERVICIO,
            arguments = listOf(navArgument("id") { type = NavType.IntType; defaultValue = 0 })
        ) { entry ->
            val id = entry.arguments?.getInt("id") ?: 0
            val vm: ServiciosViewModel = viewModel(
                factory = VetViewModelFactory { ServiciosViewModel(app.repository) }
            )
            FormularioServicioScreen(
                vm = vm,
                servicioId = id,
                onAtras = { nav.popBackStack() },
                onGuardado = { nav.popBackStack() },
                onEliminado = { nav.popBackStack() }
            )
        }

        composable(Rutas.AJUSTES) {
            ConBarraVet(nav) {
                ConUsuario(usuario) { u ->
                    AjustesScreen(
                        usuario = u,
                        modoOscuro = modoOscuro,
                        notificaciones = notificaciones,
                        aceptarCitas = aceptarCitas,
                        onModoOscuro = sesionVm::cambiarModoOscuro,
                        onNotificaciones = sesionVm::cambiarNotificaciones,
                        onAceptarCitas = sesionVm::cambiarAceptarCitas,
                        onCambiarPerfil = cambiarPerfil,
                        onCerrarSesion = cerrarSesion,
                        onAtras = { nav.irPestana(Rutas.PANEL, Rutas.PANEL) }
                    )
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
