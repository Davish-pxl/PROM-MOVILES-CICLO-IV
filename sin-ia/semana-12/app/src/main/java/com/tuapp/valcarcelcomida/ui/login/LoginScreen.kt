package com.tuapp.valcarcelcomida.ui.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tuapp.valcarcelcomida.domain.model.Sesion
import com.tuapp.valcarcelcomida.ui.UiState
import com.tuapp.valcarcelcomida.ui.components.Cargando
import com.tuapp.valcarcelcomida.ui.theme.ValcarcelComidaTheme

@Composable
fun LoginScreen(
    estado: UiState<Sesion?>,
    onIngresar: (correo: String, clave: String) -> Unit
) {
    var correo by rememberSaveable { mutableStateOf("") }
    var clave by rememberSaveable { mutableStateOf("") }

    Surface(Modifier.fillMaxSize()) {
        when {
            estado is UiState.Cargando ||
                    (estado is UiState.Exito && estado.datos != null) -> Cargando()

            else -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding()
                    .imePadding()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Comidas · Chicken", style = MaterialTheme.typography.headlineMedium)
                Text("Inicia sesión para continuar", style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(24.dp))

                OutlinedTextField(
                    value = correo,
                    onValueChange = { correo = it },
                    label = { Text("Correo") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = clave,
                    onValueChange = { clave = it },
                    label = { Text("Contraseña") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                if (estado is UiState.Error) {
                    Spacer(Modifier.height(12.dp))
                    Text(estado.mensaje, color = MaterialTheme.colorScheme.error)
                }

                Spacer(Modifier.height(24.dp))
                Button(
                    onClick = { onIngresar(correo, clave) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Ingresar")
                }
            }
        }
    }
}


@Preview(showBackground = true, name = "Formulario")
@Composable
private fun PreviewLogin() = ValcarcelComidaTheme {
    LoginScreen(UiState.Exito(null)) { _, _ -> }
}

@Preview(showBackground = true, name = "Con error")
@Composable
private fun PreviewLoginError() = ValcarcelComidaTheme {
    LoginScreen(UiState.Error("Correo o contraseña incorrectos")) { _, _ -> }
}