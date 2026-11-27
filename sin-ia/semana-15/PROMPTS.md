# PROMPTS.md · Fase 2 (rama mejora-ia15)

Mejoras realizadas con Gemini en Android Studio sobre AvisosCampus, sin romper la arquitectura MVVM + Repository.

| N | Mejora | Capas que cambiaron | Commit |
|---|---|---|---|
| 1 | Dos canales (Avisos y Urgentes) | Notificador | `feat(notificacion): agrega canal Urgentes para el recordatorio` |
| 2 | Texto largo con BigTextStyle | Notificador | `feat(notificacion): muestra texto completo con BigTextStyle` |
| 3 | Receptor del cargador | Model + Receiver + ViewModel + View | `feat(receptor): escucha el cargador con su propio switch` |

## Registro de prompts

| Prompt que escribí | Qué respondió Gemini | Qué cambié o descarté | Por qué |
|---|---|---|---|
| «Tener dos canales: Avisos (IMPORTANCE_DEFAULT) y Urgentes (IMPORTANCE_HIGH); el recordatorio usa Urgentes, sin tocar el ViewModel ni el PendingIntent» | Modificó solo `Notificador.kt`: agregó `CANAL_URGENTE = "urgentes"`, crea ambos canales con `createNotificationChannels` y elige canal y prioridad según `aviso.origen`. Confirmó que `AvisosApp` no necesitaba cambios. | Acepté el código tras revisar que conservaba `FLAG_IMMUTABLE`, `aviso.id` y la comprobación de permiso. Corregí su explicación: dijo que llamar otra vez a `createNotificationChannel` con un id existente "se ignora", pero en realidad sí puede actualizar el nombre y la descripción, y bajar (nunca subir) la importancia si el usuario no la modificó. | La elección del canal es un detalle del sistema de notificaciones, por eso vive en el Notificador y no en el ViewModel. Usar un id nuevo (`urgentes`) evita el problema de que un canal ya creado no sube su importancia. |
| «Mostrar el texto completo al expandir la notificación con BigTextStyle, con el origen y la hora en una segunda línea, conservando setContentText para la vista contraída» | Modificó solo `Notificador.kt`: creó `textoLargo` con el formato pedido y agregó `setStyle(BigTextStyle().bigText(textoLargo))`. Explicó que `setContentText` es la vista contraída y `bigText` la expandida. | Acepté el código. En el prompt pedí un formato exacto y el uso de `origen.texto` para evitar que mostrara el nombre del enum (BOTON en lugar de Botón). | Los textos del laboratorio son cortos; sin el origen y la hora, BigTextStyle no mostraría ninguna diferencia visible. La forma visual de la notificación es responsabilidad del Notificador, por eso no se tocó el ViewModel. |
| «Escuchar ACTION_POWER_CONNECTED y ACTION_POWER_DISCONNECTED con un CargadorReceiver y su propio switch, siguiendo el mismo patrón que el modo avión; el ViewModel solo recibe un Boolean» | Creó `CargadorReceiver.kt` y modificó `Modelos.kt` (Origen.CARGADOR), `AvisosViewModel.kt` (escuchandoCargador, cambiarEscuchaCargador, alCambiarCargador), `PantallaAvisos.kt` (segundo DisposableEffect con RECEIVER_NOT_EXPORTED, unregisterReceiver en onDispose y el nuevo switch) y `HistorialAvisos.kt` (etiqueta verde). | Revisé que el ViewModel no tuviera Context, Intent ni BroadcastReceiver, que hubiera dos DisposableEffect independientes con su unregisterReceiver y que no tocara el Manifest. Corregí su explicación: dijo que el ViewModel es "Kotlin puro independiente del SDK de Android", pero hereda de `androidx.lifecycle.ViewModel`; lo correcto es que no depende de Context ni de clases del sistema, y por eso su lógica se puede probar sin un dispositivo. | Es la mejora que cambia el ViewModel (regla de la Fase 2). El receptor se registra en tiempo de ejecución porque recibe un lambda y solo debe escuchar mientras el switch esté activo; registrarlo en el Manifest lo dejaría activo siempre y no podría comunicarse con el ViewModel. |

## Verificación de las reglas

- No hay `Context`, `NotificationManager` ni `BroadcastReceiver` en `viewmodel/`.
- Los dos receptores (modo avión y cargador) se registran con `RECEIVER_NOT_EXPORTED` y se quitan en `onDispose`.
- El `PendingIntent` sigue siendo `FLAG_IMMUTABLE`.
- No se agregaron dependencias ni se modificó el `AndroidManifest.xml` en la Fase 2.