package com.tuapp.saludpluscitas.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tuapp.saludpluscitas.R
import com.tuapp.saludpluscitas.ui.theme.*

/**
 * Insignia de ícono circular o de esquinas redondeadas.
 */
@Composable
fun IconBadge(
    icon: ImageVector,
    fondo: Color,
    tint: Color,
    modifier: Modifier = Modifier,
    tamano: Dp = 44.dp,
    shape: Shape = CircleShape,
) {
    Box(
        modifier = modifier
            .size(tamano)
            .clip(shape)
            .background(fondo),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(tamano * 0.5f)
        )
    }
}

/**
 * Botón principal de la aplicación (52 dp de alto, esquinas 14 dp).
 */
@Composable
fun PrimaryButton(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = AzulPrimario,
            contentColor = Color.White
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = texto,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            if (icon != null) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

/**
 * Campo de texto personalizado con OutlinedTextField y 12 dp de esquinas.
 */
@Composable
fun CampoTexto(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    leadingIcon: ImageVector? = null,
    isPassword: Boolean = false,
    enabled: Boolean = true,
) {
    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        enabled = enabled,
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        leadingIcon = if (leadingIcon != null) {
            { Icon(imageVector = leadingIcon, contentDescription = null, tint = TextoSecundario) }
        } else null,
        trailingIcon = if (isPassword) {
            {
                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = "Alternar contraseña", tint = TextoSecundario)
                }
            }
        } else null,
        visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = BordeGris,
            focusedBorderColor = AzulPrimario
        ),
        modifier = modifier.fillMaxWidth()
    )
}

/**
 * Selecciona la imagen del avatar según el prefijo del nombre.
 */
@DrawableRes
fun avatarParaMedico(nombre: String): Int {
    return when {
        nombre.startsWith("Dra.") -> R.drawable.doctoras
        nombre.startsWith("Dr.") -> R.drawable.doctor
        else -> R.drawable.doctoras
    }
}

/**
 * Devuelve el ícono oficial de Material Icons según el nombre de la especialidad.
 */
fun iconoParaEspecialidad(nombre: String): ImageVector {
    return when {
        nombre.contains("General", ignoreCase = true) -> Icons.Filled.MedicalServices
        nombre.contains("Pediatría", ignoreCase = true) -> Icons.Filled.ChildCare
        nombre.contains("Ginecología", ignoreCase = true) -> Icons.Filled.PregnantWoman
        nombre.contains("Cardiología", ignoreCase = true) -> Icons.Filled.Favorite
        nombre.contains("Dermatología", ignoreCase = true) -> Icons.Filled.Spa
        nombre.contains("Traumatología", ignoreCase = true) -> Icons.Filled.Healing
        nombre.contains("Oftalmología", ignoreCase = true) -> Icons.Filled.Visibility
        else -> Icons.Filled.LocalHospital
    }
}

/**
 * Tarjeta reusable de Médico para listados.
 */
@Composable
fun TarjetaMedico(
    nombre: String,
    especialidad: String,
    modifier: Modifier = Modifier,
    rating: String = "4.9 (120)",
    disponibilidad: String = "Disponible hoy",
    onClick: () -> Unit = {},
) {
    val esDisponibleHoy = disponibilidad.contains("hoy", ignoreCase = true)
    val colorPillFondo = if (esDisponibleHoy) VerdeDisponibleFondo else AmbarAvisoFondo
    val colorPillTexto = if (esDisponibleHoy) VerdeDisponible else AmbarAviso

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
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
            Image(
                painter = painterResource(id = avatarParaMedico(nombre)),
                contentDescription = "Foto de $nombre",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextoPrincipal
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = especialidad,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextoSecundario
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = AmbarAviso,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = rating,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = TextoSecundario
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    // Pill Badge de Disponibilidad
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = colorPillFondo
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(colorPillTexto)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = disponibilidad,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = colorPillTexto
                            )
                        }
                    }
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = TextoSecundario
            )
        }
    }
}
