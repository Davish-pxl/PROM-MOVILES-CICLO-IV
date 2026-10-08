package com.tuapp.saludpluscitas.ui.screens.agendamiento

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.tuapp.saludpluscitas.data.model.Cita
import com.tuapp.saludpluscitas.data.repository.Repositorio
import com.tuapp.saludpluscitas.navigation.Rutas

fun calcularRangoHora(horaInicio: String): String {
    return when (horaInicio) {
        "08:00" -> "08:00 a 08:30"
        "08:30" -> "08:30 a 09:00"
        "09:00" -> "09:00 a 09:30"
        "09:30" -> "09:30 a 10:00"
        "10:00" -> "10:00 a 10:30"
        "10:30" -> "10:30 a 11:00"
        "11:00" -> "11:00 a 11:30"
        "11:30" -> "11:30 a 12:00"
        "12:00" -> "12:00 a 12:30"
        else -> "$horaInicio a 10:00"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmarCitaScreen(
    navController: NavController,
    medicoId: Int,
    fecha: String,
    hora: String
) {
    val medico = Repositorio.obtenerMedicos().find { it.id == medicoId }
    val especialidad = Repositorio.obtenerEspecialidades().find { it.id == medico?.especialidadId }

    var motivoConsulta by remember { mutableStateOf("") }
    var estaGuardando by remember { mutableStateOf(false) }

    val fechaLimpia = fecha.replace("-", "/")
    val fechaVisual = if (fechaLimpia.contains("setiembre") || fechaLimpia.contains("Septiembre")) {
        fechaLimpia
    } else {
        "Martes 16 de setiembre 2026"
    }
    val horaFormateada = if (hora.contains("a")) hora else calcularRangoHora(hora)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Confirmar cita", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                }
            )
        },
        bottomBar = {
            Surface(color = MaterialTheme.colorScheme.surface) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Button(
                        onClick = {
                            if (!estaGuardando) {
                                estaGuardando = true
                                val usuarioLogueado = Repositorio.usuarioActual?.email ?: "usuario@correo.com"
                                val nuevaCita = Cita(
                                    id = (Repositorio.obtenerCitas().size + 1),
                                    usuarioEmail = usuarioLogueado,
                                    medicoId = medicoId,
                                    fecha = fechaLimpia,
                                    hora = hora
                                )
                                Repositorio.agendarCita(nuevaCita)

                                navController.navigate(Rutas.CitaExitosa.ruta) {
                                    popUpTo(Rutas.Home.ruta) { inclusive = false }
                                }
                            }
                        },
                        enabled = !estaGuardando,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        if (estaGuardando) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text("Agendar cita", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Tarjeta del médico
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(56.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = (medico?.nombre ?: "D").take(2).uppercase(),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = medico?.nombre ?: "Dra. Ana Torres",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = especialidad?.nombre ?: "Ginecóloga",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = medico?.cmp ?: "CMP: 12345",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))

            // Detalles de la Cita
            ItemDetalleCita(
                icono = Icons.Default.CalendarToday,
                titulo = "Fecha",
                valor = fechaVisual
            )
            Spacer(modifier = Modifier.height(12.dp))

            ItemDetalleCita(
                icono = Icons.Default.AccessTime,
                titulo = "Hora",
                valor = horaFormateada
            )
            Spacer(modifier = Modifier.height(12.dp))

            ItemDetalleCita(
                icono = Icons.Default.MedicalServices,
                titulo = "Tipo de atención",
                valor = "Consulta presencial"
            )
            Spacer(modifier = Modifier.height(12.dp))

            ItemDetalleCita(
                icono = Icons.Default.LocationOn,
                titulo = "Dirección",
                valor = "Av. Los Olivos 123\nLima"
            )
            Spacer(modifier = Modifier.height(20.dp))

            // Motivo de Consulta
            Text(
                text = "Motivo de consulta (opcional)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = motivoConsulta,
                onValueChange = { motivoConsulta = it },
                placeholder = { Text("Consulta de rutina", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun ItemDetalleCita(
    icono: ImageVector,
    titulo: String,
    valor: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = titulo,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = valor,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}