package com.tuapp.clinicasalud.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tuapp.clinicasalud.model.DatosLocales

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservarConsultaScreen(
    medicoId: Int,
    onBackClick: () -> Unit,
    onConfirmBooking: (String, String, String) -> Unit
) {
    val especialista = DatosLocales.listaEspecialistas.find { it.id == medicoId }
        ?: DatosLocales.listaEspecialistas.first()

    var fechaIndexSeleccionada by remember { mutableStateOf(1) } // Por defecto "Vie 27"
    var horaSeleccionada by remember { mutableStateOf("10:30") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reservar Cita Médica") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onBackClick() }
                    ) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Atrás", tint = Color.Black)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Agendar cita", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(text = "Selecciona fecha", fontSize = 14.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Selector de fechas
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        DatosLocales.fechasDisponibles.forEachIndexed { index, opcion ->
                            val isSelected = index == fechaIndexSeleccionada
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(72.dp)
                                    .background(
                                        color = if (isSelected) PrimaryPurple else LightPurpleCard,
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .clickable { fechaIndexSeleccionada = index },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = opcion.diaSemana,
                                        color = if (isSelected) Color.White.copy(alpha = 0.8f) else Color.Gray,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = opcion.numeroDia,
                                        color = if (isSelected) Color.White else Color.Black,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(text = "Selecciona hora", fontSize = 14.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Selector de horas
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        DatosLocales.horasDisponibles.forEach { hora ->
                            val isSelected = hora == horaSeleccionada
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .background(
                                        color = if (isSelected) PrimaryPurple else LightPurpleCard,
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .clickable { horaSeleccionada = hora },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = hora,
                                    color = if (isSelected) Color.White else Color.Black,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }

                Button(
                    onClick = {
                        val fechaElegidaTexto = "${DatosLocales.fechasDisponibles[fechaIndexSeleccionada].diaSemana} ${DatosLocales.fechasDisponibles[fechaIndexSeleccionada].numeroDia}"
                        onConfirmBooking(especialista.nombreCompleto, fechaElegidaTexto, horaSeleccionada)
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                ) {
                    Text(text = "Confirmar cita", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}