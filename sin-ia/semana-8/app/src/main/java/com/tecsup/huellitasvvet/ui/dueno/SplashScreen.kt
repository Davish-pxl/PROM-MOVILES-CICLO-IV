package com.tecsup.huellitasvvet.ui.dueno

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tecsup.huellitasvvet.model.Especie
import com.tecsup.huellitasvvet.model.Rol
import com.tecsup.huellitasvvet.ui.components.MascotaAvatar
import com.tecsup.huellitasvvet.ui.components.VetButton
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    perfil: String?,
    onIrLogin: () -> Unit,
    onIrInicio: () -> Unit,
    onIrPanel: () -> Unit
) {
    LaunchedEffect(perfil) {
        when (perfil) {
            Rol.CLIENTE.name -> { delay(800); onIrInicio() }
            Rol.ADMIN.name -> { delay(800); onIrPanel() }
            else -> Unit
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.weight(1f))

        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Filled.Pets,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(56.dp)
            )
        }
        Spacer(Modifier.height(24.dp))

        Text(
            "HuellitasVet",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(Modifier.height(6.dp))
        Text(
            "Cuidamos a tu mejor amigo",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(56.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            listOf(Especie.PERRO, Especie.GATO, Especie.PERRO).forEach { especie ->
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)),
                    contentAlignment = Alignment.Center
                ) {
                    MascotaAvatar(especie = especie.name, tamano = 52.dp)
                }
            }
        }

        Spacer(Modifier.weight(1f))

        if (perfil == "") {
            VetButton(texto = "Comenzar", onClick = onIrLogin)
            Spacer(Modifier.height(8.dp))
            TextButton(onClick = onIrLogin, modifier = Modifier.fillMaxWidth()) {
                Text("Ya tengo una cuenta", fontWeight = FontWeight.SemiBold)
            }
        } else {
            Spacer(Modifier.height(108.dp))
        }
    }
}