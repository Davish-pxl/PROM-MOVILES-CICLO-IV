package com.tuapp.saludpluscitas.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.tuapp.saludpluscitas.ui.components.TopBarBase

@Composable
fun TerminosScreen(navController: NavController) {
    Scaffold(
        topBar = { TopBarBase(titulo = "Términos y Condiciones", navController = navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Términos y Condiciones del Servicio",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = """
                1. Aceptación del Servicio:
                Al utilizar la aplicación SaludPlus Citas, el usuario acepta todos los términos y políticas aquí estipulados.
                
                2. Uso de Datos Personales:
                Los datos e historial de citas registrados se guardan en el dispositivo para fines únicamente demostrativos y de agendamiento médico.
                
                3. Cancelación de Citas:
                El paciente puede cancelar o reprogramar sus citas desde el módulo de 'Mis Citas' o 'Detalle de Cita' sin costo alguno.
                
                4. Responsabilidad:
                La clínica SaludPlus garantiza la confidencialidad de la información registrada por el usuario paciente.
                """.trimIndent(),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}