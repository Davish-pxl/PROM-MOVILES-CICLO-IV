# PROMPTS.md · Fase 2 · CampusGPS (rama mejora-ia13)

## Herramientas y forma de trabajo

- **Implementación:** Gemini en Android Studio, en modo agente. Cada cambio de archivo se aprobó uno por uno con "Allow".
- **Redacción y revisión de los prompts:** los prompts se prepararon con apoyo de Claude (chat), con una estructura fija: contexto, objetivo, requisitos por capa, reglas de MVVM, restricciones, criterios de aceptación y entrega.
- **Regla aplicada:** la IA propone, yo decido. Revisé cada cambio con `git diff`, lo probé en un celular físico y corregí lo que estaba mal antes de hacer el commit.
- **Commits:** los hice manualmente, uno por mejora. En los prompts se prohibió al agente ejecutar comandos de git.
- **Seguridad:** ningún prompt incluyó la clave de API. Se prohibió modificar `local.properties`, `AndroidManifest.xml`, `build.gradle.kts` y `libs.versions.toml`.
- **Nombre de la rama:** `mejora-ia13` en lugar de `mejora-ia`, porque el repositorio contiene todas las semanas del curso y así no se mezcla con las mejoras de otras semanas.

## Mejoras elegidas

| # | Mejora | Capas que cambian | Commit |
|---|---|---|---|
| 1 | Filtrar por categoría | ViewModel + View | `feat(filtro): filtra lugares por categoria con chips` |
| 2 | Radio de cercanía | ViewModel + View | `feat(radio): agrega radio de cercania con circulo en el mapa` |
| 3 | Punto azul nativo | View | `feat(mapa): activa punto azul nativo de ubicacion` |

Las mejoras 1 y 2 cambian el ViewModel, así que se cumple la regla de que al menos una mejora toque el ViewModel o el Repository.

## Registro de prompts

| Prompt que escribí | Qué respondió Gemini | Qué cambié o descarté | Por qué |
|---|---|---|---|
| **Mejora 1 · Filtrar por categoría.** Pedí chips (Todas, Estudio, Comida, Eventos, Servicios) que filtren el mapa y la lista, con el filtrado en el ViewModel y sin pasar el ViewModel a `PanelLugares` (prompt completo abajo). | Modificó `MapaViewModel.kt` (agregó `categoria`, `filtrarCategoria`, `lugaresVisibles` con `combine` + `stateIn`, y calculó `masCercano` sobre los visibles) y `PantallaMapa.kt` (fila de `FilterChip` con scroll en `PanelLugares`, recibida como lambda, y lista de 220 a 180 dp). | **Corregí `filtrarCategoria`:** leía `lugaresVisibles.value` justo después de cambiar `_categoria`. Lo cambié para comprobar con `_lugares.value` y la categoría nueva. También volví a poner los comentarios `// Paso N` que Gemini había borrado. | `lugaresVisibles` se calcula con `stateIn`, que se actualiza de forma asíncrona: en ese instante todavía tenía el filtro anterior, así que un lugar oculto podía seguir seleccionado. Además, el prompt pedía conservar los comentarios. |
| **Mejora 2 · Radio de cercanía.** Pedí chips de 100, 200 y 500 m, un `Circle` alrededor de la posición y una etiqueta "Dentro del radio", con la comparación de distancias solo en el ViewModel (prompt completo abajo). | Modificó 4 archivos: `MapaViewModel.kt` (`radio`, `cambiarRadio`, `idsDentroDelRadio` con `combine(lugares, radio)`), `MapaUbicacion.kt` (parámetro `radioMetros` y `Circle` solo en estado `Lista`), `ListaLugares.kt` (parámetro `idsDentro: Set<Int>` y etiqueta) y `PantallaMapa.kt` (chips de radio). | No cambié la lógica. Revisé con `git diff` que la comparación `metros <= radio` estuviera solo en el ViewModel y que `ListaLugares` solo usara `idsDentro.contains(id)`. Observé que `cambiarRadio` quedó entre las propiedades y lo ordené. | La regla de oro de MVVM: ningún Composable calcula distancias. También confirmé que `filtrarCategoria` no se hubiera revertido a la versión con error. |
| **Mejora 3 · Punto azul nativo.** Pedí activar `isMyLocationEnabled` y el botón "Mi ubicación" solo con el permiso concedido, con el estado del permiso en la View (prompt completo abajo). | Modificó `PantallaMapa.kt` (estado `permisoConcedido` con `remember { mutableStateOf(...) }`, actualizado en el callback del lanzador y en `alPulsar()`, pasado a los dos diseños) y `MapaUbicacion.kt` (`MapProperties`, `MapUiSettings` y `@SuppressLint("MissingPermission")`). | Lo acepté sin cambios. Verifiqué que no tocara el ViewModel y que siguieran el marcador "Estás aquí", el verde (`HUE_GREEN`) y el `Circle`. Anoté una limitación (ver abajo). | El permiso es un asunto de la plataforma Android: necesita `Context` y el lanzador de la Activity, así que pertenece a la View. El ViewModel queda libre de dependencias de Android y se puede probar sin emular permisos. |

## Errores de arquitectura y de lógica que corregí

1. **`filtrarCategoria` leía un `StateFlow` todavía sin actualizar (mejora 1).** Gemini escribió `lugaresVisibles.value.any { ... }` justo después de `_categoria.value = cat`. Como `lugaresVisibles` se deriva con `combine` + `stateIn`, su valor se actualiza en una corrutina aparte y, en ese momento, todavía corresponde al filtro anterior. El código compilaba y a veces parecía funcionar, pero el resultado no estaba garantizado. Lo corregí comprobando directamente con `_lugares.value` y la categoría nueva.

2. **Gemini borró comentarios que el prompt pedía conservar (mejora 1).** Eliminó los `// TODO` y `// Paso N` de la Fase 1. Los restauré y agregué `// Mejora N (Fase 2)` para que se vea qué parte del código es de cada fase.

3. **Mezcla de versiones del ViewModel (mejora 2).** Al reemplazar `MapaViewModel.kt` por una copia anterior, se perdieron `radio` e `idsDentroDelRadio`, y `PantallaMapa.kt` dejó de compilar con el error "Property delegate must have a getValue...". No usé "Fix with AI", porque podía inventar propiedades nuevas y deshacer la corrección de `filtrarCategoria`. Lo resolví revisando qué propiedades usaba la pantalla y uniendo en un solo archivo el filtro corregido y el radio.

4. **Limitación detectada en la mejora 3.** Si el usuario concede el permiso desde "Abrir ajustes" y vuelve a la app, el punto azul no aparece hasta que pulsa "Obtener mi ubicación", porque `permisoConcedido` solo se actualiza en el callback del lanzador y en `alPulsar()`. Se podría mejorar volviendo a comprobar el permiso en el evento `ON_RESUME` del ciclo de vida. No lo implementé porque no afecta al uso normal: al pulsar el botón se corrige.

## Cómo verifiqué cada mejora

- `git diff --stat` para confirmar que solo cambiaran los archivos permitidos.
- `git diff` y `Select-String` para buscar lógica fuera de lugar (por ejemplo, `metros <=` en `ListaLugares.kt`) y confirmar que no se perdiera código (`Estás aquí`, `HUE_GREEN`, `Circle(`).
- Pruebas en un celular físico: filtro con selección activa, cambio de radio, rotación de pantalla, permiso denegado y GPS apagado.

## Prompts completos

### Mejora 1 · Filtrar por categoría

```
CONTEXTO
Trabajo en una app Android llamada CampusGPS (paquete com.valcarcel.campusgps), hecha con Kotlin, Jetpack Compose, Material 3 y Maps Compose, con arquitectura MVVM + Repository. Estoy en la rama mejora-ia13.

Estructura relevante:
- model/Modelos.kt: Posicion, Lugar(id, nombre, categoria, latitud, longitud), LugarConDistancia(lugar, metros: Float?), TipoError y UbicacionUi (Inicial, Buscando, Lista, Error).
- viewmodel/MapaViewModel.kt: expone estado, lugares (StateFlow<List<LugarConDistancia>>, ordenada por distancia), seleccionado (StateFlow<Int?>) y masCercano (StateFlow<Lugar?>). Tiene buscar(), permisoDenegado() y seleccionar(id).
- ui/PantallaMapa.kt: lee los StateFlow con collectAsState() y arma la pantalla. Tiene un Composable privado PanelLugares (botones, textos y lista) que se usa en vertical y en horizontal.
- ui/MapaUbicacion.kt: GoogleMap con el marcador "Estás aquí" y un Marker por lugar (verde el más cercano, azul los demás).
- ui/ListaLugares.kt: LazyColumn de tarjetas; recibe lugares, seleccionado, onClick(id) y masCercanoId.

Las categorías existentes son: Estudio, Comida, Eventos, Servicios.

OBJETIVO
Agregar un filtro por categoría con chips (Todas, Estudio, Comida, Eventos, Servicios) que muestre solo los lugares de esa categoría, tanto en el mapa como en la lista.

REQUISITOS POR CAPA
1. ViewModel (MapaViewModel.kt):
   - Agrega un estado privado _categoria: MutableStateFlow<String?> (null = Todas) y su versión pública categoria: StateFlow<String?>.
   - Agrega fun filtrarCategoria(categoria: String?).
   - Agrega lugaresVisibles: StateFlow<List<LugarConDistancia>>, calculado con combine(lugares, categoria) y stateIn. Debe mantener el orden por distancia.
   - masCercano debe calcularse sobre lugaresVisibles, para que el verde siempre sea un lugar visible.
   - Si el lugar seleccionado queda oculto por el filtro, pon el seleccionado en null.
   - No cambies la lógica existente de buscar(), actualizarDistancias(), permisoDenegado() ni del manejo de errores.
2. View:
   - En PanelLugares (PantallaMapa.kt) agrega, encima de la lista, una fila horizontal con scroll de FilterChip de Material 3: Todas, Estudio, Comida, Eventos, Servicios. El chip activo se ve seleccionado.
   - Al tocar un chip se llama a vm.filtrarCategoria(...) mediante un parámetro lambda. PanelLugares NO debe recibir el ViewModel.
   - MapaUbicacion y ListaLugares deben recibir lugaresVisibles en lugar de lugares. No cambies su lógica interna.
   - En vertical, reduce la altura de la lista de 220.dp a 180.dp para compensar la fila de chips.

REGLAS DE ARQUITECTURA (obligatorias)
- La View solo habla con su ViewModel. Los Composables no filtran ni calculan nada: solo dibujan lo que reciben.
- El ViewModel no recibe Context ni conoce Composables.
- No modifiques model/, data/, util/, LugaresRepositoryLocal.kt ni MensajeError.kt.

RESTRICCIONES
- NO ejecutes ningún comando de git (ni add, ni commit, ni push, ni checkout). Yo haré los commits manualmente.
- NO modifiques local.properties, AndroidManifest.xml, build.gradle.kts ni libs.versions.toml.
- NO agregues dependencias nuevas.
- NO muestres ni escribas ninguna clave de API.
- Conserva los comentarios // TODO y // Paso N existentes.

CRITERIOS DE ACEPTACIÓN
- Al abrir la app, el chip "Todas" está activo y se ven los 5 lugares.
- Al tocar "Estudio", el mapa y la lista muestran solo Biblioteca Central y Laboratorio de Cómputo.
- La lista filtrada sigue ordenada por distancia y el verde es el más cercano de los visibles.
- Al volver a "Todas", reaparecen los 5.
- El filtro se mantiene al rotar la pantalla.
- El proyecto compila sin errores ni advertencias nuevas.

ENTREGA
Al terminar, dame:
1. La lista de archivos que modificaste.
2. Un resumen de qué cambiaste en cada archivo y por qué.
3. Cómo cumple tu solución las reglas de MVVM.
```

### Mejora 2 · Radio de cercanía

```
CONTEXTO
Trabajo en una app Android llamada CampusGPS (paquete com.valcarcel.campusgps), hecha con Kotlin, Jetpack Compose, Material 3 y Maps Compose, con arquitectura MVVM + Repository. Estoy en la rama mejora-ia13. Ya existe un filtro por categoría (lugaresVisibles y filtrarCategoria en el ViewModel, y FilterChip en PanelLugares).

Estructura relevante:
- model/Modelos.kt: Posicion, Lugar, LugarConDistancia(lugar, metros: Float?), UbicacionUi (Inicial, Buscando, Lista(posicion), Error).
- viewmodel/MapaViewModel.kt: expone estado, lugares, lugaresVisibles, categoria, seleccionado y masCercano. Las distancias ya se calculan en el ViewModel (actualizarDistancias con util/distanciaMetros).
- ui/PantallaMapa.kt: arma la pantalla y tiene el Composable privado PanelLugares.
- ui/MapaUbicacion.kt: GoogleMap con el marcador "Estás aquí" (solo en estado Lista) y los marcadores de lugares.
- ui/ListaLugares.kt: LazyColumn de tarjetas con punto de color, nombre, categoría y distancia.

OBJETIVO
Agregar un selector de radio de cercanía (100, 200 o 500 m). Debe dibujar un Circle alrededor de la posición del usuario en el mapa y marcar en la lista qué lugares están dentro de ese radio.

REQUISITOS POR CAPA
1. ViewModel (MapaViewModel.kt):
   - Agrega _radio: MutableStateFlow<Int> con valor inicial 200 y su versión pública radio: StateFlow<Int>.
   - Agrega fun cambiarRadio(metros: Int).
   - Agrega idsDentroDelRadio: StateFlow<Set<Int>>, calculado con combine(lugares, radio) y stateIn: contiene el id de cada lugar con metros != null y metros <= radio.
   - El cálculo de "dentro o fuera" se hace SOLO aquí, nunca en un Composable.
   - No cambies la lógica existente (buscar, actualizarDistancias, filtro por categoría, seleccionar, masCercano, errores).
2. View:
   - En PanelLugares agrega una fila con tres FilterChip: "100 m", "200 m" y "500 m". El activo se ve seleccionado. Al tocar uno se llama a una lambda que ejecuta vm.cambiarRadio(...). PanelLugares NO recibe el ViewModel.
   - En MapaUbicacion agrega un parámetro radioMetros: Int. Solo cuando el estado es UbicacionUi.Lista, dibuja un Circle de Maps Compose centrado en la posición del usuario, con radius = radioMetros.toDouble(), relleno azul semitransparente y borde azul delgado.
   - En ListaLugares agrega un parámetro idsDentro: Set<Int> (con valor por defecto emptySet() para no romper llamadas existentes). Las tarjetas de lugares dentro del radio muestran una etiqueta pequeña "Dentro del radio" o un indicador visual claro. ListaLugares solo pregunta si el id está en el conjunto: no calcula distancias.
   - Si hace falta espacio, pon los chips de radio en la misma fila con scroll horizontal que los chips de categoría, separados visualmente.

REGLAS DE ARQUITECTURA (obligatorias)
- La View solo habla con su ViewModel. Los Composables no calculan distancias ni comparan con el radio.
- El ViewModel no recibe Context ni conoce Composables.
- No modifiques model/, data/, util/, LugaresRepositoryLocal.kt ni MensajeError.kt.

RESTRICCIONES
- NO ejecutes ningún comando de git (ni add, ni commit, ni push, ni checkout). Yo haré los commits manualmente.
- NO modifiques local.properties, AndroidManifest.xml, build.gradle.kts ni libs.versions.toml.
- NO agregues dependencias nuevas (Circle ya está en maps-compose).
- NO muestres ni escribas ninguna clave de API.
- Conserva los comentarios // TODO y // Paso N existentes y el filtro por categoría.

CRITERIOS DE ACEPTACIÓN
- Antes de buscar la ubicación no se dibuja ningún círculo y ninguna tarjeta aparece como "Dentro del radio".
- Después de buscar, se ve un círculo de 200 m alrededor del marcador "Estás aquí".
- Al cambiar a 100 m o 500 m, el círculo cambia de tamaño y las etiquetas "Dentro del radio" se actualizan.
- El filtro por categoría sigue funcionando junto con el radio.
- El radio elegido se mantiene al rotar la pantalla.
- El proyecto compila sin errores ni advertencias nuevas.

ENTREGA
Al terminar, dame:
1. La lista de archivos que modificaste.
2. Un resumen de qué cambiaste en cada archivo y por qué.
3. Cómo cumple tu solución las reglas de MVVM.
```

### Mejora 3 · Punto azul nativo

```
CONTEXTO
Trabajo en una app Android llamada CampusGPS (paquete com.valcarcel.campusgps), hecha con Kotlin, Jetpack Compose, Material 3 y Maps Compose, con arquitectura MVVM + Repository. Estoy en la rama mejora-ia13. Ya existen el filtro por categoría y el radio de cercanía.

Estructura relevante:
- ui/PantallaMapa.kt: pide el permiso de ubicación con rememberLauncherForActivityResult(RequestMultiplePermissions()) y comprueba FINE o COARSE con ContextCompat.checkSelfPermission dentro de la función alPulsar(). Tiene diseño vertical y horizontal.
- ui/MapaUbicacion.kt: GoogleMap(modifier, cameraPositionState) con el marcador rojo "Estás aquí", los marcadores de lugares y el Circle del radio.

OBJETIVO
Activar el punto azul nativo de Google Maps (isMyLocationEnabled) y el botón "Mi ubicación" del mapa, SOLO si el permiso de ubicación fue concedido.

REQUISITOS (solo capa View)
1. PantallaMapa.kt:
   - Crea un estado local permisoConcedido con remember { mutableStateOf(...) }, inicializado comprobando FINE o COARSE con ContextCompat.checkSelfPermission.
   - Actualízalo a true en el callback del lanzador cuando el usuario acepta, y también cuando alPulsar() detecta que ya tiene el permiso.
   - Pásalo a MapaUbicacion en los dos diseños (vertical y horizontal).
   - El permiso se maneja solo en la View: NO lo muevas al ViewModel.
2. MapaUbicacion.kt:
   - Agrega un parámetro permisoConcedido: Boolean = false.
   - En GoogleMap usa properties = MapProperties(isMyLocationEnabled = permisoConcedido) y uiSettings = MapUiSettings(myLocationButtonEnabled = permisoConcedido).
   - Si hace falta, anota la función con @SuppressLint("MissingPermission") y agrega un comentario que explique que el permiso ya se verificó en PantallaMapa.
   - Conserva el marcador rojo "Estás aquí", los marcadores de lugares, el verde del más cercano y el Circle del radio.

REGLAS DE ARQUITECTURA (obligatorias)
- No modifiques el ViewModel, model/, data/ ni util/.
- El ViewModel no debe recibir Context ni información de permisos.

RESTRICCIONES
- NO ejecutes ningún comando de git (ni add, ni commit, ni push, ni checkout). Yo haré los commits manualmente.
- NO modifiques local.properties, AndroidManifest.xml, build.gradle.kts ni libs.versions.toml.
- NO agregues dependencias nuevas (MapProperties y MapUiSettings ya están en maps-compose).
- NO muestres ni escribas ninguna clave de API.
- Conserva los comentarios // TODO y // Paso N existentes y las mejoras anteriores.

CRITERIOS DE ACEPTACIÓN
- Con el permiso denegado, no aparece el punto azul ni el botón "Mi ubicación", y la app no se cierra.
- Al aceptar el permiso, aparecen el punto azul nativo y el botón "Mi ubicación" en la esquina del mapa, sin reiniciar la app.
- Al pulsar el botón "Mi ubicación" del mapa, la cámara va al punto azul.
- El marcador rojo "Estás aquí", los marcadores de lugares, el filtro y el radio siguen funcionando.
- Funciona en vertical y en horizontal.
- El proyecto compila sin errores ni advertencias nuevas.

ENTREGA
Al terminar, dame:
1. La lista de archivos que modificaste.
2. Un resumen de qué cambiaste en cada archivo y por qué.
3. Por qué el estado del permiso se queda en la View y no en el ViewModel.
```