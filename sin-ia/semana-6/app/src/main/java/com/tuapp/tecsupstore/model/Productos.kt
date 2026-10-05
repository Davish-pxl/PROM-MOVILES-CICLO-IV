package com.tuapp.tecsupstore.model

data class Productos(
    val id: Int,
    val nombre: String,
    val categoria: String,
    val precio: Double,
    val codigo: String,
    val stock: Int,
    val rating: Double,
    val descripcion: String
)

object ProductDataSource {
    val productos = listOf(
        Productos(
            id = 1,
            nombre = "Laptop Gamer Tecsup",
            categoria = "Tecnología",
            precio = 3499.90,
            codigo = "TEC-2024-001",
            stock = 12,
            rating = 4.8,
            descripcion = "Procesador de última generación con 16GB RAM y SSD de 512GB. Ideal para desarrollo y diseño."
        ),
        Productos(
            id = 2,
            nombre = "Mouse Inalámbrico Pro",
            categoria = "Accesorios",
            precio = 129.50,
            codigo = "TEC-2024-002",
            stock = 25,
            rating = 4.5,
            descripcion = "Diseño ergonómico con sensor óptico de alta precisión y batería de larga duración."
        ),
        Productos(
            id = 3,
            nombre = "Teclado Mecánico RGB",
            categoria = "Accesorios",
            precio = 289.00,
            codigo = "TEC-2024-003",
            stock = 8,
            rating = 4.7,
            descripcion = "Switches red silenciosos con retroiluminación personalizable y estructura de aluminio."
        ),
        Productos(
            id = 4,
            nombre = "Monitor 27'' 144Hz",
            categoria = "Monitores",
            precio = 899.00,
            codigo = "TEC-2024-004",
            stock = 15,
            rating = 4.9,
            descripcion = "Pantalla IPS Full HD con tiempo de respuesta de 1ms, ideal para gaming y programación."
        ),
        Productos(
            id = 5,
            nombre = "Audífonos Noise Cancelling",
            categoria = "Audio",
            precio = 450.00,
            codigo = "TEC-2024-005",
            stock = 20,
            rating = 4.6,
            descripcion = "Cancelación de ruido activa, micrófono integrado HD y hasta 30 horas de reproducción continua."
        )
    )

    fun getProductById(id: Int): Productos {
        return productos.find { it.id == id } ?: productos.first()
    }
}