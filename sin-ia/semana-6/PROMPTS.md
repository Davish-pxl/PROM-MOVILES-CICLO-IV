# Documentación de Prompts - TecsupStoreIA (Fase 2)

## Información General
- **Proyecto:** TecsupStoreIA
- **Rama Git:** `mejora-iaa`
- **Fase:** Fase 2 - Menú Contextual DropdownMenu y NavigationDrawer Lateral con Badge de Favoritos.

---

## Prompts Utilizados en la Generación e Implementación

### Prompt 1: Configuración del Nombre de la Aplicación
> "Configura el archivo `strings.xml` de la aplicación Android para que el nombre oficial de la app sea 'TecsupStoreIA', asegurando que no afecte configuraciones ni dependencias previas."

### Prompt 2: Menú Contextual DropdownMenu en TarjetaProducto.kt
> "Implementa en `TarjetaProducto.kt` un ícono de tres puntos verticales (`Icons.Default.MoreVert`) alineado a la derecha de cada tarjeta de producto. Al hacer clic en los 3 puntos, se debe desplegar un `DropdownMenu` con las siguientes opciones: 'Favoritos', 'Compartir' y 'Reportar'. Cada opción debe incluir su correspondiente ícono (`leadingIcon`). La opción 'Favoritos' debe ser interactiva, cambiando dinámicamente entre agregar y remover de favoritos y actualizando de forma reactiva un estado global compartido (`mutableStateListOf`)."

### Prompt 3: NavigationDrawer Completo en AppDrawer.kt
> "Crea el componente `AppDrawer.kt` que defina el contenido del `NavigationDrawer` de la aplicación TecsupStoreIA. Debe incluir:
> 1. Un encabezado con un avatar circular con las iniciales 'DV', el nombre 'David Valcarcel' y el correo 'david@tecsup.edu.pe'.
> 2. Opciones de navegación: 'Inicio', 'Mis pedidos', 'Favoritos', 'Perfil' y 'Cerrar sesión'.
> 3. En la opción 'Favoritos', incluir un `Badge` numérico dinámico que muestre en tiempo real el conteo de elementos en la lista de favoritos.
> 4. Resaltado visual del ítem activo dentro del drawer usando colores distintivos (`selectedContainerColor`)."

### Prompt 4: Rutas de Navegación en Screen.kt
> "Actualiza `Screen.kt` para gestionar todas las rutas de navegación requeridas en la aplicación TecsupStoreIA: `Home` ('home'), `List` ('list'), `Favoritos` ('favoritos'), `Profile` ('profile') y `Detail` ('detail/{itemId}')."

### Prompt 5: Orquestación de Navegación Global en AppNavegacion.kt
> "Crea el archivo `AppNavegacion.kt` que envuelva toda la estructura de navegación principal con `ModalNavigationDrawer`, gestionando el `NavHost`, el botón hamburguesa (≡) en la `TopAppBar` de las pantallas principales para abrir el drawer, y el estado global reactivo de favoritos mediante `mutableStateListOf`."

---

## Resultados y Verificación
- **Compilación:** Construcción exitosa en Gradle (`app:assembleDebug`).
- **Navegación:** Funcionamiento fluido del drawer lateral y navegación entre pantallas (`Home`, `List`, `Favoritos`, `Profile`, `Detail`).
- **Reactividad:** Actualización instantánea del `Badge` de favoritos en el drawer al agregar o quitar productos desde las tarjetas.
