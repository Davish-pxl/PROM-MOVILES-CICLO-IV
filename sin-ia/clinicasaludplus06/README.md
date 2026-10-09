## VII. Preguntas de reflexión

### 1. ¿Por qué los modelos, Rutas.kt y AppNavigation.kt se entregaron completos y las pantallas no? ¿Qué tienen en común los archivos que sí se dejaron como esqueleto?
Los modelos (`data.model`), `Rutas.kt` y `AppNavigation.kt` definen la arquitectura base, la estructura de datos invariable y el contrato de navegación global de la aplicación. Se entregaron completos para asegurar la coherencia del proyecto y evitar que las firmas de datos o las rutas cambiaran. Por otro lado, las pantallas se dejaron como esqueleto porque representan la capa de interfaz de usuario (`UI`), donde se evalúa el diseño de la vista, el manejo de estados locales (`remember`, `mutableStateOf`) y la integración fluida con Jetpack Compose.

### 2. ¿Por qué el Repositorio es un `object` y no una clase normal? ¿Qué pasaría con las citas si cada pantalla creara su propia lista?
`Repositorio` se definió como un `object` en Kotlin para aplicar el patrón **Singleton**, garantizando una única instancia compartida en toda la aplicación con un único estado global en memoria. Si fuera una clase normal y cada pantalla instanciara su propio objeto, las listas de citas y datos estarían desincronizadas: una cita registrada en una pantalla no se reflejaría en las demás, ya que cada vista tendría su propia copia local de los datos.

### 3. ¿Cómo lograste que la búsqueda de especialidades y los horarios disponibles se actualicen solos, sin que tú "actualices" nada a mano?
Se logró utilizando el modelo de **programación reactiva** de Jetpack Compose mediante los estados observables (`remember`, `mutableStateOf`) y la derivación de estados con `derivedStateOf`. Al vincular la lista de horarios o el filtro de búsqueda a las variables de estado (como `diaSeleccionado` o el texto del buscador), cualquier cambio recarga automáticamente los composables afectados en el árbol de la interfaz sin necesidad de refrescar manualmente.

### 4. ¿Qué diferencia notaste entre `navigate()` normal (Especialidades → Médicos) y el que usa `popUpTo` (Confirmar cita → Cita agendada)? ¿Qué pasa al presionar Atrás en cada caso?
* **`navigate()` normal:** Agrega una nueva pantalla a la parte superior de la pila de navegación (*backstack*). Al presionar **Atrás**, el usuario regresa a la pantalla anterior (ejemplo: de Médicos a Especialidades).
* **`navigate()` con `popUpTo`:** Elimina las pantallas intermedias del *backstack* hasta la ruta especificada antes de navegar a la pantalla destino. Al presionar **Atrás** tras confirmar una cita, el usuario no vuelve al flujo de confirmación ni al calendario, sino que regresa a la pantalla principal (`HomeScreen`), evitando la duplicación de reservas.

### 5. ¿Qué tuviste que corregir del código que te generó la IA para el calendario dinámico?
* **Imports faltantes:** La IA no incluía las importaciones explícitas del paquete `androidx.compose.material.icons.filled.*`, lo que provocaba errores al resolver íconos vectoriales como `MedicalServices` o `Science`.
* **Formato plano de `res/drawable`:** La IA asumía subcarpetas para las imágenes, lo que rompía la compilación. Se ajustó para que los PNGs (`logo_doctor.png`, `doctor.png`, `doctoras.png`, `logo_saludplus.png`) se leyeran directamente con `painterResource`.
* **Limpieza de estado en la selección de hora:** Se añadió la condición para que `horaSeleccionada` se reiniciara obligatoriamente a `null` cada vez que el usuario cambiaba de día o navegaba entre semanas.

### 6. Compara el NavigationDrawer del Laboratorio 6 con el NavigationBar de esta tarea: ¿en qué caso usarías cada uno en un proyecto propio?
* **`NavigationBar` (Bottom Navigation Bar):** Se recomienda en aplicaciones de uso frecuente en dispositivos móviles que cuentan con 3 a 5 secciones principales claramente definidas. Facilita la accesibilidad con una sola mano al estar ubicado en la parte inferior de la pantalla.
* **`NavigationDrawer` (Menú lateral):** Se utiliza cuando la aplicación cuenta con una gran cantidad de secciones (más de 5), categorías secundarias o configuraciones avanzadas del perfil que no requieren acceso constante pero sí estar organizadas en un panel deslizable.

---

## VIII. Observaciones y conclusiones

### Observaciones
1. **Manejo de archivos gráficos en Android Studio:** Se observó que las imágenes PNG dentro de `res/drawable` deben ubicarse de forma estrictamente plana, sin subcarpetas ni caracteres especiales en sus nombres, para evitar fallos de referencia `R.drawable` en tiempo de compilación.
2. **Precisión en los Prompts para la IA:** Para lograr que la IA generara código compilable sin recortar imports o utilizar *placeholders*, fue imprescindible redactar un prompt estructurado por secciones especificando las librerías obligatorias (`material-icons-extended` y `java.time`).

### Conclusiones
1. **Ventajas del desarrollo sobre un esqueleto:** Trabajar a partir de una arquitectura base predefinida (`data.model`, `Rutas` y `Repositorio`) aceleró el desarrollo UI, permitiendo concentrar el esfuerzo exclusivamente en el diseño de las vistas y el manejo de estados en Jetpack Compose.
2. **Evolución de la Fase 1 a la Fase 2 mediante IA:** La integración de un asistente de IA en la Fase 2 optimizó el flujo de trabajo en comparación con el desarrollo manual de la Fase 1. Sin embargo, se evidenció que el rol del desarrollador sigue siendo crítico para auditar, ajustar la sintaxis y corregir la lógica reactiva en los componentes generados.
