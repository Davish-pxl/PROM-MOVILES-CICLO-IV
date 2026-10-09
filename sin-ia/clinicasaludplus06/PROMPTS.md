# Registro de Prompts y Registro de Cambios — Fase 2: Mejora con IA

---

## 📌 Prompt 1: Generación Inicial de Interfaz y Calendario Dinámico

### 📝 Prompt Usado
> "Actúa como desarrollador Senior de Android nativo (Kotlin, Jetpack Compose, Material Design 3, java.time). Rediseña la app de Clínica SaludPlus con sistema de diseño personalizado, tarjetas de accesos rápidos, especialidades y un calendario dinámico en FechaHoraScreen.kt usando java.time.LocalDate con 5 días hábiles, navegación semanal, bloqueo de horas ocupadas y envío de fecha en español."

### 🤖 Respuesta Resumida
La IA generó el código inicial para `HomeScreen.kt` y `FechaHoraScreen.kt`, incluyendo los componentes `IconBadge`, las tarjetas en cuadrícula 2x2, la lógica de la función `diasHabiles` y la consulta reactiva a `Repositorio.obtenerCitas()`.

### 🛠️ Qué tuve que corregir
- Faltaban los imports de la librería extendida de íconos vectoriales (`androidx.compose.material.icons.filled.*`).
- No se estaban cargando correctamente las imágenes locales `.png` de la carpeta `res/drawable`.
- La pantalla principal mostraba banners promocionales no requeridos en la interfaz limpia del usuario.

---

## 📌 Prompt 2: Integración de Recursos Locales PNG y Corrección Visual

### 📝 Prompt Usado
> "Actúa como desarrollador Senior de Android Nativo. Las imágenes locales de drawable y los íconos vectoriales no se están renderizando en HomeScreen.kt ni en FechaHoraScreen.kt. Reescribe el código incluyendo explícitamente painterResource, Image, e imports completos. Renderiza las imágenes PNG doctor.png y doctoras.png con recorte circular CircleShape."

### 🤖 Respuesta Resumida
La IA añadió la función auxiliar `avatarParaMedico` y colocó imágenes en el encabezado de `HomeScreen.kt` e ilustraciones en banners promocionales.

### 🛠️ Qué tuve que corregir
- Se generaron subcarpetas indebidas al copiar los archivos PNG en `res/drawable`, lo que causó un error de compilación. Tuve que mover los archivos PNG directamente a la raíz de la carpeta `res/drawable` en formato plano.
- La IA añadió elementos adicionales como un banner promocional ("Atención Médica Digital") y un avatar PNG dentro de `HomeScreen.kt` que no correspondían a la pantalla principal.

---

## 📌 Prompt 3: Prompt Maestro Definitivo y Ajustes Finales de Interfaz

### 📝 Prompt Usado
```text
ROL Y OBJETIVO:
Actúa como desarrollador Senior de Android nativo (Kotlin, Jetpack Compose, Material Design 3, java.time). Vas a rediseñar COMPLETAMENTE la app del paciente de "Clínica SaludPlus" para que se vea lo más fiel posible a la imagen de referencia y con acabado profesional. Entrega código compilable, sin placeholders ni TODO.

══════════════════════════════════════════
1. SISTEMA DE DISEÑO Y COMPONENTES REUTILIZABLES
══════════════════════════════════════════
- Colores principales: Primario azul #1E63F0, Fondo blanco, Superficie suave #F5F7FB, Texto principal #1A1A2E, secundario #6B7280.
- Estados: Disponible verde #22C55E sobre #E8F8EE, Aviso/mañana ámbar #F59E0B sobre #FEF3C7.
- Tarjetas de acceso rápido: azul #DCEBFF, verde #D9F5E5, lila #EADFFB, naranja #FFE9D6.
- Esquinas: tarjetas 16 dp, botones 14 dp, campos 12 dp.
- PROHIBIDO usar emojis. Todo símbolo gráfico debe ser un ImageVector vectorial de Material Icons (usar androidx.compose.material:material-icons-extended).
- Componentes obligatorios:
  · IconBadge(icon: ImageVector, fondo: Color, tint: Color, tamaño: Dp)
  · PrimaryButton(texto, icon opcional, onClick, enabled)
  · avatarParaMedico(nombre: String): Int -> R.drawable.doctoras ("Dra.") o R.drawable.doctor ("Dr.").

══════════════════════════════════════════
2. IMPORTS, ÍCONOS Y PANTALLA PRINCIPAL (HomeScreen.kt)
══════════════════════════════════════════
Asegúrate de incluir EXPLÍCITAMENTE los siguientes imports en la parte superior de HomeScreen.kt y FechaHoraScreen.kt:
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.Icon
import androidx.compose.foundation.Image

A) NORMAS Y RESTRICCIONES VISUALES EN HOMESCREEN.KT:
   - TOPBAR: Incluir el ícono de Menú Hamburguesa (Icons.Default.Menu) en el lado izquierdo solo como elemento visual decorativo, junto a las notificaciones a la derecha.
   - SIN IMÁGENES PNG NI BANNERS: Queda strictly PROHIBIDO colocar banners promocionales o imágenes PNG en el cuerpo o encabezado de HomeScreen.kt. Todo el diseño de esta pantalla se compone de tarjetas limpias, textos y vectores.

B) ÍCONOS VECTORIALES EN HOMESCREEN.KT:
   - Tarjetas de accesos rápidos usando IconBadge:
     · Agendar citas -> Icon(Icons.Filled.CalendarMonth, contentDescription = null)
     · Mis citas -> Icon(Icons.Filled.EventAvailable, contentDescription = null)
     · Mis datos / Perfil -> Icon(Icons.Filled.Person, contentDescription = null)
     · Resultados -> Icon(Icons.Filled.Science, contentDescription = null)
   - Especialidades destacadas:
     · Medicina General -> Icons.Filled.MedicalServices
     · Pediatría -> Icons.Filled.ChildCare
     · Ginecología -> Icons.Filled.PregnantWoman
     · Cardiología -> Icons.Filled.Favorite

══════════════════════════════════════════
3. REQUERIMIENTOS PANTALLA 6 (FechaHoraScreen.kt - CALENDARIO DINÁMICO Y PNG)
══════════════════════════════════════════
- Encabezado y Uso de PNG: Tarjeta resumen del médico cargando dinámicamente su foto PNG con painterResource(id = avatarParaMedico(medico.nombre)) (usando R.drawable.doctoras o R.drawable.doctor), recorte CircleShape, mes/año dinámico en español (ej. "Octubre 2026") usando DateTimeFormatter.ofPattern("MMMM yyyy", Locale("es", "PE")).
- Navegación por semanas: `<` (ChevronLeft) y `>` (ChevronRight). Deshabilitar `<` si offsetSemanas == 0.
- Lógica de días hábiles: Función pura diasHabiles(desde: LocalDate, cantidad: Int = 5): List<LocalDate> usando LocalDate.now(), omitiendo sábados y domingos. Siempre exactamente 5 tarjetas.
- Grilla de horarios (3 columnas): "08:00 AM" a "05:00 PM". Consultar Repositorio.obtenerCitas() filtrando por medicoId y fecha ("dd/MM/yyyy") con derivedStateOf. Si está ocupado: Icons.Filled.Block + "(Ocupado)", alpha 0.4 y sin clic.
- Reinicio de estado: Al tocar un día distinto o cambiar de semana, horaSeleccionada = null de inmediato.
- Botón "Continuar" deshabilitado si horaSeleccionada == null. Al continuar, enviar fecha en patrón "EEEE d 'de' MMMM yyyy" con Uri.encode().

══════════════════════════════════════════
4. RESTRICCIONES DE ARQUITECTURA
══════════════════════════════════════════
- NO modificar nada del paquete data.model (Cita.kt, Usuario.kt, Medico.kt, Especialidad.kt) ni las firmas de Repositorio.kt.
- Entregar el código completo compilable con todos sus imports sin omitir ningún bloque.
