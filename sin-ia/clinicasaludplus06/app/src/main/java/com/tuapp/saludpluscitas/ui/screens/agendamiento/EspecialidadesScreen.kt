package com.tuapp.saludpluscitas.ui.screens.agendamiento

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
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
import com.tuapp.saludpluscitas.ui.components.IconBadge
import com.tuapp.saludpluscitas.ui.components.iconoParaEspecialidad
import com.tuapp.saludpluscitas.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EspecialidadesScreen(navController: NavController) {
    var textoBusqueda by remember { mutableStateOf("") }
    val especialidades = Repositorio.obtenerEspecialidades()

    val especialidadesFiltradas = remember(textoBusqueda) {
        if (textoBusqueda.isBlank()) {
            especialidades
        } else {
            especialidades.filter {
                it.nombre.contains(textoBusqueda, ignoreCase = true) ||
                        it.descripcion.contains(textoBusqueda, ignoreCase = true)
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Especialidades",
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Buscador de Especialidades
            OutlinedTextField(
                value = textoBusqueda,
                onValueChange = { textoBusqueda = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Buscar especialidad...", color = TextoSecundario) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar",
                        tint = TextoSecundario,
                    )
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = BordeGris,
                    focusedBorderColor = AzulPrimario,
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (especialidadesFiltradas.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No se encontraron especialidades.",
                        fontSize = 15.sp,
                        color = TextoSecundario
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(especialidadesFiltradas) { especialidad ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    navController.navigate("${Rutas.Medicos.ruta}/${especialidad.id}")
                                },
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
                                // Insignia de Ícono
                                IconBadge(
                                    icon = iconoParaEspecialidad(especialidad.nombre),
                                    fondo = AzulPrimario.copy(alpha = 0.12f),
                                    tint = AzulPrimario,
                                    tamano = 48.dp
                                )

                                Spacer(modifier = Modifier.width(16.dp))

                                // Nombre y Descripción
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = especialidad.nombre,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextoPrincipal
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = especialidad.descripcion,
                                        fontSize = 13.sp,
                                        color = TextoSecundario
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                    contentDescription = "Ver médicos",
                                    tint = TextoSecundario
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EspecialidadesScreenPreview() {
    EspecialidadesScreen(navController = rememberNavController())
}
