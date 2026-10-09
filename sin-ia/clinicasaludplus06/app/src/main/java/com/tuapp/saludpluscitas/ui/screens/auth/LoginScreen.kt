package com.tuapp.saludpluscitas.ui.screens.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tuapp.saludpluscitas.data.repository.Repositorio
import com.tuapp.saludpluscitas.ui.components.CampoTexto
import com.tuapp.saludpluscitas.ui.components.PrimaryButton
import com.tuapp.saludpluscitas.ui.theme.AzulPrimario
import com.tuapp.saludpluscitas.ui.theme.TextoPrincipal
import com.tuapp.saludpluscitas.ui.theme.TextoSecundario

@Composable
fun LoginScreen(
    onLoginExitoso: () -> Unit,
    onNavigateToRegister: () -> Unit,
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var mensajeError by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Iniciar Sesión",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = TextoPrincipal
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Bienvenido de nuevo a SaludPlus",
            fontSize = 15.sp,
            color = TextoSecundario
        )

        Spacer(modifier = Modifier.height(32.dp))

        CampoTexto(
            value = email,
            onValueChange = {
                email = it
                mensajeError = ""
            },
            label = "Correo electrónico",
            placeholder = "juan@correo.com",
            leadingIcon = Icons.Outlined.Email
        )

        Spacer(modifier = Modifier.height(16.dp))

        CampoTexto(
            value = password,
            onValueChange = {
                password = it
                mensajeError = ""
            },
            label = "Contraseña",
            placeholder = "••••••••",
            leadingIcon = Icons.Outlined.Lock,
            isPassword = true
        )

        if (mensajeError.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = mensajeError,
                color = MaterialTheme.colorScheme.error,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        PrimaryButton(
            texto = "Ingresar",
            onClick = {
                if (email.isBlank() || password.isBlank()) {
                    mensajeError = "Completa todos los campos"
                } else {
                    val exito = Repositorio.iniciarSesion(email, password)
                    if (exito) {
                        onLoginExitoso()
                    } else {
                        mensajeError = "Correo o contraseña incorrectos"
                    }
                }
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier
                .clickable { onNavigateToRegister() }
                .padding(8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "¿No tienes cuenta? ",
                fontSize = 14.sp,
                color = TextoSecundario
            )
            Text(
                text = "Regístrate aquí",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = AzulPrimario
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen(
        onLoginExitoso = {},
        onNavigateToRegister = {}
    )
}
