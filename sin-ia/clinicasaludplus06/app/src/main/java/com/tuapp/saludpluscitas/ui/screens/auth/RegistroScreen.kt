package com.tuapp.saludpluscitas.ui.screens.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tuapp.saludpluscitas.data.model.Usuario
import com.tuapp.saludpluscitas.data.repository.Repositorio
import com.tuapp.saludpluscitas.ui.components.CampoTexto
import com.tuapp.saludpluscitas.ui.components.PrimaryButton
import com.tuapp.saludpluscitas.ui.theme.AzulPrimario
import com.tuapp.saludpluscitas.ui.theme.TextoPrincipal
import com.tuapp.saludpluscitas.ui.theme.TextoSecundario

@Composable
fun RegistroScreen(
    onRegistroExitoso: () -> Unit,
    onBackToLogin: () -> Unit,
    onTerminosClick: () -> Unit,
) {
    var nombre by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
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

        // Encabezado
        Text(
            text = "Crear cuenta",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = TextoPrincipal
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Regístrate para agendar tus citas",
            fontSize = 15.sp,
            color = TextoSecundario
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Campos de Formulario
        CampoTexto(
            value = nombre,
            onValueChange = {
                nombre = it
                mensajeError = ""
            },
            label = "Nombre completo",
            placeholder = "Juan Pérez",
            leadingIcon = Icons.Outlined.Person
        )

        Spacer(modifier = Modifier.height(16.dp))

        CampoTexto(
            value = telefono,
            onValueChange = {
                telefono = it
                mensajeError = ""
            },
            label = "Teléfono",
            placeholder = "987 654 321",
            leadingIcon = Icons.Outlined.Phone
        )

        Spacer(modifier = Modifier.height(16.dp))

        CampoTexto(
            value = email,
            onValueChange = {
                email = it
                mensajeError = ""
            },
            label = "Correo (opcional)",
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

        // Botón de Registro
        PrimaryButton(
            texto = "Registrarme",
            onClick = {
                if (nombre.isBlank() || password.isBlank()) {
                    mensajeError = "Por favor completa tu nombre y contraseña"
                } else {
                    val correoFinal = email.ifBlank { "$nombre@correo.com".lowercase().replace(" ", "") }
                    val nuevoUsuario = Usuario(email = correoFinal, password = password, nombre = nombre)
                    val registrado = Repositorio.registrarUsuario(nuevoUsuario)
                    if (registrado) {
                        onRegistroExitoso()
                    } else {
                        mensajeError = "El correo ya se encuentra registrado"
                    }
                }
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Términos y Condiciones
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Al registrarte aceptas nuestros ",
                fontSize = 12.sp,
                color = TextoSecundario
            )
            Text(
                text = "Términos y Condiciones",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = AzulPrimario,
                modifier = Modifier.clickable { onTerminosClick() }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Enlace Iniciar Sesión
        Row(
            modifier = Modifier
                .clickable { onBackToLogin() }
                .padding(8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "¿Ya tienes cuenta? ",
                fontSize = 14.sp,
                color = TextoSecundario
            )
            Text(
                text = "Iniciar sesión",
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
fun RegistroScreenPreview() {
    RegistroScreen(
        onRegistroExitoso = {},
        onBackToLogin = {},
        onTerminosClick = {}
    )
}
