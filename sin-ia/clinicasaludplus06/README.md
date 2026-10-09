# ClinicaSaludPlus
**Proyecto:** Gestión Integral de Citas Médicas
**Repositorio:** ClinicaSaludPlus
## 🚀 Flujo Principal de la Aplicación

El recorrido del usuario sigue la secuencia lógica trazada en la arquitectura del sistema (**1 → 2 → 8 → 3 → 4 → 5 → 6 → 7 → 9 → 10 → 11**):

| Paso / Pantalla | Vista | Descripción |
| :--- | :---: | :--- |
| **1. Splash Screen** | <img width="717" height="1600" alt="image" src="https://github.com/user-attachments/assets/b20158dd-00ed-409e-bd20-4f76f5348c8c" />| Pantalla de bienvenida e inicio del flujo de la aplicación. |
| **2. Registro** | <img width="717" height="1600" alt="image" src="https://github.com/user-attachments/assets/306c7a74-f930-4e3c-8506-5fe5f0f81a99" />| Registro de usuario con enlace a Términos y Condiciones. |
| **8. Iniciar Sesión** |<img width="717" height="1600" alt="image" src="https://github.com/user-attachments/assets/4d797f96-b329-4ad2-96fa-f1106165573c" />| Autenticación y validación de credenciales en memoria. |
| **3. Inicio (Home)** |<img width="717" height="1600" alt="image" src="https://github.com/user-attachments/assets/5e0e73a8-61a4-47d6-aec8-2b7401ee78c6" />| Dashboard principal con accesos rápidos y especialidades. |
| **4. Especialidades** |<img width="717" height="1600" alt="image" src="https://github.com/user-attachments/assets/451e5aaf-94ce-404e-9c6a-1742cce77acc" />| Listado de especialidades médicas con buscador reactivo. |
| **5. Médicos** |<img width="717" height="1600" alt="image" src="https://github.com/user-attachments/assets/35bb7b2c-96aa-4291-93d2-1d66ae84779a" />| Selección de especialista filtrado por `especialidadId`. |
| **6. Fecha y Hora** |<img width="717" height="1600" alt="image" src="https://github.com/user-attachments/assets/694dc96e-76a9-41a9-a458-032db2c9217f" />| Selección dinámica con `LocalDate` y bloqueo de horarios. |
| **7. Confirmar Cita** |<img width="717" height="1600" alt="image" src="https://github.com/user-attachments/assets/15edacd7-ec2f-4356-b47b-b1f76f93ceb5" />| Resumen de la cita y validación de datos del paciente. |
| **9. Cita Agendada** |<img width="717" height="1600" alt="image" src="https://github.com/user-attachments/assets/e7b899fb-51ac-47fe-9829-68f9e55515e4" />| Confirmación exitosa con limpieza de pila (`popUpTo`). |
| **10. Mis Citas** |<img width="717" height="1600" alt="image" src="https://github.com/user-attachments/assets/d1a900aa-917a-4d48-aae1-4664621bef67" /><img width="717" height="1600" alt="image" src="https://github.com/user-attachments/assets/c584cfb6-6a74-4539-9187-eec59914bfa8" />| Listado de citas del usuario con manejo de estado vacío. |
| **11. Perfil / Mis Datos** |<img width="717" height="1600" alt="image" src="https://github.com/user-attachments/assets/5395c23e-f2b4-465e-976d-4d88d1465cd9" />| Consulta de datos del usuario activo y cierre de sesión. |

---

## 🛠️ Retos Extras Integrados (Fase 1)

* **Reto 12 — Detalle de Cita (`DetalleCitaScreen.kt`):**
  Pantalla interactiva con `AlertDialog` de confirmación previa para eliminar/cancelar la cita seleccionada del `Repositorio`.
  <img width="717" height="1600" alt="image" src="https://github.com/user-attachments/assets/a1ab05b4-7894-42a3-9f5d-0a0cec064859" />


* **Reto 13 — Resultados Médicos (`ResultadosScreen.kt`):**
  Listado de exámenes de laboratorio (Hemograma, Perfil Lipídico, Examen de Orina) haciendo uso de un modelo local privado (`ResultadoExamenLocal`), respetando el congelamiento de la carpeta `data.model`.
  <img width="717" height="1600" alt="image" src="https://github.com/user-attachments/assets/49650f23-c77e-4bcd-ad14-edf650f92269" />


* **Reto 14 — Notificaciones Dinámicas (`NotificacionesScreen.kt`):**
  Vista accesible desde la campanita de la barra superior, la cual transforma la lista de citas activas en mensajes mediante el operador `.map()`.
  <img width="717" height="1600" alt="image" src="https://github.com/user-attachments/assets/1e567351-735a-4255-a6bc-b6782ecccd1f" />


* **Reto 15 — Términos y Condiciones (`TerminosScreen.kt`):**
  Pantalla con texto legal estructurado y desplazamiento vertical fluido habilitado con `.verticalScroll(rememberScrollState())`.
  <img width="717" height="1600" alt="image" src="https://github.com/user-attachments/assets/b45b8127-712a-403d-9b76-daf0fb4ef43a" />


---

## 🏗️ Estructura del Proyecto

```text
com.tuapp.saludpluscitas/
├── data/
│   ├── model/           # Modelos base congelados (Cita, Usuario, Medico, Especialidad)
│   └── repository/      # Repositorio estático y gestión de datos en memoria
├── navigation/          # Control de rutas y navegación (AppNavigation, Rutas)
└── ui/
    ├── components/      # Componentes reutilizables (TopBarBase)
    ├── screens/         # Pantallas divididas por módulos
    │   ├── agendamiento/ # Especialidades, Medicos, FechaHora, ConfirmarCita, CitaExitosa
    │   ├── auth/        # SplashScreen, LoginScreen, RegistroScreen, TerminosScreen
    │   ├── citas/       # MisCitasScreen, DetalleCitaScreen
    │   ├── home/        # HomeScreen
    │   ├── notificaciones/ # NotificacionesScreen
    │   ├── perfil/      # PerfilScreen
    │   └── resultados/  # ResultadosScreen
    └── theme/           # Tema visual Material 3
