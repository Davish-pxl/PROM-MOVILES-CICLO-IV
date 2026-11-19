# CentroTareas

App Android hecha con Kotlin, Jetpack Compose y Material 3 que ejecuta tareas
que tardan (una tarea con progreso, un cálculo pesado de números primos y dos
cargas de datos simuladas) sin congelar la pantalla, usando corrutinas,
ViewModel y el patrón MVVM + Repository. Todos los datos son ficticios.

## Observación del Paso 1

**Qué pasa con la pantalla:** al pulsar "Tarea larga (MAL)" la app deja de
responder durante unos 4 segundos. Si toco "Tócame" en ese tiempo, el contador
no sube y los botones no reaccionan. Cuando terminan los 4 segundos, la
pantalla vuelve a responder y el mensaje cambia a "Terminó".

Los toques hechos durante el congelamiento no se pierden: quedan en la cola de
eventos del hilo principal y se procesan todos juntos cuando termina
`Thread.sleep`, por eso el contador salta de golpe al número de veces que
presioné. Después, los toques se registran normalmente y el mensaje se queda
en "Terminó" hasta que vuelvo a lanzar la tarea.

**Por qué no se ve "Trabajando...":** `tareaLargaMala()` se ejecuta en el hilo
principal, que es el mismo hilo que dibuja la pantalla y atiende los toques.
El valor del mensaje sí cambia a "Trabajando...", pero `Thread.sleep(4000)`
bloquea ese hilo y Compose no puede redibujar. Cuando el hilo se libera, el
mensaje ya cambió a "Terminó", por eso solo se dibuja el valor final.

**Qué mostraría Android con 10 segundos:** al no responder a los toques
durante más de unos 5 segundos, Android mostraría el diálogo ANR
("La aplicación no responde"), con las opciones de cerrar la app o esperar.