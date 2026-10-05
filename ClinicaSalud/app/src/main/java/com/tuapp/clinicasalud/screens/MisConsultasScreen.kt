package com.tuapp.clinicasalud.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tuapp.clinicasalud.model.DatosLocales

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisConsultasScreen(
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Consultas") },
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
                    .padding(24.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickableWithNoIndication(onClick = onBackClick)
                ) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Atrás", tint = Color.Black)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Mis citas", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }

                Spacer(modifier = Modifier.height(24.dp))

                if (DatosLocales.consultasIniciales.isEmpty()) {
                    Text(
                        text = "No tienes consultas reservadas aún.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray
                    )
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        items(DatosLocales.consultasIniciales) { cita ->
                            val esConfirmada = cita.estadoActual == "Confirmada"

                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = LightPurpleCard),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (esConfirmada) {
                                        Box(
                                            modifier = Modifier
                                                .width(6.dp)
                                                .height(90.dp)
                                                .background(PrimaryPurple)
                                        )
                                    }

                                    Column(
                                        modifier = Modifier
                                            .padding(16.dp)
                                            .fillMaxWidth()
                                    ) {
                                        Text(
                                            text = cita.nombreDoctor,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            color = Color.Black
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = cita.fechaHoraInfo,
                                            fontSize = 13.sp,
                                            color = Color.Gray
                                        )
                                        Spacer(modifier = Modifier.height(10.dp))

                                        Surface(
                                            shape = RoundedCornerShape(12.dp),
                                            color = if (esConfirmada) Color(0xFFE2F3EC) else Color(0xFFEFEFEF)
                                        ) {
                                            Box(modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)) {
                                                Text(
                                                    text = cita.estadoActual,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (esConfirmada) Color(0xFF2E7D32) else Color.DarkGray
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}