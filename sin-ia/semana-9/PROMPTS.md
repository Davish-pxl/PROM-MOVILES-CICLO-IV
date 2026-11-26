# PROMPTS.md · Fase 2 (rama `mejora-ia9`)

Herramienta usada: **Gemini en Android Studio (modo agente)**.
Cada prompt se escribió con el mismo formato: CONTEXTO, OBJETIVO, REQUISITOS POR CAPA,
REGLAS DE ARQUITECTURA, RESTRICCIONES, CRITERIOS DE ACEPTACIÓN y ENTREGA.
Después de cada respuesta revisé el código, lo probé en el emulador y en Logcat, corregí lo
necesario y recién entonces hice el commit a mano (Gemini no ejecutó comandos de git).

## Mejoras realizadas

| # | Mejora | Capas que cambian | Commit |
|---|---|---|---|
| 1 | Encabezado `Accept: application/json` con un Interceptor de OkHttp | RetrofitClient | `feat(red): agrega interceptor con encabezado Accept application/json` |
| 2 | Búsqueda por título sobre las publicaciones ya descargadas | ViewModel + View | `feat(busqueda): filtra por titulo las publicaciones descargadas` |
| 3 | Borrar publicación con `@DELETE("posts/{id}")` y `Response<Unit>` | API + Repository + ViewModel + View | `feat(borrar): elimina publicacion con DELETE y Response en API y Repository` |

La mejora 3 cumple la regla de cambiar la interface de la API y el Repository.

## Registro de prompts

| Prompt que escribí | Qué respondió Gemini | Qué cambié o descarté | Por qué |
|---|---|---|---|
| «Agregar un Interceptor de OkHttp que añada el encabezado `Accept: application/json` a todas las peticiones, antes del `HttpLoggingInterceptor`, sin tocar `baseUrl`, conversor ni logging. Solo modificar `RetrofitClient.kt`.» | Agregó un `addInterceptor { chain -> … }` que copia la petición con `newBuilder().header("Accept", "application/json")` y la continúa con `chain.proceed()`. Lo puso antes del logger y explicó que los interceptores se ejecutan en el orden en que se agregan. | Restauré el comentario `// solo para desarrollo` del `HttpLoggingInterceptor`, que Gemini había borrado. | El prompt pedía no tocar el logging. Ese comentario advierte que el nivel `BODY` registra todo el tráfico y no debe quedar en producción. Verifiqué en Logcat que cada petición muestra `Accept: application/json`. |
| «Agregar un campo de búsqueda que filtre por título las publicaciones ya descargadas: `_busqueda`, `buscar()` y `listaVisible` con `combine` + `stateIn` en el ViewModel; un `OutlinedTextField` "Buscar por título" en `PantallaLista` que lea `listaVisible`, sin nuevas llamadas a la red.» | Agregó `busqueda`, `buscar()` y `listaVisible` en `PostsViewModel` (filtra con `contains(ignoreCase = true)` solo en `Exito`; `Cargando` y `Error` pasan igual) y el campo de búsqueda en `PantallaLista`, que ahora observa `listaVisible`. | Mantuve su lógica, pero restauré los comentarios de sección del ViewModel (`Lista`, `Filtro (null = Todos)`, `Detalle`, `Crear (null = aún no se envió)`) y el comentario de la fila de chips, que borró sin que se le pidiera. | Los comentarios documentan decisiones del diseño. Revisé que el `trim()` estuviera solo al comparar y no al guardar el texto; si estuviera en `buscar()`, no se podrían escribir espacios entre palabras. Comprobé en Logcat que escribir no genera peticiones nuevas. |
| «Agregar un botón Eliminar en el detalle: `@DELETE("posts/{id}")` que devuelva `Response<Unit>`; el Repository revisa `isSuccessful` y devuelve `Result<Unit>`; el ViewModel expone `borrado` y quita el post de la lista local; `AlertDialog` de confirmación y `onEliminada` para volver a la lista.» | Agregó `borrar()` en `PostApi`, en `PostRepository` (lanza `HttpException(response)` dentro de `llamar` si no es exitoso), en `PostsViewModel` (`_borrado`, `borrar`, `limpiarBorrado`), el botón con diálogo en `PantallaDetalle` y `onEliminada` en `MainActivity`. | 1) Agregué `vm.limpiarBorrado()` al abrir una publicación. 2) Cambié `remember` por `rememberSaveable` en el estado del diálogo. 3) Restauré los comentarios que borró en `PostRepository`, `PostsViewModel` y `MainActivity`. | 1) Sin limpiar el estado, el error de un borrado fallido aparecía en otra publicación, y un borrado que terminaba tarde podía cerrar sola la siguiente publicación abierta. 2) Con `remember` el diálogo se cerraba al rotar. 3) Los comentarios explican decisiones (por ejemplo, por qué `mensajeDe` vive fuera de `viewmodel/`). La arquitectura sí la respetó: `Response` y `HttpException` quedaron solo en `data/`. |

## Revisión de reglas en cada respuesta
Después de cada prompt verifiqué:

- `viewmodel/` no importa `retrofit2`, `PostApi`, `Response` ni `HttpException`.
- Ningún Composable llama a la API; las pantallas solo hablan con el ViewModel.
- No hay `catch` vacíos: el Repository relanza `CancellationException` y convierte lo demás en `Result.failure`.
- No se usa `GlobalScope`; todo corre en `viewModelScope`.
- Todas las URLs usan `https://`.
- No se crearon archivos ni se modificó `build.gradle.kts`, `libs.versions.toml` ni `AndroidManifest.xml`.

## Conclusión
Gemini respetó la arquitectura MVVM + Repository en las tres mejoras, pero en todas **eliminó
comentarios existentes** sin que se le pidiera. En la mejora 3 además dejó dos errores de estado
(un estado de borrado que no se limpiaba y un diálogo que se perdía al rotar) que solo
aparecieron al probar casos límite: sin internet, al cambiar de publicación y al rotar la pantalla.
La IA propone, pero la revisión y las pruebas son las que deciden qué código queda.