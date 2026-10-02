package com.tuapp.navlab.model

data class Student(
    val id: Int,
    val name: String,
    val career: String,
    val email: String,
    val phone: String,
    val studentId: String,
    val faculty: String,
    val cycle: String,
    val bio: String
)

object StudentDataSource {
    val students = listOf(
        Student(
            id = 1,
            name = "David Valcarcel",
            career = "Diseño y Desarrollo de Software",
            email = "david.valcarcel@tecsup.edu.pe",
            phone = "+51 987 654 321",
            studentId = "2024-0001",
            faculty = "Tecnología Digital",
            cycle = "IV Ciclo",
            bio = "Estudiante destacado con interés en desarrollo Android."
        ),
        Student(
            id = 2,
            name = "María García",
            career = "Arquitectura",
            email = "maria.garcia@tecsup.edu.pe",
            phone = "+51 912 345 678",
            studentId = "2024-0002",
            faculty = "Arquitectura y Diseño",
            cycle = "IV Ciclo",
            bio = "Apasionada por el diseño sostenible y modelado 3D."
        ),
        Student(
            id = 3,
            name = "Carlos Pérez",
            career = "Medicina",
            email = "carlos.perez@tecsup.edu.pe",
            phone = "+51 923 456 789",
            studentId = "2024-0003",
            faculty = "Ciencias de la Salud",
            cycle = "IV Ciclo",
            bio = "Investigador en biotecnología y salud digital."
        ),
        Student(
            id = 4,
            name = "Ana López",
            career = "Derecho",
            email = "ana.lopez@tecsup.edu.pe",
            phone = "+51 934 567 890",
            studentId = "2024-0004",
            faculty = "Ciencias Jurídicas",
            cycle = "IV Ciclo",
            bio = "Especialista en derecho informático y propiedad intelectual."
        ),
        Student(
            id = 5,
            name = "Luis Ramírez",
            career = "Administración",
            email = "luis.ramirez@tecsup.edu.pe",
            phone = "+51 945 678 901",
            studentId = "2024-0005",
            faculty = "Gestión y Negocios",
            cycle = "IV Ciclo",
            bio = "Emprendedor y entusiasta de metodologías ágiles."
        )
    )

    fun getStudentById(id: Int): Student {
        return students.find { it.id == id } ?: students.first()
    }
}
