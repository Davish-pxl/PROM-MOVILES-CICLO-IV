package com.tuapp.clinicasalud.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tuapp.clinicasalud.model.DatosLocales
import com.tuapp.clinicasalud.model.Especialista
import kotlinx.coroutines.launch

val PrimaryPurple = Color(0xFF5A3175)
val LightPurpleCard = Color(0xFFF7F5FA)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onOpenDrawer: () -> Unit,
    onDoctorClick: (Int) -> Unit,
    onNavigateMisCitas: () -> Unit,
    onNavigateHistorial: () -> Unit,
    onNavigatePerfil: () -> Unit
) {
    var categoriaSeleccionada by remember { mutableStateOf("General") }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val especialistasFiltrados = if (categoriaSeleccionada.equals("General", ignoreCase = true)) {
        DatosLocales.listaEspecialistas
    } else {
        DatosLocales.listaEspecialistas.filter {
            it.areaMedica.equals(categoriaSeleccionada, ignoreCase = true) ||
                    it.areaMedica.contains(categoriaSeleccionada, ignoreCase = true)
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(modifier = Modifier.width(300.dp)) {
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE8E1EF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "DV", color = PrimaryPurple, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = "David Valcarcel", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(text = "Paciente", color = Color.Gray, fontSize = 14.sp)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Color.LightGray, thickness = 0.5.dp)
                Spacer(modifier = Modifier.height(8.dp))

                NavigationDrawerItem(
                    label = { Text("Inicio") },
                    selected = true,
                    onClick = { scope.launch { drawerState.close() } }
                )
                NavigationDrawerItem(
                    label = { Text("Mis citas") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNavigateMisCitas()
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Historial médico") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNavigateHistorial()
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Perfil") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNavigatePerfil()
                    }
                )
            }
        }
    ) {
        Scaffold(containerColor = Color.White) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PrimaryPurple)
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "Menú",
                                    tint = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Clínica Salud+",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Hola, David",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 14.sp,
                            modifier = Modifier.padding(start = 48.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Especialidades médicas",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(DatosLocales.categorias) { cat ->
                            val isSelected = cat == categoriaSeleccionada
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = if (isSelected) PrimaryPurple else LightPurpleCard,
                                modifier = Modifier
                                    .height(36.dp)
                                    .clickable { categoriaSeleccionada = cat }
                            ) {
                                Box(
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = cat,
                                        color = if (isSelected) Color.White else Color.DarkGray,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Médicos disponibles",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.DarkGray,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(especialistasFiltrados) { medico ->
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = LightPurpleCard),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onDoctorClick(medico.id) }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFFE8E1EF)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Add,
                                                contentDescription = null,
                                                tint = PrimaryPurple,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(text = medico.nombreCompleto, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.Black)
                                            Text(text = medico.areaMedica, fontSize = 13.sp, color = Color.Gray)
                                        }
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(text = "${medico.puntaje}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.DarkGray)
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