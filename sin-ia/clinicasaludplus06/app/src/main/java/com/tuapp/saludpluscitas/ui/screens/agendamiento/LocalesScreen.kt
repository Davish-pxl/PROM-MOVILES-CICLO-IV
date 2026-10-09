package com.tuapp.saludpluscitas.ui.screens.agendamiento

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.tuapp.saludpluscitas.data.repository.Repositorio
import com.tuapp.saludpluscitas.ui.components.IconBadge
import com.tuapp.saludpluscitas.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocalesScreen(navController: NavController) {
    val listaLocales = remember { Repositorio.obtenerLocales() }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Selecciona un Local",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        color = TextoPrincipal
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar",
                            tint = TextoPrincipal
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
            contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
        ) {
            items(listaLocales) { local ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            // Guardamos la selección globalmente y regresamos al Home de forma segura
                            Repositorio.localSeleccionado = local
                            navController.popBackStack()
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
                        IconBadge(
                            icon = Icons.Filled.LocationOn,
                            fondo = AzulPrimario.copy(alpha = 0.15f),
                            tint = AzulPrimario,
                            tamano = 44.dp
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = local.nombre,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextoPrincipal
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${local.direccion}, ${local.distrito}",
                                fontSize = 13.sp,
                                color = TextoSecundario
                            )
                        }
                    }
                }
            }
        }
    }
}