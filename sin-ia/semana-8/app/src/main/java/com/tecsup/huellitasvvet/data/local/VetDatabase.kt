package com.tecsup.huellitasvvet.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tecsup.huellitasvvet.data.local.dao.CitaDao
import com.tecsup.huellitasvvet.data.local.dao.MascotaDao
import com.tecsup.huellitasvvet.data.local.dao.ServicioDao
import com.tecsup.huellitasvvet.data.local.dao.UsuarioDao
import com.tecsup.huellitasvvet.data.local.entity.Cita
import com.tecsup.huellitasvvet.data.local.entity.Mascota
import com.tecsup.huellitasvvet.data.local.entity.Servicio
import com.tecsup.huellitasvvet.data.local.entity.Usuario

@Database(
    entities = [Servicio::class, Usuario::class, Mascota::class, Cita::class],
    version = 1,
    exportSchema = false
)
abstract class VetDatabase : RoomDatabase(){
    abstract fun servicioDao(): ServicioDao
    abstract fun usuarioDao(): UsuarioDao
    abstract fun mascotaDao(): MascotaDao
    abstract fun citaDao(): CitaDao

    companion object {
        @Volatile private var INSTANCE: VetDatabase? = null
        fun obtener(context: Context): VetDatabase = INSTANCE ?: synchronized(this){
            INSTANCE ?: Room.databaseBuilder(
                context.applicationContext,
                VetDatabase::class.java,
                "huellitasvvet.db"
            ).build().also { INSTANCE = it }
        }
    }
}


