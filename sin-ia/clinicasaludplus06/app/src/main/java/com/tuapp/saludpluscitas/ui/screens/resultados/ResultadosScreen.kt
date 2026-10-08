package com.tuapp.saludpluscitas.ui.screens.resultados

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.tuapp.saludpluscitas.ui.components.TopBarBase
private data class ResultadoExamenLocal(
    val id: Int,
    val titulo: String,
    val fecha: String,
    val laboratorio: String,
    val estado: String
)

@Composable
fun ResultadosScreen(navController: NavController) {
    val listaResultados = listOf(
        ResultadoExamenLocal(1, "Hemograma Completo", "10/09/2026", "Lab. SaludPlus Central", "Normal"),
        ResultadoExamenLocal(2, "Perfil Lipídico", "02/09/2026", "Lab. SaludPlus Sede Norte", "Completado"),
        ResultadoExamenLocal(3, "Examen de Orina", "20/08/2026", "Lab. SaludPlus Central", "Normal")
    )

    Scaffold(
        topBar = { TopBarBase(titulo = "Resultados Médicos", navController = navController) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(listaResultados) { res ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(res.titulo, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            Text("${res.laboratorio} • ${res.fecha}", style = MaterialTheme.typography.bodySmall)
                            Text("Estado: ${res.estado}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }
    }
}