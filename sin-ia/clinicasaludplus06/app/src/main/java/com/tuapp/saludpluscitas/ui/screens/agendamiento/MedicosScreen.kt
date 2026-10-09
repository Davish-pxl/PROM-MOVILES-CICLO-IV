package com.tuapp.saludpluscitas.ui.screens.agendamiento

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.tuapp.saludpluscitas.data.repository.Repositorio
import com.tuapp.saludpluscitas.navigation.Rutas
import com.tuapp.saludpluscitas.ui.components.TarjetaMedico
import com.tuapp.saludpluscitas.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicosScreen(
    navController: NavController,
    especialidadId: Int,
) {
    var estaBuscando by remember { mutableStateOf(false) }
    var textoBusqueda by remember { mutableStateOf("") }

    val todosMedicos = remember(especialidadId) {
        if (especialidadId == 0) {
            Repositorio.obtenerMedicos()
        } else {
            Repositorio.obtenerMedicosPorEspecialidad(especialidadId)
        }
    }

    val medicosFiltrados = remember(textoBusqueda, todosMedicos) {
        if (textoBusqueda.isBlank()) {
            todosMedicos
        } else {
            todosMedicos.filter {
                it.nombre.contains(textoBusqueda, ignoreCase = true) ||
                        it.cmp.contains(textoBusqueda, ignoreCase = true)
            }
        }
    }

    val especialidadObj = remember(especialidadId) {
        Repositorio.obtenerEspecialidades().find { it.id == especialidadId }
    }
    val tituloBarra = if (especialidadObj != null) {
        "Médicos de ${especialidadObj.nombre}"
    } else {
        "Médicos disponibles"
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    if (estaBuscando) {
                        OutlinedTextField(
                            value = textoBusqueda,
                            onValueChange = { textoBusqueda = it },
                            placeholder = { Text("Buscar médico...", fontSize = 14.sp) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                        )
                    } else {
                        Text(
                            text = tituloBarra,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp,
                            color = TextoPrincipal,
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar",
                            tint = TextoPrincipal,
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            estaBuscando = !estaBuscando
                            if (!estaBuscando) textoBusqueda = ""
                        }
                    ) {
                        Icon(
                            imageVector = if (estaBuscando) Icons.Default.Close else Icons.Default.Search,
                            contentDescription = if (estaBuscando) "Cerrar búsqueda" else "Buscar médico",
                            tint = TextoPrincipal,
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            if (medicosFiltrados.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No se encontraron médicos disponibles.",
                        fontSize = 15.sp,
                        color = TextoSecundario
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(medicosFiltrados) { medico ->
                        val espNombre = Repositorio.obtenerEspecialidades()
                            .find { it.id == medico.especialidadId }?.nombre ?: "Especialista"

                        val ratingVal = when (medico.id) {
                            1 -> "4.9 (120)"
                            2 -> "4.8 (95)"
                            3 -> "4.7 (85)"
                            4 -> "4.6 (76)"
                            5 -> "4.8 (92)"
                            else -> "4.9 (110)"
                        }

                        val disponibilidadVal = when (medico.id) {
                            1, 3, 6 -> "Disponible hoy"
                            2, 5 -> "Disponible mañana"
                            else -> "Disponible esta semana"
                        }

                        TarjetaMedico(
                            nombre = medico.nombre,
                            especialidad = espNombre,
                            rating = ratingVal,
                            disponibilidad = disponibilidadVal,
                            onClick = {
                                navController.navigate("${Rutas.FechaHora.ruta}/${medico.id}")
                            },
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MedicosScreenPreview() {
    MedicosScreen(
        navController = rememberNavController(),
        especialidadId = 3,
    )
}
