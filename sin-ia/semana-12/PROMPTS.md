# Prompts utilizados

Fase 2 · Mejora  · Rama `mejora-ia12`

Trabajé con la IA en una sola conversación, así que cada mejora no fue un único mensaje largo.

### Contexto común a todos los prompts

```
- Guía: "Laboratorio 11-12 · Compose + MVVM + Retrofit + Room + Firebase Auth"
  (Programación en Móviles, Tecsup), subida en PDF al inicio de la conversación.
- Tema: Comidas (TheMealDB). Variante: categoría Chicken.
  Lista:   https://www.themealdb.com/api/json/v1/1/filter.php?c=Chicken
  Detalle: https://www.themealdb.com/api/json/v1/1/lookup.php?i={id}
- Paquete: com.tuapp.valcarcelcomida · Tema de Compose: ValcarcelComidaTheme.
- Proyecto con AGP 9 y Kotlin integrado (KSP 2.3.x), Room 2.8.5, Retrofit 2.11,
  Navigation Compose, Coil, Firebase Auth + Firestore.
- Fase 1 terminada en main (H1 a H16): login con roles, lista, detalle, favoritos,
  modo sin conexión, panel Admin y notificación local.
- Estructura de la guía (sección 5): data/remote, data/local, data/auth,
  data/repository, data/mapper, domain/model, notification, ui/...
- Reglas MVVM de la guía (se revisan en la rúbrica):
  * Las pantallas solo observan el estado con collectAsStateWithLifecycle()
    y llaman funciones del ViewModel.
  * Prohibido usar Retrofit, Room o Firebase dentro de un composable.
  * El ViewModel no recibe Context; la notificación va por la interface Notificador.
  * El Repository es una interface y el ViewModel depende de ella.
  * Tres modelos: ItemDto (API), ItemEntity (Room) e Item (UI).
  * Room es la fuente única de verdad.
  * Cada ViewModel expone un StateFlow<UiState<T>>.
  * Los ViewModel se crean con un ViewModelProvider.Factory que recibe el AppContainer.
- Mis reglas para la Fase 2:
  * Trabajo individual (no en pareja).
  * No crear archivos nuevos: todo dentro de los archivos de la estructura.
  * Un commit por mejora, con el formato tipo: descripción.
  * Código hito por hito, con los archivos completos para reemplazar.
```

| # | Mejora | Commit |
|---|---|---|
| 1 | Pruebas unitarias del ListaViewModel | `test: pruebas unitarias del ListaViewModel` |
| 2 | Nuevo diseño de la app | `feat: nuevo diseño de la app` |
| 3 | Comidas y recetas en español | `feat: comidas y recetas en español` |
| 4 | Selector de categorías y buscador | `feat: selector de categorias y buscador` |
| 5 | Deshacer al quitar un favorito | `feat: deshacer al quitar un favorito` |
| 6 | Errores en ventana emergente | `fix: errores en ventana emergente` |

---

## Prompt 1 · Pruebas unitarias del ListaViewModel


```
Contexto que la IA ya tenía:
- Requisito R12 de la guía: "Pruebas unitarias del ViewModel de la lista con un
  repositorio falso (mínimo 3 pruebas)".
- Rúbrica, Fase 2 con IA: "3+ commits en mejora-ia, PROMPTS.md con reflexión y
  3 pruebas unitarias".
- ListaViewModel(repo: ItemRepository, auth: AuthRepository, notificador: Notificador)
  con estado: StateFlow<UiState<List<Item>>>, aviso: StateFlow<String?>,
  esAdmin: StateFlow<Boolean>, refrescar() y alternarFavorito(id).
- ItemRepository, AuthRepository y Notificador son interfaces.

Mensajes que envié:
"Empecemos la Fase 2. Asimismo quiero llamar a esa rama mejora-ia12."
"No hay ListaViewModelTest.kt en app/src/test/java/com/tuapp/valcarcelcomida/ui/lista."
"Pero esto no es una mejora que tenemos que implementar de las 3 que faltan,
asimismo también creo que las mejoras deben ser en la aplicación, ¿o estoy mal?"

Decisiones que tomé:
- La rama se llama mejora-ia12 (la guía dice mejora-ia).
- Las pruebas cuentan como un commit de la Fase 2, pero además quiero mejoras
  visibles en la aplicación.
```

**Qué generó la IA:** Explicó cómo crear la rama `mejora-ia12` desde `main`, agregar la dependencia `kotlinx-coroutines-test` y creó `ListaViewModelTest.kt` en `app/src/test` con tres dobles de prueba dentro del mismo archivo: `FakeItemRepository`, `FakeAuthRepository` y `FakeNotificador`. Las 6 pruebas iniciales cubren: éxito con notificación, error sin datos guardados, aviso sin conexión con datos guardados, estado vacío, alternar favorito y rol admin/usuario. Usó `Dispatchers.setMain(UnconfinedTestDispatcher())` porque `viewModelScope` necesita `Dispatchers.Main`, que no existe en una prueba de JVM. En las mejoras 4 y 6 actualizó el repositorio falso y agregó 2 pruebas más.

**Qué tuve que corregir yo:** No encontraba dónde poner el archivo: había que crear el paquete `ui.lista` dentro de `app/src/test` (no en `androidTest`). Además, `./gradlew testDebugUnitTest` solo decía `BUILD SUCCESSFUL` sin mostrar cuántas pruebas corrieron; lo comprobé con `--rerun` y el reporte `app/build/reports/tests/testDebugUnitTest/index.html`: **8 tests, 0 failures**. También olvidé hacer el commit de las pruebas a tiempo y lo hice por separado para no mezclarlo con otro.

**¿Respeta MVVM?:** Sí, y lo demuestra. El `ListaViewModel` recibe las interfaces `ItemRepository`, `AuthRepository` y `Notificador`, así que en la prueba se le pasan versiones falsas sin internet, Room ni Firebase. Si dependiera de `ItemRepositoryImpl`, no se podría probar así.

---

## Prompt 2 · Nuevo diseño de la app


```
Contexto que la IA ya tenía:
- Pantallas de la Fase 1: LoginScreen, ListaScreen (LazyColumn con filas),
  DetalleScreen, FavoritosScreen y AdminScreen, todas "tontas": reciben el
  estado y avisan los clics.
- Tema generado por Android Studio en ui/theme (Color.kt, Theme.kt, Type.kt)
  con colores por defecto y dynamic color.
- Componentes en ui/components: Cargando.kt y ErrorView.kt.

Mensajes que envié (al elegir la mejora de interfaz):
"Toda la interfaz en general: quiero mejorar el diseño de la app, también que las
comidas estén en español y no en inglés, asimismo también sus detalles, también
que esté un selector de categorías, buscar por nombre en la lista, snackbar con
deshacer al quitar un favorito."

Decisiones que tomé:
- Dividir todo en un commit por mejora, empezando por el diseño.
- No crear archivos nuevos (respuesta: "No, mete todo dentro de los archivos
  existentes").
- Cuando la IA propuso agregar más información al Panel Admin (datos de sesión,
  recetas por categoría, favoritos, porcentaje), respondí:
  "Dejaré el panel admin así."
```

**Qué generó la IA:** Una paleta propia "cocina" (pimentón, crema y oliva) en `Color.kt` y `Theme.kt` para modo claro y oscuro, bordes redondeados con `Shapes` y el *dynamic color* desactivado para que Android 12+ no reemplace los colores. Cambió la lista a una grilla de dos columnas (`LazyVerticalGrid`) con tarjetas de foto grande y el corazón sobre la imagen; el detalle con foto de borde a borde, etiquetas de categoría y país, y la preparación en pasos numerados; el login con logo y tarjeta; los totales del panel con colores; y `Cargando` con un texto debajo.

**Qué tuve que corregir yo:** No hubo errores de compilación. Observé que `GrillaComidas` y `MensajeVacio` quedaron dentro de `ListaScreen.kt` y `FavoritosScreen` los importa desde ahí: evita copiar código, pero hace que una pantalla dependa del archivo de otra. Sin la regla de no crear archivos, irían mejor en `ui/components/`.

**¿Respeta MVVM?:** Sí. Ningún ViewModel cambió; solo cambió cómo se dibuja el estado. Las pantallas siguen recibiendo el estado y avisando los clics.

---

## Prompt 3 · Comidas y recetas en español


```
Contexto que la IA ya tenía:
- TheMealDB devuelve todo en inglés: strMeal, strCategory, strArea,
  strInstructions y strIngredient1 a strIngredient20.
- ItemRepositoryImpl(api, dao) con refrescar() (Retrofit -> Room) y
  obtenerDetalle(id) (lookup.php).
- Mappers.kt con ItemDto.aEntity(), ItemEntity.aModelo() e ItemDetalleDto.aModelo().
- La interface Notificador como ejemplo de cómo separar una dependencia de Android.

Mensajes que envié:
"También que las comidas estén en español y no en inglés, asimismo también
sus detalles."
"¿Qué API usamos, dónde está ubicada y de dónde la sacaste?"

Decisiones que tomé (opciones que me dio la IA):
- "¿Cómo traducimos al español?" -> "ML Kit: todo en español (descarga ~30 MB
  la 1ra vez)", en lugar de traducir solo las categorías y dejar las recetas
  en inglés.
- "En la Fase 2, ¿puedo crear archivos nuevos?" -> "No, mete todo dentro de los
  archivos existentes."
```

**Qué generó la IA:** Me avisó que TheMealDB solo tiene datos en inglés. Creó la interface `Traductor` en `ItemRepository.kt` (igual que `Notificador`) y su implementación `TraductorMlKit` al final de `ItemRepositoryImpl.kt`. El nombre se traduce al sincronizar y **se guarda ya en español en Room**; ingredientes y preparación se traducen al abrir el detalle, línea por línea para no perder los pasos. Categoría y país se traducen con tablas fijas en `Mappers.kt`, porque traducir una palabra suelta da errores ("Indian" puede salir "Indio"). Un `Mutex` evita descargar el modelo dos veces y, si la traducción falla, se muestra el texto original. A mi pregunta sobre la API respondió que TheMealDB es la asignada al tema Comidas en la sección 3 de la guía, que la URL base está en `RetrofitClient` y los endpoints en `ApiService`.

**Qué tuve que corregir yo:** Al compilar salieron 31 errores: `Unresolved reference 'mlkit'` y `Expecting ')'` en la línea 42. Eran dos causas: no había sincronizado la dependencia `com.google.mlkit:translate` y al pegar el archivo se perdió un paréntesis. Sincronicé y volví a pegar el archivo completo. Observé que en cada sincronización se vuelven a traducir todos los nombres, aunque ya estuvieran traducidos en Room, y que algunos nombres propios quedan raros ("Chicken Handi" → "Pollo Handi").

**¿Respeta MVVM?:** Sí. La traducción vive en la capa de datos detrás de una interface; el repositorio no sabe que es ML Kit y los ViewModel y pantallas no cambiaron. El `Context` no llega a ningún ViewModel.

---

## Prompt 4 · Selector de categorías y buscador


```
Contexto que la IA ya tenía:
- La variante del laboratorio es solo Chicken; ApiService.listar() recibe la
  categoría en el parámetro c.
- ItemEntity (tabla items) con id, titulo, subtitulo, imagenUrl y esFavorito.
- ItemDao con todos(), favoritos(), idsFavoritos(), guardarTodos() (@Upsert)...
- ListaViewModel observa repo.items y usa refrescoTerminado para no mostrar
  "lista vacía" antes de que responda la API.
- Room es la fuente única de verdad y los favoritos deben sobrevivir al sincronizar.

Mensajes que envié:
"También que esté un selector de categorías, buscar por nombre en la lista."
"Ahora vamos por este: feat: selector de categorias y buscador."
"¿Por qué ahora tenemos más categorías si antes éramos solo pollo?"
"No, está bien."

Decisiones que tomé:
- Mantener el selector: en main (Fase 1) sigue siendo solo Chicken y en
  mejora-ia12 es una mejora; la app arranca en Pollo.
- Hacer el commit de las pruebas por separado, porque me había faltado.
```

**Qué generó la IA:** Agregó la columna `categoria` en `ItemEntity`, la consulta `porCategoria` en `ItemDao` y subió `AppDatabase` a `version = 2` con `fallbackToDestructiveMigration`. La categoría elegida vive en el repositorio como `StateFlow` y `items` usa `flatMapLatest` para escuchar la consulta de la nueva categoría. El modelo `Categoria` quedó dentro de `Item.kt` y la lista de categorías en `ApiService`. El buscador filtra en el ViewModel con `combine(repo.items, busqueda)` e ignora mayúsculas y tildes con `java.text.Normalizer`. En la pantalla agregó el campo de búsqueda con botón para borrar y los chips de categorías.

**Qué tuve que corregir yo:** No hubo errores de compilación. Observé que al subir la versión de la base la tabla se borra y **los favoritos guardados se pierden una vez** (en una app real se usaría una migración), y que `normalizar()` crea la expresión regular en cada llamada, cuando podría crearse una sola vez como propiedad privada.

**¿Respeta MVVM?:** Sí. La lógica de búsqueda (normalizar y filtrar) está en `ListaViewModel`, que solo conoce la interface `ItemRepository`; las funciones nuevas se declararon primero en la interface. La pantalla solo muestra el estado y avisa con `onBuscar` y `onCategoriaClick`. Room sigue siendo la fuente única de verdad.

---

## Prompt 5 · Deshacer al quitar un favorito

```
Contexto que la IA ya tenía:
- El corazón de favorito está en tres pantallas: ListaScreen, FavoritosScreen
  y DetalleScreen.
- ListaViewModel, FavoritosViewModel y DetalleViewModel tienen
  alternarFavorito(), que cambia esFavorito en Room (no borra la fila).

Mensajes que envié:
"Snackbar con deshacer al quitar un favorito."
"¿Sobre qué acciones harían los dos commits 5 y 6 en la aplicación?"
"Hacemos los dos."
"Pásame los archivos del commit 5 para colocarlos."

Decisiones que tomé:
- Hacer el commit 5 (deshacer) y el 6 (errores) por separado, cada uno con
  sus propios archivos.
```

**Qué generó la IA:** Al quitar un favorito en la lista, en Favoritos o en el detalle aparece "Quitaste «…» de favoritos" con el botón **Deshacer**, usando `SnackbarHostState` y `rememberCoroutineScope`. Deshacer vuelve a llamar a `alternarFavorito`, por eso ningún ViewModel cambió. La función `avisarFavoritoQuitado` quedó en `ListaScreen.kt` y las otras pantallas la importan. Antes de programar, me explicó qué vería la persona en la app con cada uno de los dos commits.

**Qué tuve que corregir yo:** La IA me entregó primero los archivos de la mejora 6, que ya traían este cambio mezclado. Le pedí una versión solo de esta mejora para que cada commit tuviera un solo cambio. También observé que el bloque que decide cuándo mostrar el aviso se repite en las tres pantallas.

**¿Respeta MVVM?:** Sí. El Snackbar es una reacción visual al clic, como abrir un diálogo; la pantalla no toca Room ni la API, solo llama a la función del ViewModel.

---

## Prompt 6 · Errores en ventana emergente

```
Contexto que la IA ya tenía:
- UiState.Error(mensaje) y ErrorView(mensaje, onReintentar), que ocupaba toda
  la pantalla con un texto y un botón.
- ListaViewModel y DetalleViewModel tenían cada uno su propia función mensajeDe(e)
  para IOException y HttpException.
- El login muestra sus errores debajo de los campos.

Mensajes que envié (al elegir la mejora de errores):
"¿Se podrían mostrar en una pestaña emergente flotante con dos botones?
Con un mensaje más claro."

Decisiones que tomé:
- Confirmé el comportamiento que me describió la IA: ventana con título,
  mensaje claro y dos botones (Reintentar y Cerrar o Volver); la barra de
  "sin conexión con datos guardados" y los errores del login se quedan igual.
```

**Qué generó la IA:** Agregó `titulo` y `emoji` a `UiState.Error`, con valores por defecto para no romper el login, y la función `errorDe(e)` en `UiState.kt`, que convierte cada excepción en un error claro: sin conexión, tiempo agotado, servidor caído, receta no encontrada o error inesperado. Reemplazó los `mensajeDe` repetidos de `ListaViewModel` y `DetalleViewModel`. `ErrorView` pasó a ser un `AlertDialog` con **Reintentar** y **Cerrar** (lista) o **Volver** (detalle).

**Qué tuve que corregir yo:** No hubo errores de compilación; se actualizó la prueba de error para revisar el título "Sin conexión" y siguieron pasando las 8. Observé que `UiState.kt`, que es de la capa de UI, ahora importa `HttpException` de Retrofit: no está dentro de un composable, pero lo ideal sería que el repositorio traduzca los errores de Retrofit antes de que lleguen a la UI.

**¿Respeta MVVM?:** Sí. El ViewModel decide **qué** error hay con `errorDe(e)` y la pantalla solo decide **cómo** mostrarlo. Si la ventana está abierta o cerrada es estado solo visual y vive en la pantalla con `rememberSaveable`.

---

## Reflexión

- **¿Qué mejoró la IA y qué no?** _(revisar y escribir con mis palabras)_ Generó rápido código que compilaba en todas las capas (Room, repositorio, ViewModel y pantallas), respetó la regla de no crear archivos y explicó el porqué de cada decisión. Pero no mejoró lo que no le pedí: dejó lógica repetida (el aviso de Deshacer en tres pantallas), una expresión regular que se crea en cada búsqueda y traducciones que se repiten en cada sincronización.
- **¿Qué error cometió?** _(revisar)_ Me entregó mezclados los cambios de dos mejoras distintas y tuve que pedir que los separara para tener commits limpios. Además, cambiar la versión de la base con `fallbackToDestructiveMigration` borra los favoritos una vez, algo que en una app real no sería aceptable.
- **¿Qué aprendí sobre MVVM, Room y reglas de negocio?** _(revisar)_ Que las interfaces (`ItemRepository`, `Notificador`, `Traductor`) permiten cambiar o probar una pieza sin tocar las demás: por eso se pudo probar el ViewModel con falsos. Que con Room como fuente única de verdad y `Flow`, cambiar de categoría o marcar un favorito actualiza la pantalla sola. Y que hay que revisar cada cambio con `git status` y `git diff` antes del commit, porque al pegar archivos es fácil perder un paréntesis o mezclar cambios.