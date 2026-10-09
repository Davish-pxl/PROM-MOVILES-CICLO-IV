package com.tuapp.saludpluscitas.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tuapp.saludpluscitas.R
import com.tuapp.saludpluscitas.data.model.Especialidad
import com.tuapp.saludpluscitas.data.repository.Repositorio
import com.tuapp.saludpluscitas.ui.components.IconBadge
import com.tuapp.saludpluscitas.ui.components.iconoParaEspecialidad
import com.tuapp.saludpluscitas.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onAgendarCitaClick: () -> Unit = {},
    onMisCitasClick: () -> Unit = {},
    onMisDatosClick: () -> Unit = {},
    onResultadosClick: () -> Unit = {},
    onNotificacionesClick: () -> Unit = {},
    onEspecialidadClick: (Int) -> Unit = {},
    onVerTodasClick: () -> Unit = {},
) {
    val usuarioNombre = Repositorio.usuarioActual?.nombre?.trim()?.substringBefore(" ") ?: "David"
    val especialidades = Repositorio.obtenerEspecialidades()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                actions = {
                    Box(modifier = Modifier.padding(end = 8.dp)) {
                        IconButton(onClick = onNotificacionesClick) {
                            Icon(
                                imageVector = Icons.Outlined.Notifications,
                                contentDescription = "Notificaciones",
                                tint = TextoPrincipal,
                            )
                        }
                        // Punto rojo de notificación
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(Color.Red)
                                .align(Alignment.TopEnd)
                                .offset(x = (-8).dp, y = 8.dp)
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Home, contentDescription = "Inicio") },
                    label = { Text("Inicio") },
                    selected = true,
                    onClick = { }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Outlined.CalendarMonth, contentDescription = "Citas") },
                    label = { Text("Citas") },
                    selected = false,
                    onClick = onMisCitasClick
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Outlined.Science, contentDescription = "Resultados") },
                    label = { Text("Resultados") },
                    selected = false,
                    onClick = onResultadosClick
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Outlined.Person, contentDescription = "Perfil") },
                    label = { Text("Perfil") },
                    selected = false,
                    onClick = onMisDatosClick
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header: Saludo e Imagen PNG del Usuario
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "¡Hola, $usuarioNombre!",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextoPrincipal
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "¿Qué deseas hacer hoy?",
                        fontSize = 15.sp,
                        color = TextoSecundario
                    )
                }

                // Avatar PNG de Perfil
                Image(
                    painter = painterResource(id = R.drawable.doctoras),
                    contentDescription = "Foto de perfil",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .clickable { onMisDatosClick() }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Tarjeta Promocional con Ilustración PNG
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AzulPrimario.copy(alpha = 0.1f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Atención médica digital",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = AzulPrimario
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Reserva tus citas con los mejores especialistas de la clínica.",
                            fontSize = 13.sp,
                            color = TextoSecundario
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Image(
                        painter = painterResource(id = R.drawable.logo_doctor),
                        contentDescription = "Promoción SaludPlus",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(80.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Cuadrícula 2x2 de Accesos Rápida
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TarjetaAccesoRapido(
                    titulo = "Agendar cita",
                    icon = Icons.Filled.CalendarMonth,
                    colorFondo = TarjetaAzul,
                    colorIcono = AzulPrimario,
                    modifier = Modifier.weight(1f),
                    onClick = onAgendarCitaClick
                )
                TarjetaAccesoRapido(
                    titulo = "Mis citas",
                    icon = Icons.Filled.EventAvailable,
                    colorFondo = TarjetaVerde,
                    colorIcono = VerdeDisponible,
                    modifier = Modifier.weight(1f),
                    onClick = onMisCitasClick
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TarjetaAccesoRapido(
                    titulo = "Mis datos",
                    icon = Icons.Filled.Person,
                    colorFondo = TarjetaLila,
                    colorIcono = Color(0xFF8B5CF6),
                    modifier = Modifier.weight(1f),
                    onClick = onMisDatosClick
                )
                TarjetaAccesoRapido(
                    titulo = "Resultados",
                    icon = Icons.Filled.Science,
                    colorFondo = TarjetaNaranja,
                    colorIcono = AmbarAviso,
                    modifier = Modifier.weight(1f),
                    onClick = onResultadosClick
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Sección Especialidades Destacadas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Especialidades destacadas",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextoPrincipal
                )
                TextButton(onClick = onVerTodasClick) {
                    Text(
                        text = "Ver todas",
                        color = AzulPrimario,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(especialidades.take(4)) { especialidad ->
                    EspecialidadDestacadaCard(
                        especialidad = especialidad,
                        onClick = { onEspecialidadClick(especialidad.id) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

/**
 * Tarjeta de acceso rápido de color para la cuadrícula 2x2.
 */
@Composable
private fun TarjetaAccesoRapido(
    titulo: String,
    icon: ImageVector,
    colorFondo: Color,
    colorIcono: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Card(
        modifier = modifier
            .height(110.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colorFondo),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start
        ) {
            IconBadge(
                icon = icon,
                fondo = colorIcono,
                tint = Color.White,
                tamano = 40.dp
            )
            Text(
                text = titulo,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = colorIcono
            )
        }
    }
}

/**
 * Tarjeta circular/estilizada para especialidades destacadas.
 */
@Composable
private fun EspecialidadDestacadaCard(
    especialidad: Especialidad,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .width(110.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SuperficieSuave),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            IconBadge(
                icon = iconoParaEspecialidad(especialidad.nombre),
                fondo = AzulPrimario.copy(alpha = 0.15f),
                tint = AzulPrimario,
                tamano = 48.dp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = especialidad.nombre,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextoPrincipal,
                maxLines = 1
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}
