package com.tuapp.saludpluscitas.ui.screens.agendamiento

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.tuapp.saludpluscitas.data.model.Cita
import com.tuapp.saludpluscitas.data.repository.Repositorio
import com.tuapp.saludpluscitas.navigation.Rutas
import com.tuapp.saludpluscitas.ui.components.IconBadge
import com.tuapp.saludpluscitas.ui.components.PrimaryButton
import com.tuapp.saludpluscitas.ui.components.avatarParaMedico
import com.tuapp.saludpluscitas.ui.theme.*

fun calcularRangoHora(horaInicio: String): String {
    return when (horaInicio) {
        "08:00 AM", "08:00" -> "08:00 AM a 08:30 AM"
        "08:30 AM", "08:30" -> "08:30 AM a 09:00 AM"
        "09:00 AM", "09:00" -> "09:00 AM a 09:30 AM"
        "09:30 AM", "09:30" -> "09:30 AM a 10:00 AM"
        "10:00 AM", "10:00" -> "10:00 AM a 10:30 AM"
        "10:30 AM", "10:30" -> "10:30 AM a 11:00 AM"
        "11:00 AM", "11:00" -> "11:00 AM a 11:30 AM"
        "11:30 AM", "11:30" -> "11:30 AM a 12:00 PM"
        "02:00 PM", "14:00" -> "02:00 PM a 02:30 PM"
        "03:00 PM", "15:00" -> "03:00 PM a 03:30 PM"
        "04:00 PM", "16:00" -> "04:00 PM a 04:30 PM"
        "05:00 PM", "17:00" -> "05:00 PM a 05:30 PM"
        else -> if (horaInicio.contains("a")) horaInicio else "$horaInicio a 10:00 AM"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmarCitaScreen(
    navController: NavController,
    medicoId: Int,
    fecha: String,
    hora: String,
) {
    val medico = remember(medicoId) {
        Repositorio.obtenerMedicos().find { it.id == medicoId }
    }
    val especialidad = remember(medico) {
        Repositorio.obtenerEspecialidades().find { it.id == medico?.especialidadId }
    }

    var motivoConsulta by remember { mutableStateOf("") }
    var estaGuardando by remember { mutableStateOf(false) }
    var mostrarExitoDialog by remember { mutableStateOf(false) }

    val fechaFormateada = remember(fecha) {
        if (fecha.isNotBlank()) fecha.replace("-", "/") else "Martes 16 de setiembre 2026"
    }
    val horaFormateada = remember(hora) {
        calcularRangoHora(hora)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Confirmar cita",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        color = TextoPrincipal,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar",
                            tint = TextoPrincipal,
                        )
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    PrimaryButton(
                        texto = "Agendar cita",
                        enabled = !estaGuardando,
                        onClick = {
                            if (!estaGuardando) {
                                estaGuardando = true
                                val usuarioLogueado = Repositorio.usuarioActual?.email ?: "usuario@correo.com"
                                val nuevaCita = Cita(
                                    id = (Repositorio.obtenerCitas().size + 1),
                                    usuarioEmail = usuarioLogueado,
                                    medicoId = medicoId,
                                    fecha = fechaFormateada,
                                    hora = hora,
                                )
                                Repositorio.agendarCita(nuevaCita)
                                estaGuardando = false
                                mostrarExitoDialog = true
                            }
                        },
                    )
                }
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
            Spacer(modifier = Modifier.height(8.dp))

            // Tarjeta del Médico
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SuperficieSuave),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val nombreMedico = medico?.nombre ?: "Dra. Ana Torres"
                    Image(
                        painter = painterResource(id = avatarParaMedico(nombreMedico)),
                        contentDescription = "Foto de $nombreMedico",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = nombreMedico,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextoPrincipal
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = especialidad?.nombre ?: "Ginecóloga",
                            fontSize = 14.sp,
                            color = TextoSecundario
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = medico?.cmp ?: "CMP: 12345",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextoSecundario
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Detalles de la Cita
            ItemDetalleCita(
                icono = Icons.Filled.CalendarToday,
                titulo = "Fecha",
                valor = fechaFormateada
            )

            Spacer(modifier = Modifier.height(14.dp))

            ItemDetalleCita(
                icono = Icons.Filled.AccessTime,
                titulo = "Hora",
                valor = horaFormateada
            )

            Spacer(modifier = Modifier.height(14.dp))

            ItemDetalleCita(
                icono = Icons.Filled.MedicalServices,
                titulo = "Tipo de atención",
                valor = "Consulta presencial"
            )

            Spacer(modifier = Modifier.height(14.dp))

            ItemDetalleCita(
                icono = Icons.Filled.LocationOn,
                titulo = "Dirección",
                valor = "Av. Los Olivos 123\nLima"
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Motivo de Consulta
            Text(
                text = "Motivo de consulta (opcional)",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextoPrincipal
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = motivoConsulta,
                onValueChange = { motivoConsulta = it },
                placeholder = {
                    Text(
                        text = "Consulta de rutina",
                        color = TextoSecundario.copy(alpha = 0.6f),
                        fontSize = 14.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.EditNote,
                        contentDescription = null,
                        tint = TextoSecundario
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = BordeGris,
                    focusedBorderColor = AzulPrimario
                )
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Modal de Confirmación Exitosa
    if (mostrarExitoDialog) {
        AlertDialog(
            onDismissRequest = { },
            shape = RoundedCornerShape(20.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            icon = {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = "Éxito",
                    tint = VerdeDisponible,
                    modifier = Modifier.size(64.dp)
                )
            },
            title = {
                Text(
                    text = "¡Cita agendada!",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextoPrincipal
                )
            },
            text = {
                Text(
                    text = "Tu cita con ${medico?.nombre ?: "el especialista"} ha sido registrada con éxito para el $fechaFormateada.",
                    fontSize = 14.sp,
                    color = TextoSecundario
                )
            },
            confirmButton = {
                PrimaryButton(
                    texto = "Aceptar",
                    onClick = {
                        mostrarExitoDialog = false
                        navController.navigate(Rutas.CitaExitosa.ruta) {
                            popUpTo(Rutas.Home.ruta) { inclusive = false }
                        }
                    }
                )
            }
        )
    }
}

@Composable
fun ItemDetalleCita(
    icono: ImageVector,
    titulo: String,
    valor: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconBadge(
            icon = icono,
            fondo = AzulPrimario.copy(alpha = 0.12f),
            tint = AzulPrimario,
            tamano = 44.dp
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = titulo,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = TextoSecundario
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = valor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextoPrincipal
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ConfirmarCitaScreenPreview() {
    ConfirmarCitaScreen(
        navController = rememberNavController(),
        medicoId = 6,
        fecha = "Martes 16 de setiembre 2026",
        hora = "09:30 AM"
    )
}
