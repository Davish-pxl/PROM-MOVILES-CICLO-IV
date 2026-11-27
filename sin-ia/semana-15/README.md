# AvisosCampus

Aplicación Android que muestra notificaciones locales y escucha anuncios del sistema. Con un botón muestra un aviso, con otro programa un recordatorio que llega a los 10 segundos y, si el usuario lo activa, avisa cuando se activa o desactiva el modo avión. Todos los avisos quedan anotados en un historial.

Laboratorio de la Semana 15 del curso Programación en Móviles (4to ciclo).

## Tecnologías

- Kotlin + Jetpack Compose + Material 3 (sin XML)
- Notificaciones locales con `NotificationCompat` y `NotificationChannel`
- Permiso `POST_NOTIFICATIONS` en tiempo de ejecución (Android 13+)
- `PendingIntent` para abrir la app al tocar la notificación
- Corrutinas (`viewModelScope`, `delay`, `Job`) para el recordatorio
- `BroadcastReceiver` registrado en tiempo de ejecución para el modo avión
- Patrón MVVM + Repository (interface)

## Funcionalidades

| Función | Descripción |
|---|---|
| Canal de notificación | Se crea el canal "Avisos" una sola vez en la clase `Application` |
| Permiso | Pide `POST_NOTIFICATIONS` y muestra su estado: Sin pedir, Permitido o Denegado |
| Mostrar aviso | Muestra la notificación "Tarea lista"; cada aviso usa su propio id |
| Abrir la app | Al tocar la notificación se abre `MainActivity` y la notificación desaparece |
| Recordatorio | Cuenta regresiva de 10 s con opción de cancelar |
| Receptor | Escucha el modo avión mientras el switch está activo |
| Historial | Lista de los 20 avisos más recientes, lo más nuevo arriba |
| Permiso denegado | El aviso se guarda en el historial, se muestra un mensaje y un botón para abrir Ajustes |

## Estructura de paquetes

```
com.valcarcel.avisoscampus/
├── AvisosApp.kt                    Application: crea el canal
├── MainActivity.kt
├── model/
│   └── Modelos.kt                  Aviso, Origen, PermisoUi
├── data/
│   └── AvisosRepository.kt         interface + AvisosRepositoryMemoria
├── notificacion/
│   └── Notificador.kt              canal, permiso y mostrar
├── receiver/
│   └── ModoAvionReceiver.kt
├── viewmodel/
│   ├── AvisosViewModel.kt
│   └── AvisosViewModelFactory.kt
└── ui/
    ├── PantallaAvisos.kt
    ├── HistorialAvisos.kt
    └── theme/
```

## Arquitectura MVVM + Repository

```
View (PantallaAvisos)
  | eventos: botones, switch          ^ collectAsState()
  v                                   |
ViewModel (AvisosViewModel) --- StateFlow (avisos, permiso, faltan, mensaje...) --->
  | registra el aviso                 | emite: eventos (SharedFlow<Aviso>)
  v                                   v
AvisosRepository (interface)          View recibe el evento y llama a
  -> AvisosRepositoryMemoria          Notificador.mostrar(context, aviso)
```

| Capa | Conoce a | No debe |
|---|---|---|
| View (`ui/`) | Solo a su ViewModel y al Notificador | Decidir qué se avisa ni guardar el historial |
| ViewModel | Solo a la interface del Repository | Conocer Composables, recibir un Context ni mostrar notificaciones |
| Repository | Nada de la UI | Usar Context |
| Notificador | El sistema (canal, NotificationManager) | Guardar estado de la app |

El ViewModel decide qué avisar y lo anota en el Repository. Después emite un evento. La pantalla lo recibe y, como sí tiene `Context`, llama al `Notificador`. Así el ViewModel nunca toca el sistema de notificaciones y se puede probar sin Android.

## Capturas

| Notificación en el panel | Recordatorio |
|---|---|
| ![Notificación](capturas/01-notificacion.png) | ![Recordatorio](capturas/02-recordatorio.png) |

| Modo avión | Historial |
|---|---|
| ![Modo avión](capturas/03-modo-avion.png) | ![Historial](capturas/04-historial.png) |

| Permiso denegado |
|---|
| ![Permiso denegado](capturas/05-permiso-denegado.png) |

## Pruebas de aceptación

Probado en emulador con Android 13 o superior.

| Prueba | Resultado esperado | OK |
|---|---|---|
| Abrir Ajustes → Apps → AvisosCampus → Notificaciones | Existe el canal Avisos | [x] |
| Pulsar Permitir notificaciones | Aparece el cuadro de Android y la etiqueta cambia | [x] |
| Pulsar Mostrar aviso | Aparece la notificación Tarea lista | [x] |
| Pulsar Mostrar aviso dos veces | Aparecen dos notificaciones distintas | [x] |
| Tocar la notificación | Se abre la app y la notificación desaparece | [x] |
| Recordar en 10 s | Cuenta regresiva y aviso al llegar a 0 | [x] |
| Recordar y minimizar la app | El aviso llega igual | [x] |
| Recordar y cancelar | No llega ningún aviso | [x] |
| Switch activado y cambiar el modo avión | Aviso Activado y luego Desactivado | [x] |
| Switch desactivado y cambiar el modo avión | No hay aviso | [x] |
| Permiso denegado y Mostrar aviso | Mensaje de error, aviso en el historial y la app no se cierra | [x] |
| Rotar la pantalla durante el recordatorio | La cuenta regresiva continúa | [x] |
| Revisar el código | No hay Context en `viewmodel/` y todo receptor registrado se quita en `onDispose` | [x] |

Observación: en Android 13 o superior, el canal "Avisos" solo aparece en Ajustes cuando la app declara el permiso `POST_NOTIFICATIONS` en el Manifest. Antes de eso, Ajustes muestra "This app does not send notifications" aunque el canal ya exista (se comprobó con `adb shell dumpsys notification`).

## Para pensar

**1. ¿Por qué una notificación necesita un canal y un permiso? ¿Qué pasa si falta cada uno?**

El canal (Android 8+) agrupa las notificaciones por tipo y define su importancia, sonido y vibración; además, le permite al usuario controlar cada tipo por separado desde Ajustes. El permiso (Android 13+) es la autorización del usuario para que la app pueda mostrar notificaciones. Si falta el canal, en Android 8+ la notificación no se muestra y el sistema registra un error en Logcat. Si falta el permiso, `notify` no muestra nada; por eso `Notificador.mostrar` revisa antes `tienePermiso` y devuelve `false`.

**2. ¿Por qué el ViewModel emite un evento y no muestra la notificación él mismo?**

Porque mostrar una notificación necesita un `Context`, y el ViewModel no debe guardar uno: vive más que la Activity (sobrevive a la rotación) y retener su Context causaría fugas de memoria. Además, separarlo mantiene las responsabilidades: el ViewModel decide qué avisar y registra el aviso, y la View, que sí tiene Context, se encarga de mostrarlo. Así el ViewModel se puede probar sin Android.

**3. ¿Qué pasa si usas el mismo id en dos notificaciones?**

La segunda reemplaza a la primera: en el panel queda una sola notificación con el contenido más reciente. Por eso se usa `aviso.id`, que es distinto para cada aviso. Usar el mismo id a propósito sirve para actualizar una notificación existente, por ejemplo una barra de progreso.

**4. ¿Por qué onReceive debe ser corto y por qué hay que quitar el receptor al salir?**

`onReceive` se ejecuta en el hilo principal y Android le da muy poco tiempo; si hace trabajo largo, bloquea la interfaz y puede provocar un ANR (la app no responde). Por eso solo lee el dato y se lo entrega al ViewModel. Hay que quitar el receptor con `unregisterReceiver` en `onDispose` porque, si se queda registrado, sigue vivo aunque la pantalla ya no exista: se filtra memoria y puede seguir reaccionando a anuncios cuando ya no debería.

**5. ¿En qué casos usarías una notificación local y en cuáles una push de Firebase?**

Una notificación local la genera la propia app en el dispositivo, sin internet: recordatorios, alarmas, avisos de una tarea terminada o de un cambio del celular (como el modo avión). Una push de Firebase la envía un servidor cuando ocurre algo fuera del celular: un mensaje nuevo de otro usuario, una nota publicada por el docente o una promoción. La regla es: si el evento nace en el dispositivo, local; si nace en el servidor o en otro usuario, push.

## Autor

David Valcarcel - Programación en Móviles, 4to ciclo