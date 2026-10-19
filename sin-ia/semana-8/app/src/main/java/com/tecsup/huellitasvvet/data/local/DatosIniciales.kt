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
    suspend fun sembrar (db: VetDatabase){
        if (db.usuarioDao().contar() >0) return

        db.withTransaction {
            db.servicioDao().insertarTodos(listOf(
                Servicio(nombre = "Consulta general", descripcion = "Revision completa de salud",
                   duracionMin = 30, precio = 60.0, icono = "heart" ),
                Servicio(nombre = "Vacunacion", descripcion = "Aplicacion de vacunas",
                    duracionMin = 30, precio = 45.0, icono = "syringe"),
                Servicio(nombre = "Baño y Corte", descripcion = "Baño, corte de pelo y uñas",
                   duracionMin = 60, precio = 50.0, icono = "scissors"),
                Servicio(nombre = "Desparasitacion", descripcion = "Control de parasitos",
                    duracionMin = 15, precio = 25.0, activo = false, icono = "syringe")
            ))
            db.usuarioDao().insertarTodos(listOf(
                Usuario(nombre = "Ana Garcia", correo = "ana@correo.test", clave = "1234", rol = Rol.CLIENTE.name),
                Usuario(nombre = "Luis Ramos", correo = "luis@correo.test", clave = "1234", rol = Rol.CLIENTE.name),
                Usuario(nombre = "Dra. Rojas", correo = "vet@huellitas.test", clave = "admin", rol = Rol.ADMIN.name)
            ))
            db.mascotaDao().insertarTodos(listOf(
                Mascota(duenoId = 1, nombre = "Luna", especie = Especie.PERRO.name, raza = "Labrador",
                    anioNacimiento = 2021, peso = 24.0, sexo = Sexo.HEMBRA.name),
                Mascota(duenoId = 1, nombre = "Max", especie = Especie.GATO.name, raza = "Siamés",
                    anioNacimiento = 2022, peso = 4.2, sexo = Sexo.MACHO.name),
                Mascota(duenoId = 2, nombre = "Rocky", especie = Especie.PERRO.name, raza = "Bulldog",
                    anioNacimiento = 2019, peso = 22.5, sexo = Sexo.MACHO.name),
                Mascota(duenoId = 2, nombre = "Michi", especie = Especie.GATO.name, raza = "Mestizo",
                    anioNacimiento = 2023, peso = 3.8, sexo = Sexo.HEMBRA.name)
            ))
            val atendida = EstadoCita.ATENDIDA.name
            db.citaDao().insertarTodos(listOf(
                Cita(mascotaId = 1, servicioId = 1, fecha = dia(-60), hora = "10:00", precio = 60.0,
                    estado = atendida, diagnostico = "Otitis leve en oido derecho",
                    tratamiento = "Gotas oticas por 7 dias", pesoRegistrado = 22.0),
                Cita(mascotaId = 1, servicioId = 2, fecha = dia(-30), hora = "11:00", precio = 45.0,
                    estado = atendida, diagnostico = "Vacuna octuple aplicada sin reacciones",
                    tratamiento = "Refuerzo en un año", pesoRegistrado = 23.1),
                Cita(mascotaId = 1, servicioId = 1, fecha = dia(-8), hora = "09:30", precio = 60.0,
                    estado = atendida, diagnostico = "Control general, buen estado de salud",
                    tratamiento = "Mantener dieta actual", pesoRegistrado = 24.0),
                Cita(mascotaId = 2, servicioId = 1, fecha = dia(-20), hora = "15:00", precio = 60.0,
                    estado = atendida, diagnostico = "Gastritis por cambio de alimento",
                    tratamiento = "Dieta blanda 5 dias", pesoRegistrado = 4.2),
                Cita(mascotaId = 3, servicioId = 3, fecha = dia(-12), hora = "10:30", precio = 50.0,
                    estado = atendida, diagnostico = "Piel sana, sin pulgas",
                    tratamiento = "Baño mensual", pesoRegistrado = 22.5),

                Cita(mascotaId = 4, servicioId = 2, fecha = dia(-5), hora = "16:00", precio = 45.0,
                    estado = EstadoCita.CANCELADA.name),

                Cita(mascotaId = 1, servicioId = 3, fecha = dia(0), hora = "16:30", precio = 50.0,
                    estado = EstadoCita.CONFIRMADA.name),
                Cita(mascotaId = 3, servicioId = 1, fecha = dia(0), hora = "17:30", precio = 60.0,
                    estado = EstadoCita.PENDIENTE.name, nota = "Cojea de la pata trasera"),

                Cita(mascotaId = 2, servicioId = 2, fecha = dia(1), hora = "10:00", precio = 45.0,
                    estado = EstadoCita.PENDIENTE.name),
                Cita(mascotaId = 4, servicioId = 1, fecha = dia(2), hora = "11:30", precio = 60.0,
                    estado = EstadoCita.CONFIRMADA.name),
                Cita(mascotaId = 1, servicioId = 1, fecha = dia(4), hora = "09:00", precio = 60.0,
                    estado = EstadoCita.PENDIENTE.name)
            ))
        }
    }
}
