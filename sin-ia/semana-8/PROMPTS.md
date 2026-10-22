# Prompts utilizados

Fase 2 · Mejora asistida por Gemini en Android Studio · Rama `mejora-ia8`

---

## Prompt 1 · Gráfico de peso de la mascota

**Texto enviado a Gemini:**

```
Tengo una app Android en Kotlin con Jetpack Compose, Material 3, MVVM, Room y un
VetRepository (interface). Paquete: com.tecsup.huellitasvvet.

Agrega en la ficha de la mascota un gráfico de línea con el peso registrado en
cada cita ATENDIDA, de la más antigua a la más reciente.

Lo que ya existe:
- Entidad Cita (tabla "citas") con: mascotaId (Int), fecha (String "aaaa-MM-dd"),
  estado (String: PENDIENTE, CONFIRMADA, ATENDIDA, CANCELADA) y
  pesoRegistrado (Double?).
- CitaDao en data/local/dao/CitaDao.kt.
- VetRepository es una INTERFACE en data/repository/VetRepository.kt y su
  implementación es VetRepositoryRoom (recibe VetDatabase y usa db.citaDao()).
- MascotaViewModel(repo: VetRepository, duenoId: Int) en viewmodel/.
- FichaMascotaScreen en ui/dueno/, con una pestaña "Historial clínico".
- DatoGrafico(etiqueta: String, valor: Float) en ui/components/graficos/GraficoDona.kt.
- La función internal fechaCorta(fecha: String) en ui/dueno/MascotasScreen.kt
  convierte "2026-10-12" en "12 oct".

Pasos:
1. Crea data class PuntoPeso(val fecha: String, val peso: Double) en model/.
2. En CitaDao agrega:
   fun observarPesos(mascotaId: Int): Flow<List<PuntoPeso>>
   con una consulta que devuelva fecha y pesoRegistrado (como peso) de las citas
   ATENDIDA de esa mascota con pesoRegistrado no nulo, ordenadas por fecha ascendente.
3. Declara primero en la interface VetRepository:
   fun observarPesos(mascotaId: Int): Flow<List<PuntoPeso>>
   y luego impleméntala en VetRepositoryRoom delegando en el DAO.
4. En MascotaViewModel agrega pesos(mascotaId: Int): StateFlow<List<DatoGrafico>>
   que convierta cada PuntoPeso en DatoGrafico(etiqueta = fecha, valor = peso).
   Guarda cada StateFlow en un mapa para no crear uno nuevo en cada recomposición.
5. Crea GraficoLineas(datos: List<DatoGrafico>, color: Color, modifier: Modifier)
   en ui/components/graficos/GraficoLineas.kt, dibujado solo con Canvas: línea,
   puntos y el valor de cada punto. Si hay menos de 2 datos, muestra
   "Se necesitan al menos 2 atenciones para ver la evolución".
6. En FichaMascotaScreen, en la pestaña "Historial clínico", muestra el gráfico
   dentro de una tarjeta titulada "Evolución del peso", arriba del historial.
   Las etiquetas del eje X se muestran con fechaCorta().

Reglas obligatorias de MVVM:
- La View no usa DAO ni Repository: solo el ViewModel.
- El ViewModel solo conoce la interface VetRepository y no importa nada de Compose.
- Toda función nueva del Repository se declara primero en la interface.
- Lectura que actualiza la pantalla = Flow, sin suspend.
- Usa MaterialTheme.colorScheme, sin colores fijos.
- No uses XML ni librerías externas.
- No se modifica ninguna entidad, así que no hay que subir la version de la base.

Muéstrame solo los archivos que cambian, completos.
```

**Qué generó la IA:** Creó `PuntoPeso` (model) y `GraficoLineas` (Canvas con línea, puntos, valores y fechas). Agregó la consulta `observarPesos` en `CitaDao`, la declaró en la interface `VetRepository`, la implementó en `VetRepositoryRoom`, agregó `pesos()` en `MascotaViewModel` y mostró el gráfico en la pestaña "Historial clínico" de la ficha.

**Qué tuve que corregir yo:** No hubo errores de compilación ni de arquitectura. Detecté un `try/catch` innecesario alrededor de `fechaCorta()` (la fecha siempre viene de la base con el mismo formato) y que el gráfico se muestra aunque la mascota no tenga atenciones, repitiendo el mensaje de "Aún no tiene atenciones".

**¿Respeta MVVM?:** Sí. La View solo usa `MascotaViewModel`; el ViewModel solo conoce la interface `VetRepository` y no importa Compose; la función nueva se declaró primero en la interface; la lectura es un `Flow` sin `suspend`.

---

## Prompt 2 · Reprogramar cita

**Texto enviado a Gemini:**

```
Tengo una app Android en Kotlin con Jetpack Compose, Material 3, MVVM, Room y un
VetRepository (interface). Paquete: com.tecsup.huellitasvvet.

Quiero que el dueño pueda REPROGRAMAR una cita: cambiar su fecha y hora.

Reglas de negocio (van en el ViewModel, no en la pantalla):
- Solo se reprograma una cita PENDIENTE o CONFIRMADA.
- Solo si faltan MÁS de 2 horas para la cita actual
  (Duration.between(LocalDateTime.now(), fechaHoraCita).toMinutes() > 120).
- La nueva fecha va de hoy a 7 días.
- La nueva hora debe estar en Horarios.todos (18 horarios de 09:00 a 17:30).
- Si la nueva fecha es hoy, solo horas futuras.
- El nuevo horario debe estar libre: no puede existir otra cita no CANCELADA
  con la misma fecha y hora. Antes de guardar, vuelve a consultar con
  repo.observarHorasOcupadas(fecha).first().
- La nueva fecha y hora deben ser distintas de las actuales.
- Al reprogramar, la cita vuelve a estado PENDIENTE para que el veterinario la
  confirme de nuevo.

Lo que ya existe:
- Entidad Cita (tabla "citas") con id, mascotaId, servicioId, fecha (String
  "aaaa-MM-dd"), hora (String "HH:mm") y estado (String).
- CitaDao en data/local/dao/CitaDao.kt.
- VetRepository (interface) en data/repository/ con:
  fun observarCita(id: Int): Flow<CitaConDetalle?>
  fun observarHorasOcupadas(fecha: String): Flow<List<String>>
  Su implementación es VetRepositoryRoom (usa db.citaDao()).
- CitaConDetalle tiene: cita (Cita), mascota (MascotaConDueno con .mascota y
  .dueno) y servicio (Servicio).
- model/Horarios.kt: object Horarios { val todos: List<String> }.
- Componentes en ui/components/:
  FechaChip(fecha: LocalDate, seleccionada: Boolean, onClick: () -> Unit, modifier)
  HoraChip(hora: String, seleccionada: Boolean, ocupada: Boolean, onClick: () -> Unit, modifier)
  CitaCard(c: CitaConDetalle, modifier, onCancelar: (() -> Unit)? = null)
  AppTopBar(titulo: String, onAtras: (() -> Unit)?)
  VetButton(texto: String, onClick: () -> Unit, modifier, enabled: Boolean)
  MascotaAvatar(especie: String, modifier, tamano: Dp)
- MisCitasViewModel en viewmodel/ tiene fun puedeCancelar(c: Cita): Boolean
  con la regla de 2 horas.
- MisCitasScreen en ui/dueno/ muestra las próximas citas con CitaCard.
- Rutas en ui/navigation/Rutas.kt y NavVet.kt crea cada ViewModel con
  viewModel(factory = VetViewModelFactory { MiViewModel(app.repository, ...) }).
  En NavVet, "usuario" es el usuario actual y ConUsuario(usuario) { u -> ... }.
- La función internal fechaLegible(fecha: String) en ui/dueno/MascotasScreen.kt
  convierte "2026-10-21" en "Mié 21 oct".

Pasos:
1. En CitaDao agrega:
   suspend fun reprogramar(id: Int, fecha: String, hora: String)
   con un UPDATE que cambie fecha y hora y ponga estado = 'PENDIENTE'.
2. Declara primero en la interface VetRepository:
   suspend fun reprogramarCita(id: Int, fecha: String, hora: String)
   y luego impleméntala en VetRepositoryRoom delegando en el DAO.
3. Crea viewmodel/ReprogramarCitaViewModel.kt (recibe repo: VetRepository y
   citaId: Int) con: cita: StateFlow<CitaConDetalle?>, fechas (hoy a 7 días),
   fecha y hora elegidas como StateFlow, horasNoDisponibles: StateFlow<Set<String>>
   (ocupadas con flatMapLatest + pasadas si es hoy), puedeConfirmar, mensaje,
   reprogramada (Boolean para volver atrás), elegirFecha (limpia la hora),
   elegirHora y confirmar() que aplica TODAS las reglas de arriba.
4. Crea ui/dueno/ReprogramarCitaScreen.kt: tarjeta con la cita actual (mascota,
   servicio, fechaLegible y hora), chips de fecha con FechaChip, grilla de 4
   columnas con HoraChip, el mensaje de error y el botón "Confirmar nueva fecha".
5. En CitaCard agrega un parámetro opcional onReprogramar: (() -> Unit)? = null
   que muestre un TextButton "Reprogramar" junto a "Cancelar".
6. En MisCitasScreen pasa onReprogramar en las próximas citas solo si
   vm.puedeCancelar(c.cita) es true, y agrega el parámetro
   onReprogramar: (Int) -> Unit a MisCitasScreen.
7. En Rutas agrega REPROGRAMAR_CITA = "reprogramar_cita/{id}" y
   fun reprogramarCita(id: Int). En NavVet agrega el destino (sin barra inferior)
   y conecta onReprogramar desde MIS_CITAS. Al reprogramar, vuelve atrás.

Reglas obligatorias de MVVM:
- La View no usa DAO ni Repository: solo su ViewModel.
- El ViewModel solo conoce la interface VetRepository y no importa nada de Compose.
- Toda función nueva del Repository se declara primero en la interface.
- Escritura = suspend; lectura que actualiza la pantalla = Flow.
- Usa MaterialTheme.colorScheme, sin colores fijos.
- No uses XML ni librerías externas.
- No se modifica ninguna entidad, así que no hay que subir la version de la base.

Muéstrame solo los archivos que cambian, completos.
```

**Qué generó la IA:** Agregó `reprogramar()` en `CitaDao`, lo declaró en la interface `VetRepository` y lo implementó en `VetRepositoryRoom`. Creó `ReprogramarCitaViewModel` con todas las reglas (estado, 2 horas, rango de fechas, horario válido, hora futura, horario distinto y una segunda consulta antes de guardar) y `ReprogramarCitaScreen` con `FechaChip` y `HoraChip`. Agregó el botón "Reprogramar" en `CitaCard` y `MisCitasScreen`, y la ruta en `Rutas` y `NavVet`.

**Qué tuve que corregir yo:** No hubo errores de compilación. Al revisar, noté que el código que Gemini mostró en el chat no coincidía del todo con el que escribió en el proyecto (en el destino de Ajustes mostraba `onRecordatorios`, pero el archivo quedó con `onNotificaciones`, que es lo correcto), por eso revisé los cambios reales con `git diff`. También observé que eliminó los comentarios de `NavVet.kt` al reescribirlo y que duplicó la regla de 2 horas en `MisCitasViewModel` y `ReprogramarCitaViewModel`.

**¿Respeta MVVM?:** Sí. Las reglas están en `ReprogramarCitaViewModel`, que solo conoce la interface `VetRepository` y no importa Compose; la pantalla solo observa y avisa; la función nueva se declaró primero en la interface y es `suspend` porque es una escritura.

---

## Prompt 3 · Búsqueda de pacientes

**Texto enviado a Gemini:**

```
Tengo una app Android en Kotlin con Jetpack Compose, Material 3, MVVM, Room y un
VetRepository (interface). Paquete: com.tecsup.huellitasvvet.

La búsqueda de pacientes YA EXISTE y funciona. Quiero MEJORARLA sin romper lo actual.

Lo que ya existe:
- viewmodel/PacientesViewModel.kt (recibe repo: VetRepository) con:
  - texto: StateFlow<String> y especie: StateFlow<Especie?> (null = Todos)
  - pacientes: StateFlow<List<MascotaConDueno>>, calculado con
    combine(repo.observarTodasLasMascotas(), _texto, _especie) que filtra por
    nombre de mascota o de dueño y por especie
  - ultimaVisita: StateFlow<Map<Int, String>>
  - fun buscar(texto: String) y fun filtrarEspecie(especie: Especie?)
- ui/veterinario/PacientesScreen.kt con un OutlinedTextField (placeholder
  "Buscar mascota o dueño..."), chips Todos / Perros / Gatos / Otros (ChipFiltro)
  y una LazyColumn de TarjetaPaciente.
- MascotaConDueno tiene: mascota (Mascota con nombre, especie, raza) y
  dueno (Usuario con nombre).
- model/Especie.kt: enum class Especie { PERRO, GATO, OTRO }.

Mejoras que quiero:
1. En PacientesViewModel:
   - Que el texto busque también por RAZA, además de nombre de mascota y de dueño.
   - Que la búsqueda ignore mayúsculas y TILDES ("siames" debe encontrar "Siamés").
     Normaliza con java.text.Normalizer en una función privada del ViewModel.
   - Expón hayFiltros: StateFlow<Boolean> (true si hay texto o especie elegida).
   - Agrega fun limpiarFiltros() que vacíe el texto y vuelva a "Todos".
   - Mantén el combine de los tres flujos y las funciones que ya existen.
2. En PacientesScreen:
   - Agrega en el buscador un trailingIcon con el ícono Close que aparezca solo
     cuando hay texto y llame a vm.buscar("").
   - Cambia el placeholder a "Buscar por mascota, dueño o raza...".
   - Debajo de los chips muestra "N pacientes encontrados" (o "1 paciente encontrado").
   - Si la lista está vacía y hayFiltros es true, muestra
     "No hay pacientes que coincidan con la búsqueda" y un TextButton
     "Limpiar filtros" que llame a vm.limpiarFiltros().
   - No cambies TarjetaPaciente ni ChipFiltro.

Reglas obligatorias de MVVM:
- Solo cambian PacientesViewModel y PacientesScreen.
- No modifiques el DAO, la interface VetRepository ni VetRepositoryRoom.
- La View no usa DAO ni Repository: solo su ViewModel.
- El ViewModel solo conoce la interface VetRepository y no importa nada de Compose.
- La lógica de búsqueda (normalizar, filtrar, contar) va en el ViewModel, no en la View.
- Usa MaterialTheme.colorScheme, sin colores fijos.
- No uses XML ni librerías externas.

Muéstrame solo los archivos que cambian, completos.
```

**Qué generó la IA:** La búsqueda por nombre de mascota o dueño y el filtro por especie ya existían desde la Fase 1 (Paso 12). Gemini la amplió: busca también por raza, ignora mayúsculas y tildes con `java.text.Normalizer`, agrega `hayFiltros` y `limpiarFiltros()` en el ViewModel, y en la pantalla un botón para borrar el texto, el contador "N pacientes encontrados" y el botón "Limpiar filtros" cuando no hay resultados.

**Qué tuve que corregir yo:** Nada; compiló y funcionó a la primera, y respetó la restricción de no tocar el DAO ni el Repository. Observé que crea la expresión regular de `normalizar()` en cada llamada, cuando podría crearse una sola vez como propiedad privada.

**¿Respeta MVVM?:** Sí. La lógica de búsqueda (normalizar, filtrar y combinar los tres flujos) está en `PacientesViewModel`, que solo conoce la interface `VetRepository`; la pantalla solo muestra los resultados y avisa los eventos.

---

## Reflexión


- **¿Qué mejoró la IA y qué no?** Generó rápido código funcional en las cuatro capas y aplicó bien las reglas de negocio que le describí, pero no mejoró lo que no le pedí: dejó código de más (un `try/catch` innecesario) y repitió la regla de 2 horas en dos ViewModels en lugar de reutilizarla.
- **¿Qué error cometió?** Al reescribir `NavVet.kt` completo eliminó todos los comentarios, y lo que mostró en el chat no coincidía exactamente con lo que escribió en el archivo; por eso hay que revisar siempre los cambios reales con `git diff`.
- **¿Qué aprendí sobre MVVM, Room y reglas de negocio?** Que la IA respeta MVVM cuando el prompt incluye los nombres reales de clases y archivos y las reglas explícitas (la View solo usa su ViewModel, el ViewModel solo la interface, las funciones nuevas primero en la interface). Que las reglas de negocio deben vivir en el ViewModel para poder revisarlas en un solo lugar, y que gracias a Room con `Flow` cada mejora se actualizó en pantalla sin código extra.