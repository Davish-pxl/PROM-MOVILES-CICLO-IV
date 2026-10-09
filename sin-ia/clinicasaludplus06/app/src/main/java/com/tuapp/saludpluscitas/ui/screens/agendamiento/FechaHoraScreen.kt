package com.tuapp.saludpluscitas.ui.screens.agendamiento

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.tuapp.saludpluscitas.data.repository.Repositorio
import com.tuapp.saludpluscitas.navigation.Rutas
import com.tuapp.saludpluscitas.ui.components.PrimaryButton
import com.tuapp.saludpluscitas.ui.components.avatarParaMedico
import com.tuapp.saludpluscitas.ui.theme.*
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.Locale

// Constantes
private const val MAX_SEMANAS = 8L
private val HORARIOS_BASE = listOf(
    "08:00 AM", "09:00 AM", "10:00 AM", "11:00 AM",
    "02:00 PM", "03:00 PM", "04:00 PM", "05:00 PM",
)

// Saver para LocalDate
private val LocalDateSaver = Saver<LocalDate, String>(
    save = { it.toString() },
    restore = { LocalDate.parse(it) },
)

// ══════════════════════════════════════════
// LÓGICA DE FECHAS: FUNCIONES PURAS Y TESTEABLES
// ══════════════════════════════════════════

/**
 * Calcula [cantidad] días laborales consecutivos (lunes a viernes) a partir de [desde].
 * Excluye sábados y domingos. Si [desde] es fin de semana, avanza al siguiente lunes.
 */
fun diasHabiles(desde: LocalDate, cantidad: Int = 5): List<LocalDate> {
    var actual = desde
    while ((actual.dayOfWeek == DayOfWeek.SATURDAY) || (actual.dayOfWeek == DayOfWeek.SUNDAY)) {
        actual = actual.plusDays(1)
    }
    val resultado = mutableListOf<LocalDate>()
    while (resultado.size < cantidad) {
        if ((actual.dayOfWeek != DayOfWeek.SATURDAY) && (actual.dayOfWeek != DayOfWeek.SUNDAY)) {
            resultado.add(actual)
        }
        actual = actual.plusDays(1)
    }
    return resultado
}

/**
 * Calcula la fecha base para una semana dada un [offsetSemanas].
 * Para offset 0, si hoy es día hábil retorna hoy; si es fin de semana retorna el siguiente lunes.
 * Para offset > 0, retorna el lunes correspondiente a ese offset semanal.
 */
fun obtenerFechaBase(hoy: LocalDate, offsetSemanas: Long): LocalDate {
    val primerDia = if (hoy.dayOfWeek == DayOfWeek.SATURDAY || hoy.dayOfWeek == DayOfWeek.SUNDAY) {
        hoy.with(TemporalAdjusters.next(DayOfWeek.MONDAY))
    } else {
        hoy
    }
    return if (offsetSemanas == 0L) {
        primerDia
    } else {
        primerDia.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).plusWeeks(offsetSemanas)
    }
}

/**
 * Formatea el encabezado de mes y año en español (PE), p. ej. "Octubre 2026".
 */
fun obtenerTextoMesAnio(fecha: LocalDate): String {
    val formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.forLanguageTag("es-PE"))
    return fecha.format(formatter).replaceFirstChar { it.uppercase() }
}

/**
 * Formatea la abreviatura del día, p. ej. "Lun", "Mar", "Mié", "Jue", "Vie".
 */
fun obtenerNombreDiaAbreviado(fecha: LocalDate): String {
    val raw = fecha.format(DateTimeFormatter.ofPattern("EEE", Locale.forLanguageTag("es-PE")))
    return raw.replace(".", "").replaceFirstChar { it.uppercase() }
}

/**
 * Formatea la fecha para consultar en el repositorio (patrón dd/MM/yyyy).
 */
fun obtenerFechaFormatoRepo(fecha: LocalDate): String {
    return fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
}

/**
 * Formatea la fecha extendida enviada a ConfirmarCitaScreen (patrón "EEEE d 'de' MMMM yyyy").
 * Ejemplo: "Jueves 8 de octubre 2026"
 */
fun obtenerFechaTextoExtendido(fecha: LocalDate): String {
    val formatter = DateTimeFormatter.ofPattern("EEEE d 'de' MMMM yyyy", Locale.forLanguageTag("es-PE"))
    return fecha.format(formatter).replaceFirstChar { it.uppercase() }
}

// ══════════════════════════════════════════
// COMPOSABLE PRINCIPAL
// ══════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FechaHoraScreen(
    navController: NavController,
    medicoId: Int,
) {
    val medico = remember(medicoId) {
        Repositorio.obtenerMedicos().find { it.id == medicoId }
    }
    val especialidad = remember(medico) {
        Repositorio.obtenerEspecialidades().find { it.id == medico?.especialidadId }
    }

    val hoy = remember { LocalDate.now() }
    var offsetSemanas by rememberSaveable { mutableLongStateOf(0L) }

    val diasVisibles = remember(hoy, offsetSemanas) {
        diasHabiles(obtenerFechaBase(hoy, offsetSemanas))
    }

    var diaSeleccionado by rememberSaveable(stateSaver = LocalDateSaver) {
        mutableStateOf(diasVisibles.first())
    }
    var horaSeleccionada by rememberSaveable { mutableStateOf<String?>(null) }

    // Al cambiar de semana: auto-seleccionar el primer día y reiniciar hora seleccionada
    LaunchedEffect(offsetSemanas) {
        val nuevosDias = diasHabiles(obtenerFechaBase(hoy, offsetSemanas))
        if (nuevosDias.isNotEmpty() && diaSeleccionado !in nuevosDias) {
            diaSeleccionado = nuevosDias.first()
            horaSeleccionada = null
        }
    }

    val mesAnioTexto = remember(diasVisibles) {
        obtenerTextoMesAnio(diasVisibles.first())
    }

    val fechaRepo = remember(diaSeleccionado) {
        obtenerFechaFormatoRepo(diaSeleccionado)
    }
    val fechaExtendida = remember(diaSeleccionado) {
        obtenerFechaTextoExtendido(diaSeleccionado)
    }

    // Horarios ocupados reactivos
    val horasOcupadasState by remember(medicoId, diaSeleccionado) {
        derivedStateOf {
            Repositorio.obtenerCitas()
                .filter { cita ->
                    cita.medicoId == medicoId &&
                            (cita.fecha == fechaRepo || cita.fecha == fechaExtendida || cita.fecha.replace("-", "/") == fechaRepo)
                }
                .map { it.hora }
                .toSet()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Seleccionar fecha y hora",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        color = TextoPrincipal,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar",
                            tint = TextoPrincipal,
                        )
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    PrimaryButton(
                        texto = "Continuar",
                        icon = Icons.AutoMirrored.Filled.ArrowForward,
                        enabled = horaSeleccionada != null,
                        onClick = {
                            if (horaSeleccionada != null) {
                                val fechaEncoded = Uri.encode(fechaExtendida)
                                val horaEncoded = Uri.encode(horaSeleccionada)
                                navController.navigate("${Rutas.ConfirmarCita.ruta}/$medicoId/$fechaEncoded/$horaEncoded")
                            }
                        },
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // 1. Tarjeta resumen del médico (Avatar con R.drawable.doctoras / doctor)
            Card(
                modifier = Modifier.fillMaxWidth(),
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
                    val nombreMedico = medico?.nombre ?: "Dra. Ana Torres"
                    Image(
                        painter = painterResource(id = avatarParaMedico(nombreMedico)),
                        contentDescription = "Foto de $nombreMedico",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = nombreMedico,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextoPrincipal
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = especialidad?.nombre ?: "Ginecóloga",
                            fontSize = 14.sp,
                            color = TextoSecundario
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 2. Encabezado del calendario (Navegación semanal con CalendarMonth)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        if (offsetSemanas > 0) {
                            offsetSemanas--
                        }
                    },
                    enabled = offsetSemanas > 0
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Semana anterior",
                        tint = if (offsetSemanas > 0) TextoPrincipal else TextoSecundario.copy(alpha = 0.38f)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.CalendarMonth,
                        contentDescription = null,
                        tint = AzulPrimario,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = mesAnioTexto,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextoPrincipal
                    )
                }

                IconButton(
                    onClick = {
                        if (offsetSemanas < MAX_SEMANAS) {
                            offsetSemanas++
                        }
                    },
                    enabled = offsetSemanas < MAX_SEMANAS
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Semana siguiente",
                        tint = if (offsetSemanas < MAX_SEMANAS) TextoPrincipal else TextoSecundario.copy(alpha = 0.38f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 3. Fila de 5 días (Lun-Vie)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                diasVisibles.forEach { dia ->
                    val esSeleccionado = (dia == diaSeleccionado)

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                diaSeleccionado = dia
                                horaSeleccionada = null
                            },
                        shape = RoundedCornerShape(12.dp),
                        color = if (esSeleccionado) {
                            AzulPrimario
                        } else {
                            SuperficieSuave
                        },
                        contentColor = if (esSeleccionado) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            TextoPrincipal
                        }
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = obtenerNombreDiaAbreviado(dia),
                                fontSize = 12.sp,
                                color = if (esSeleccionado) {
                                    MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                                } else {
                                    TextoSecundario
                                }
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = dia.dayOfMonth.toString(),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (esSeleccionado) {
                                    MaterialTheme.colorScheme.onPrimary
                                } else {
                                    TextoPrincipal
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 4. Grilla de horarios (3 columnas)
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(HORARIOS_BASE) { hora ->
                    val estaOcupada = hora in horasOcupadasState
                    val esSeleccionada = (hora == horaSeleccionada)

                    val backgroundColor = when {
                        esSeleccionada -> AzulPrimario
                        estaOcupada -> SuperficieSuave.copy(alpha = 0.5f)
                        else -> SuperficieSuave
                    }

                    val borderColor = when {
                        esSeleccionada -> AzulPrimario
                        estaOcupada -> BordeGris.copy(alpha = 0.4f)
                        else -> BordeGris
                    }

                    Box(
                        modifier = Modifier
                            .height(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(backgroundColor)
                            .border(
                                width = 1.dp,
                                color = borderColor,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .then(
                                if (estaOcupada) {
                                    Modifier.alpha(0.4f)
                                } else {
                                    Modifier.clickable {
                                        horaSeleccionada = hora
                                    }
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        ) {
                            Icon(
                                imageVector = if (estaOcupada) Icons.Filled.Block else Icons.Outlined.AccessTime,
                                contentDescription = null,
                                tint = when {
                                    esSeleccionada -> MaterialTheme.colorScheme.onPrimary
                                    estaOcupada -> MaterialTheme.colorScheme.error
                                    else -> TextoSecundario
                                },
                                modifier = Modifier.size(14.dp)
                            )

                            Spacer(modifier = Modifier.width(4.dp))

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = hora,
                                    fontSize = 12.sp,
                                    fontWeight = if (esSeleccionada) FontWeight.Bold else FontWeight.Medium,
                                    color = when {
                                        esSeleccionada -> MaterialTheme.colorScheme.onPrimary
                                        estaOcupada -> TextoSecundario
                                        else -> TextoPrincipal
                                    }
                                )
                                if (estaOcupada) {
                                    Text(
                                        text = "(Ocupado)",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Normal,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FechaHoraScreenPreview() {
    MaterialTheme {
        FechaHoraScreen(
            navController = rememberNavController(),
            medicoId = 6
        )
    }
}
