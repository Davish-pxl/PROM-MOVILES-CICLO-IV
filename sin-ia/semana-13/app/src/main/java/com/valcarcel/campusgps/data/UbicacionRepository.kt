package com.valcarcel.campusgps.data

import com.valcarcel.campusgps.model.Posicion

interface UbicacionRepository {
    suspend fun obtenerPosicion(): Result<Posicion>
}