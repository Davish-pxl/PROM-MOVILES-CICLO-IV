# PROMPTS - Fase 2 (rama mejora-ia14)

Registro de los prompts usados con **Gemini en Android Studio** para mejorar
CentroTareas sin romper la arquitectura MVVM + Repository.

## Mejoras realizadas

| Mejora | Capas que cambian | Commit |
|---|---|---|
| 1. Comparación de tiempos | model (archivo nuevo) + ViewModel + View | `feat(tiempos): agrega tabla de comparacion de tiempos de carga` |
| 2. Reintento automático | ViewModel + View | `feat(reintento): reintenta la carga de pedidos hasta 3 veces con espera creciente` |
| 3. Primos con progreso | Repository + ViewModel + View | `feat(primos): muestra el porcentaje del calculo de primos` |

## Registro de prompts

| N | Prompt que escribí | Qué respondió Gemini | Qué cambié o descarté | Por qué |
|---|---|---|---|---|
| 1 | «Agregar una tabla que acumule los tiempos de cada carga exitosa, para comparar "Una por una" contra "A la vez". Crear model/RegistroTiempo.kt, guardar los últimos 10 registros en el ViewModel, mostrar la tabla con promedios por modo y un botón Limpiar historial.» | Creó `RegistroTiempo(modo, milisegundos)`, agregó `historialTiempos` (StateFlow) y `limpiarHistorial()` en el ViewModel con `takeLast(10)`, y la tabla con columnas N, Modo y Tiempo en la View. Los promedios los calculaba dentro de la View. | Acepté el archivo nuevo en `model/` y el historial en el ViewModel. Le pedí mover el cálculo de los promedios al ViewModel como `promediosPorModo` (StateFlow derivado con `map` y `stateIn`), y la View quedó solo mostrándolos. | El modelo de datos va en la capa `model/` sin tocar `Estados.kt`, y el historial vive en el ViewModel para sobrevivir a la rotación. El prompt decía que la View no debe calcular nada: el promedio es un dato derivado del estado y en MVVM le corresponde al ViewModel. |
| 2 | «Antes de mostrar el error, reintentar automáticamente la carga de pedidos hasta 3 veces con espera creciente (1 s, 2 s) con una función genérica conReintentos, exponer el intento actual y mostrar "Reintentando (intento N de 3)...". La CancellationException nunca cuenta como intento.» | Creó `conReintentos(intentos, esperaInicial, bloque)` con `delay` que se duplica, `intento` como StateFlow que vuelve a 0 en un `finally`, y el texto de reintento en la View. Relanza la `CancellationException` sin reintentar. Además, eliminó los comentarios `// Paso N` del ViewModel aunque el prompt pedía conservarlos. | Revisé que `_simularError.value` se leyera dentro del bloque de `conReintentos`, para que al desactivar el interruptor el siguiente intento tenga éxito: estaba bien. Decidí no restaurar los comentarios eliminados. | La `CancellationException` no es un error: si se reintentara, la corrutina no se detendría al cancelarla. Los comentarios no cambian el funcionamiento y los nombres ya explican el código; quedó registrado que Gemini no respetó esa restricción. |
| 3 | «Primos con progreso: contarPrimos(hasta, alAvanzar: (Float) -> Unit = {}) que informe el avance cada 1 %, progresoPrimos en el ViewModel, el porcentaje mientras Calculando y "Cancelado al N %" al cancelar.» | Cambió la firma en la interface, calculó `paso = maxOf(1, hasta / 100)` y llamó a `alAvanzar` cada 1 % y al final con `1f`. Agregó `progresoPrimos` en el ViewModel y la barra con porcentaje en la View. Conservó `withContext(Dispatchers.Default)` y `ensureActive()`. Explicó que informar en cada número saturaría la UI con cientos de miles de recomposiciones. | Acepté el código. Le pedí cambiar la barra lineal por un `CircularProgressIndicator` determinado con el porcentaje en el centro. Corregí su explicación: `StateFlow` combina los valores y solo guarda el último, así que la pantalla no se redibujaría tantas veces. Gemini afirmó que "todas las pruebas pasan", pero el proyecto no tiene pruebas automáticas, así que las hice yo en el emulador. | El costo real de informar en cada número está en el ciclo pesado: escribir el estado medio millón de veces vuelve más lento el cálculo. El círculo distingue el progreso de los primos de la barra lineal de la Tarea con progreso, que se mantiene así por el contrato del Paso 3. Los resultados (25, 168, 1229 y 41 538) no cambiaron. |

## Reglas verificadas en todas las mejoras

- No se usa `GlobalScope`, `runBlocking` ni `Thread.sleep`.
- La `CancellationException` siempre se relanza.
- La View solo muestra el estado; el ViewModel no recibe `Context`; el Repository no conoce la UI.
- Gemini no ejecutó comandos de git: todos los commits los hice yo, uno por mejora.

## Prompts completos

### Prompt 1 - Comparación de tiempos

```
CONTEXTO
Trabajo en una app Android llamada CentroTareas (paquete com.valcarcel.centrotareas), hecha con Kotlin, Jetpack Compose, Material 3, corrutinas, ViewModel y StateFlow, con arquitectura MVVM + Repository. Estoy en la rama mejora-ia14. La Fase 1 está terminada.

Estructura relevante:
- model/Estados.kt: sealed interface CargaUi con Inicial, Cargando, Exito(modo: String, perfil: String, pedidos: Int, milisegundos: Long) y Error(mensaje: String).
- viewmodel/TareasViewModel.kt: tiene _carga / carga (StateFlow<CargaUi>), cargarUnaPorUna() (modo "Una por una", ≈ 5000 ms) y cargarALaVez() (modo "A la vez" con async dentro de coroutineScope, ≈ 3000 ms). Ambas miden el tiempo con System.currentTimeMillis(), tienen try/catch que relanza la CancellationException y usan simularError.
- ui/PantallaTareas.kt: TarjetaDosCargas(vm) muestra el Switch "Simular error", los botones Una por una (OutlinedButton) y A la vez (Button), un CircularProgressIndicator mientras Cargando, el resultado con RecuadroResultado(titulo, vararg detalles) y el error con RecuadroError + botón Reintentar. Las tarjetas usan TarjetaGris y TituloTarjeta.

OBJETIVO
Agregar una tabla que acumule los tiempos de cada carga exitosa, para comparar "Una por una" contra "A la vez".

REQUISITOS
1. model/: crea un archivo nuevo model/RegistroTiempo.kt con data class RegistroTiempo(val modo: String, val milisegundos: Long). No modifiques Estados.kt.
2. TareasViewModel.kt:
   - Agrega private val _historialTiempos = MutableStateFlow<List<RegistroTiempo>>(emptyList()) y expón solo la versión de lectura historialTiempos: StateFlow<List<RegistroTiempo>>.
   - Cada vez que una carga termine en CargaUi.Exito (en cargarUnaPorUna y en cargarALaVez), agrega un RegistroTiempo al final de la lista. Guarda como máximo los últimos 10 registros.
   - Las cargas con error NO se registran.
   - Agrega fun limpiarHistorial() que deja la lista vacía.
3. PantallaTareas.kt, dentro de TarjetaDosCargas:
   - Debajo del resultado, si el historial no está vacío, muestra el título "Comparación de tiempos" y una tabla simple hecha con Row y Column: columnas "N", "Modo" y "Tiempo (ms)".
   - Debajo de la tabla, muestra el promedio de cada modo (por ejemplo "Promedio Una por una: 5012 ms" y "Promedio A la vez: 3006 ms"). Si un modo no tiene registros, no muestres su promedio.
   - Agrega un TextButton "Limpiar historial" que llama a vm.limpiarHistorial().
   - Mantén la tarjeta gris y el estilo de las otras tarjetas.

REGLAS DE ARQUITECTURA (obligatorias)
- La View solo muestra el estado y avisa eventos: no lanza corrutinas ni calcula tiempos.
- El ViewModel no recibe Context ni conoce Composables.
- No modifiques data/ (Repository).

RESTRICCIONES
- NO ejecutes ningún comando de git (ni add, ni commit, ni push, ni checkout). Yo haré los commits manualmente.
- NO modifiques local.properties, AndroidManifest.xml, build.gradle.kts ni libs.versions.toml.
- NO agregues dependencias nuevas.
- Prohibido usar GlobalScope, runBlocking y Thread.sleep.
- Conserva los comentarios // TODO y // Paso N existentes, los Log.d("HILO", ...) y todo lo que ya funciona (tarea con progreso, cancelar, primos, cargas, simular error y reintentar).

CRITERIOS DE ACEPTACIÓN
- Después de una carga exitosa aparece una fila nueva en la tabla con su modo y su tiempo.
- Los promedios muestran que "A la vez" (≈ 3000 ms) es más rápido que "Una por una" (≈ 5000 ms).
- Con Simular error activado, la carga fallida no se agrega a la tabla.
- Con más de 10 cargas, solo se ven las últimas 10.
- "Limpiar historial" vacía la tabla.
- Al rotar la pantalla, el historial se conserva.
- El proyecto compila sin errores ni advertencias nuevas.

ENTREGA
Al terminar, dame:
1. La lista de archivos que creaste o modificaste.
2. Un resumen de qué cambiaste en cada archivo y por qué.
3. Por qué el historial vive en el ViewModel y no en la View.
```

### Prompt 2 - Reintento automático

```
CONTEXTO
Trabajo en una app Android llamada CentroTareas (paquete com.valcarcel.centrotareas), hecha con Kotlin, Jetpack Compose, Material 3, corrutinas, ViewModel y StateFlow, con arquitectura MVVM + Repository. Estoy en la rama mejora-ia14. Ya existe la tabla de comparación de tiempos (historialTiempos).

Estructura relevante:
- data/TareasRepository.kt (interface) y data/TareasRepositoryLocal.kt: cargarPerfil() tarda 2 s; cargarPedidos(simularError: Boolean) tarda 3 s y, si simularError es true, lanza IOException("Sin conexión").
- viewmodel/TareasViewModel.kt: cargarUnaPorUna() y cargarALaVez() ponen CargaUi.Cargando, llaman al Repository, miden el tiempo y terminan en CargaUi.Exito o, en el catch (e: Exception), primero hacen "if (e is CancellationException) throw e" y luego ponen CargaUi.Error("No se pudo cargar: " + e.message). cargarALaVez() usa async dentro de coroutineScope. simularError es un StateFlow<Boolean>.
- ui/PantallaTareas.kt: TarjetaDosCargas muestra CircularProgressIndicator mientras Cargando, y en Error un RecuadroError con el botón Reintentar.

OBJETIVO
Antes de mostrar el error, reintentar automáticamente la carga de pedidos hasta 3 veces con una espera creciente (1 s, 2 s), y mostrar en pantalla en qué intento va.

REQUISITOS
1. TareasViewModel.kt:
   - Crea una función privada genérica:
     private suspend fun <T> conReintentos(intentos: Int = 3, esperaInicial: Long = 1000, bloque: suspend () -> T): T
     que ejecuta bloque(); si lanza una Exception que NO sea CancellationException y no es el último intento, espera con delay(espera), duplica la espera y vuelve a intentar. En el último intento relanza la excepción. Si la excepción es CancellationException, la relanza de inmediato sin reintentar.
   - Agrega private val _intento = MutableStateFlow(0) y expón intento: StateFlow<Int>. conReintentos actualiza _intento con el número del intento actual (1, 2 o 3). Al terminar la carga (éxito o error) vuelve a 0.
   - Usa conReintentos alrededor de repo.cargarPedidos(...) en cargarUnaPorUna() y en cargarALaVez() (dentro del async). El perfil no necesita reintentos.
   - Si después de los 3 intentos sigue fallando, el estado final es CargaUi.Error("No se pudo cargar tras 3 intentos: " + mensaje).
   - Mantén el try/catch que relanza la CancellationException y el registro en historialTiempos solo para las cargas exitosas.
2. PantallaTareas.kt, dentro de TarjetaDosCargas:
   - Mientras Cargando y intento > 1, muestra debajo del CircularProgressIndicator el texto "Reintentando (intento N de 3)...".
   - No cambies el resto de la tarjeta.

REGLAS DE ARQUITECTURA (obligatorias)
- La lógica de reintentos va en el ViewModel. La View solo muestra el intento.
- No modifiques data/ ni model/.
- El ViewModel no recibe Context ni conoce Composables.

RESTRICCIONES
- NO ejecutes ningún comando de git (ni add, ni commit, ni push, ni checkout). Yo haré los commits manualmente.
- NO modifiques local.properties, AndroidManifest.xml, build.gradle.kts ni libs.versions.toml.
- NO agregues dependencias nuevas.
- Prohibido usar GlobalScope, runBlocking y Thread.sleep. Las esperas son con delay.
- Siempre relanza la CancellationException: nunca la cuentes como un intento fallido.
- Conserva los comentarios // TODO y // Paso N existentes, los Log.d("HILO", ...) y todo lo que ya funciona, incluida la tabla de comparación de tiempos.

CRITERIOS DE ACEPTACIÓN
- Con Simular error desactivado, las cargas funcionan igual que antes (≈ 5000 ms y ≈ 3000 ms) y no aparece el texto de reintento.
- Con Simular error activado, aparece "Reintentando (intento 2 de 3)..." y luego "(intento 3 de 3)...", y al final el recuadro rojo con "No se pudo cargar tras 3 intentos: Sin conexión" y el botón Reintentar.
- Si desactivo Simular error mientras está reintentando, el siguiente intento tiene éxito y sale el recuadro verde.
- El botón Reintentar manual sigue funcionando.
- La app no se cierra en ningún caso.
- El proyecto compila sin errores ni advertencias nuevas.

ENTREGA
Al terminar, dame:
1. La lista de archivos que modificaste.
2. Un resumen de qué cambiaste en cada archivo y por qué.
3. Por qué la CancellationException no debe contarse como un intento fallido.
```

### Prompt 3 - Primos con progreso

```
CONTEXTO
Trabajo en una app Android llamada CentroTareas (paquete com.valcarcel.centrotareas), hecha con Kotlin, Jetpack Compose, Material 3, corrutinas, ViewModel y StateFlow, con arquitectura MVVM + Repository. Estoy en la rama mejora-ia14. Ya existen la tabla de comparación de tiempos, y el reintento automático.

Estructura relevante:
- data/TareasRepository.kt (interface): suspend fun contarPrimos(hasta: Int): Int, más cargarPerfil() y cargarPedidos(simularError).
- data/TareasRepositoryLocal.kt: contarPrimos usa withContext(Dispatchers.Default), recorre de 2 a hasta, llama a ensureActive() en cada vuelta y cuenta con una función privada esPrimo(n) que usa while (d.toLong() * d <= n).
- viewmodel/TareasViewModel.kt: _primos / primos (StateFlow<PrimosUi>), trabajoPrimos (Job?), contar(hasta) pone PrimosUi.Calculando, mide el tiempo con System.currentTimeMillis(), termina en PrimosUi.Listo(cantidad, ms) y, si lo cancelan, captura la CancellationException, pone PrimosUi.Cancelado y la relanza. cancelarPrimos() cancela el Job.
- model/Estados.kt: PrimosUi con Inicial, Calculando, Listo(cantidad, milisegundos) y Cancelado.
- ui/PantallaTareas.kt: TarjetaPrimos(vm) con OutlinedTextField "Hasta el número" (500000 por defecto), botones Contar y Cancelar, el texto "Calculando..." mientras calcula y el resultado en RecuadroResultado. La tarjeta Tarea con progreso ya usa un LinearProgressIndicator con el porcentaje debajo.

OBJETIVO
Mostrar el porcentaje del cálculo de primos mientras avanza.

REQUISITOS
1. TareasRepository.kt: cambia la firma a
   suspend fun contarPrimos(hasta: Int, alAvanzar: (Float) -> Unit = {}): Int
2. TareasRepositoryLocal.kt:
   - Dentro del ciclo, informa el avance con alAvanzar(n.toFloat() / hasta) solo cada 1 % del recorrido (por ejemplo, cuando n % paso == 0, con paso = maxOf(1, hasta / 100)), para no saturar la UI con miles de actualizaciones.
   - Al terminar, llama a alAvanzar(1f).
   - Conserva withContext(Dispatchers.Default), ensureActive() en cada vuelta y esPrimo(n) sin cambios. El resultado de la cuenta no debe cambiar.
3. TareasViewModel.kt:
   - Agrega private val _progresoPrimos = MutableStateFlow(0f) y expón progresoPrimos: StateFlow<Float>.
   - En contar(hasta): pon _progresoPrimos en 0f al empezar y pasa al Repository alAvanzar = { _progresoPrimos.value = it }.
   - Si lo cancelan, el progreso se queda donde estaba (igual que en la tarea con progreso).
   - Conserva la medición del tiempo, PrimosUi.Listo, PrimosUi.Cancelado y el relanzamiento de la CancellationException.
4. PantallaTareas.kt, dentro de TarjetaPrimos:
   - Mientras PrimosUi.Calculando, muestra un LinearProgressIndicator a lo ancho con el progreso de los primos y debajo el porcentaje con (progreso * 100).toInt() seguido de " %", y el texto "Calculando...".
   - En PrimosUi.Cancelado, muestra "Cancelado al N %".
   - No cambies el campo, los botones ni el recuadro del resultado.
5. No modifiques model/Estados.kt.

REGLAS DE ARQUITECTURA (obligatorias)
- El Repository no conoce la UI ni expone estados de pantalla: solo informa un Float de 0f a 1f.
- El ViewModel no recibe Context ni conoce Composables.
- La View solo muestra el estado.

RESTRICCIONES
- NO ejecutes ningún comando de git (ni add, ni commit, ni push, ni checkout). Yo haré los commits manualmente.
- NO modifiques local.properties, AndroidManifest.xml, build.gradle.kts ni libs.versions.toml.
- NO agregues dependencias nuevas.
- Prohibido usar GlobalScope, runBlocking y Thread.sleep.
- Si agregas algún try/catch, relanza siempre la CancellationException.
- Conserva las mejoras anteriores (tabla de tiempos y reintento automático) y todo lo que ya funciona.

CRITERIOS DE ACEPTACIÓN
- Con N = 100, 1000 y 10 000 siguen saliendo 25, 168 y 1229 primos; con 500 000, 41 538.
- Con N grande, la barra avanza de 0 a 100 % mientras calcula y el porcentaje sube.
- Tócame sigue sumando mientras calcula.
- Cancelar a mitad: el estado pasa a "Cancelado al N %" y se puede volver a contar desde 0 %.
- El tiempo del cálculo no aumenta de forma notable respecto a antes.
- La tarea con progreso, las cargas, la tabla de tiempos y el reintento siguen funcionando.
- El proyecto compila sin errores ni advertencias nuevas.

ENTREGA
Al terminar, dame:
1. La lista de archivos que modificaste.
2. Un resumen de qué cambiaste en cada archivo y por qué.
3. Por qué el progreso se informa cada 1 % y no en cada número.
```