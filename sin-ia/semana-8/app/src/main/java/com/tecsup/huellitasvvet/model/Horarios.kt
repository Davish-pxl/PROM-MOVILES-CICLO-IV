package com.tecsup.huellitasvvet.model
import java.time.LocalTime

object Horarios {
    val todos: List<String> = List(18) { i ->
        LocalTime.of(9, 0).plusMinutes(30L * i).toString()
    }
}