package com.tuapp.saludpluscitas.ui.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tuapp.saludpluscitas.R
import com.tuapp.saludpluscitas.ui.components.PrimaryButton
import com.tuapp.saludpluscitas.ui.theme.AzulPrimario
import com.tuapp.saludpluscitas.ui.theme.TextoSecundario

@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Encabezado con Logo y Marca
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_saludplus),
                contentDescription = "Logo Clínica SaludPlus",
                modifier = Modifier.size(72.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Clínica SaludPlus",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = AzulPrimario
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Tu salud, nuestra prioridad",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = TextoSecundario
            )
        }

        // Ilustración del Doctor al Centro
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_doctor),
                contentDescription = "Ilustración de Doctor",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .aspectRatio(1f)
            )
        }

        // Acciones al Final
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PrimaryButton(
                texto = "Comenzar",
                icon = Icons.AutoMirrored.Filled.ArrowForward,
                onClick = onNavigateToLogin
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(onClick = onNavigateToRegister) {
                Text(
                    text = "Ya tengo una cuenta",
                    color = AzulPrimario,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    SplashScreen(
        onNavigateToLogin = {},
        onNavigateToRegister = {}
    )
}
