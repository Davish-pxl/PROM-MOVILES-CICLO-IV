package com.tuapp.saludpluscitas.ui.screens.agendamiento

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import com.tuapp.saludpluscitas.ui.components.IconBadge
import com.tuapp.saludpluscitas.ui.components.TarjetaMedico
import com.tuapp.saludpluscitas.ui.components.iconoParaEspecialidad
import com.tuapp.saludpluscitas.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctoresPorEspecialidadScreen(navController: NavController) {
    val especialidades = remember { Repositorio.obtenerEspecialidades() }
    val todosLosMedicos = remember { Repositorio.obtenerMedicos() }

    // Agrupamos los médicos existentes según su especialidad
    val medicosPorEspecialidad = remember(especialidades, todosLosMedicos) {
        especialidades.mapNotNull { especialidad ->
            val medicos = todosLosMedicos.filter { it.especialidadId == especialidad.id }
            if (medicos.isNotEmpty()) especialidad to medicos else null
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Doctores por Especialidad",
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
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp)
        ) {
            medicosPorEspecialidad.forEach { (especialidad, medicos) ->
                // Encabezado de la Categoría/Especialidad
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 4.dp)
                    ) {
                        IconBadge(
                            icon = iconoParaEspecialidad(especialidad.nombre),
                            fondo = AzulPrimario.copy(alpha = 0.15f),
                            tint = AzulPrimario,
                            tamano = 36.dp
                        )
                        Text(
                            text = especialidad.nombre,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextoPrincipal
                        )
                    }
                }

                // Lista de doctores perteneciente a esta categoría
                items(medicos) { medico ->
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
                        especialidad = especialidad.nombre,
                        rating = ratingVal,
                        disponibilidad = disponibilidadVal,
                        onClick = {
                            navController.navigate("${Rutas.FechaHora.ruta}/${medico.id}")
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DoctoresPorEspecialidadScreenPreview() {
    DoctoresPorEspecialidadScreen(navController = rememberNavController())
}