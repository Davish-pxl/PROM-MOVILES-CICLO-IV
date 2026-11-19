package com.valcarcel.campusgps.data

import com.valcarcel.campusgps.model.Posicion

interface UbicacionRepository {
    suspend fun obtenerPosicion(): Result<Posicion>
    fun playServicesDisponible(): Boolean
}
class UbicacionNoDisponibleException :
    Exception("No se pudo obtener la ubicación. Activa el GPS e intenta de nuevo")