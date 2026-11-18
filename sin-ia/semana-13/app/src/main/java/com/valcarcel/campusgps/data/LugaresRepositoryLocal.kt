package com.valcarcel.campusgps.data

import com.valcarcel.campusgps.model.Lugar

class LugaresRepositoryLocal : LugaresRepository {
    override fun obtenerLugares(): List<Lugar> = listOf(
        Lugar(1, "Biblioteca Central", "Estudio", -12.0437, -76.9536),
        Lugar(2, "Cafetería Campus", "Comida", -12.0447, -76.9521),
        Lugar(3, "Laboratorio de Cómputo", "Estudio", -12.0432, -76.9516),
        Lugar(4, "Auditorio Principal", "Eventos", -12.0457, -76.9541),
        Lugar(5, "Estacionamiento Norte", "Servicios", -12.0417, -76.9531)
    )
}