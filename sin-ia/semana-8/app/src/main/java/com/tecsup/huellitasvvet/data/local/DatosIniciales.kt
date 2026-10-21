package com.tecsup.huellitasvvet.data.local

import androidx.room.withTransaction
import com.tecsup.huellitasvvet.data.local.entity.Cita
import com.tecsup.huellitasvvet.data.local.entity.Mascota
import com.tecsup.huellitasvvet.data.local.entity.Servicio
import com.tecsup.huellitasvvet.data.local.entity.Usuario
import com.tecsup.huellitasvvet.model.EstadoCita
import com.tecsup.huellitasvvet.model.Especie
import com.tecsup.huellitasvvet.model.Rol
import com.tecsup.huellitasvvet.model.Sexo
import java.time.LocalDate

object DatosIniciales {
    private fun dia(n: Long): String = LocalDate.now().plusDays(n).toString()

    suspend fun sembrar(db: VetDatabase) {
        if (db.usuarioDao().contar() > 0) return

        db.withTransaction {
            db.servicioDao().insertarTodos(listOf(
                Servicio(nombre = "Consulta general", descripcion = "Revisión completa de salud",
                    duracionMin = 30, precio = 60.0, icono = "heart"),
                Servicio(nombre = "Vacunación", descripcion = "Aplicación de vacunas",
                    duracionMin = 30, precio = 45.0, icono = "syringe"),
                Servicio(nombre = "Baño y corte", descripcion = "Baño, corte de pelo y uñas",
                    duracionMin = 60, precio = 50.0, icono = "scissors"),
                Servicio(nombre = "Desparasitación", descripcion = "Control de parásitos",
                    duracionMin = 15, precio = 25.0, activo = false, icono = "syringe")
            ))

            db.usuarioDao().insertarTodos(listOf(
                Usuario(nombre = "Ana Garcia", correo = "ana@correo.test", clave = "1234", rol = Rol.CLIENTE.name),
                Usuario(nombre = "Luis Ramos", correo = "luis@correo.test", clave = "1234", rol = Rol.CLIENTE.name),
                Usuario(nombre = "Dra. Rojas", correo = "vet@huellitas.test", clave = "admin", rol = Rol.ADMIN.name)
            ))

            db.mascotaDao().insertarTodos(listOf(
                Mascota(duenoId = 1, nombre = "Luna", especie = Especie.GATO.name, raza = "Siamésa",
                    anioNacimiento = 2023, peso = 4.3, sexo = Sexo.HEMBRA.name),
                Mascota(duenoId = 1, nombre = "Max", especie = Especie.PERRO.name, raza = "Labrador",
                    anioNacimiento = 2022, peso = 24.5, sexo = Sexo.MACHO.name),
                Mascota(duenoId = 2, nombre = "Rocky", especie = Especie.PERRO.name, raza = "Bulldog",
                    anioNacimiento = 2020, peso = 22.5, sexo = Sexo.MACHO.name),
                Mascota(duenoId = 2, nombre = "Michi", especie = Especie.GATO.name, raza = "Mestizo",
                    anioNacimiento = 2024, peso = 3.8, sexo = Sexo.HEMBRA.name)
            ))

            val atendida = EstadoCita.ATENDIDA.name
            db.citaDao().insertarTodos(listOf(
                Cita(mascotaId = 1, servicioId = 3, fecha = dia(-218), hora = "10:00", precio = 50.0,
                    estado = atendida, diagnostico = "Higiene general",
                    tratamiento = "Baño con champú hipoalergénico", pesoRegistrado = 4.0),
                Cita(mascotaId = 1, servicioId = 1, fecha = dia(-108), hora = "11:00", precio = 60.0,
                    estado = atendida, diagnostico = "Control de rutina, sin hallazgos",
                    tratamiento = "Mantener dieta actual", pesoRegistrado = 4.2),
                Cita(mascotaId = 1, servicioId = 2, fecha = dia(-7), hora = "09:30", precio = 45.0,
                    estado = atendida, diagnostico = "Vacuna triple felina",
                    tratamiento = "Refuerzo en un año", pesoRegistrado = 4.3),

                Cita(mascotaId = 2, servicioId = 2, fecha = dia(-360), hora = "10:00", precio = 45.0,
                    estado = atendida, diagnostico = "Vacuna óctuple aplicada sin reacciones",
                    tratamiento = "Refuerzo en un año", pesoRegistrado = 22.8),
                Cita(mascotaId = 2, servicioId = 1, fecha = dia(-20), hora = "15:00", precio = 60.0,
                    estado = atendida, diagnostico = "Gastritis por cambio de alimento",
                    tratamiento = "Dieta blanda 5 días", pesoRegistrado = 24.5),

                Cita(mascotaId = 3, servicioId = 3, fecha = dia(-17), hora = "10:30", precio = 50.0,
                    estado = atendida, diagnostico = "Piel sana, sin pulgas",
                    tratamiento = "Baño mensual", pesoRegistrado = 22.5),

                Cita(mascotaId = 4, servicioId = 2, fecha = dia(-5), hora = "16:00", precio = 45.0,
                    estado = EstadoCita.CANCELADA.name),

                Cita(mascotaId = 4, servicioId = 3, fecha = dia(0), hora = "16:30", precio = 50.0,
                    estado = EstadoCita.CONFIRMADA.name),
                Cita(mascotaId = 3, servicioId = 1, fecha = dia(0), hora = "17:30", precio = 60.0,
                    estado = EstadoCita.PENDIENTE.name, nota = "Cojea de la pata trasera"),

                Cita(mascotaId = 1, servicioId = 2, fecha = dia(2), hora = "10:30", precio = 45.0,
                    estado = EstadoCita.CONFIRMADA.name),
                Cita(mascotaId = 4, servicioId = 1, fecha = dia(2), hora = "11:30", precio = 60.0,
                    estado = EstadoCita.CONFIRMADA.name),
                Cita(mascotaId = 2, servicioId = 1, fecha = dia(4), hora = "16:00", precio = 60.0,
                    estado = EstadoCita.PENDIENTE.name),
                Cita(mascotaId = 2, servicioId = 3, fecha = dia(5), hora = "09:30", precio = 50.0,
                    estado = EstadoCita.PENDIENTE.name)
            ))
        }
    }
}