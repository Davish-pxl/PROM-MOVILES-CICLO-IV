package com.tecsup.huellitasvvet.ui.dueno

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.tecsup.huellitasvvet.model.Rol
import com.tecsup.huellitasvvet.ui.components.VetButton
import com.tecsup.huellitasvvet.ui.components.VetTextField
import com.tecsup.huellitasvvet.ui.theme.VetTheme
import com.tecsup.huellitasvvet.viewmodel.SesionViewModel

@Composable
fun LoginScreen(
    vm: SesionViewModel,
    onLoginOk: (Rol) -> Unit
) {
    val usuario by vm.usuarioActual.collectAsState()
    val mensajeError by vm.error.collectAsState()
    val oscuro by vm.modoOscuro.collectAsState()

    var rol by rememberSaveable { mutableStateOf(Rol.CLIENTE) }
    var correo by rememberSaveable { mutableStateOf("") }
    var clave by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(usuario) {
        usuario?.let { onLoginOk(Rol.valueOf(it.rol)) }
    }

    VetTheme(rol = rol, oscuro = oscuro) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 40.dp)
            ) {
                Text(
                    "Bienvenido",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Ingresa para continuar",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(28.dp))

                SelectorRol(
                    seleccionado = rol,
                    onSeleccionar = { rol = it; vm.limpiarError() }
                )
                Spacer(Modifier.height(24.dp))

                VetTextField(
                    value = correo,
                    onValueChange = { correo = it; vm.limpiarError() },
                    label = "Correo",
                    keyboardType = KeyboardType.Email,
                    leadingIcon = Icons.Outlined.Email
                )
                Spacer(Modifier.height(12.dp))
                VetTextField(
                    value = clave,
                    onValueChange = { clave = it; vm.limpiarError() },
                    label = "Contraseña",
                    esClave = true,
                    leadingIcon = Icons.Outlined.Lock
                )
                Text(
                    "¿Olvidaste tu contraseña?",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 8.dp)
                )

                mensajeError?.let {
                    Spacer(Modifier.height(12.dp))
                    Text(
                        it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(Modifier.height(24.dp))
                VetButton(texto = "Ingresar", onClick = { vm.login(correo, clave, rol) })
                Spacer(Modifier.height(24.dp))

                if (rol == Rol.CLIENTE) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            "¿No tienes cuenta?  ",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            "Regístrate",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                } else {
                    TarjetaModoPractica()
                }
            }
        }
    }
}

@Composable
private fun SelectorRol(seleccionado: Rol, onSeleccionar: (Rol) -> Unit) {
    val opciones = listOf(Rol.CLIENTE to "Dueño", Rol.ADMIN to "Veterinario")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(4.dp)
    ) {
        opciones.forEach { (r, texto) ->
            val elegido = r == seleccionado
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (elegido) MaterialTheme.colorScheme.primary else Color.Transparent)
                    .clickable { onSeleccionar(r) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    texto,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = if (elegido) FontWeight.Bold else FontWeight.Medium,
                    color = if (elegido) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun TarjetaModoPractica() {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                "Modo práctica",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Puedes cambiar de perfil desde Ajustes para probar ambos roles.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
