package com.valcarcel.campusgps.data

import com.valcarcel.campusgps.model.Lugar

interface LugaresRepository {
    fun obtenerLugares(): List<Lugar>
}