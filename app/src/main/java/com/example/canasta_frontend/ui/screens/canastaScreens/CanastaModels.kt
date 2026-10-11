package com.example.canasta_frontend.ui.screens.canastaScreens

data class ProductoModel(
    val id: Long,
    val nombre: String,
    val descripcion: String? = null,
    val prioridad: String = "secundario", // "esencial", "prioritario", "secundario"
    val cantidad: Double = 1.0,
    val precioUnitario: Int = 0,
    val codigoProducto: String? = null,
    val estado: String = "pendiente" // "pendiente", "comprado", "omitido"
)

data class ListaModel(
    val id: Long,
    val nombre: String,
    val descripcion: String? = null,
    val estado: String = "activa", // "activa", "completada", "archivada"
    val creador: String = "Usuario",
    val productos: List<ProductoModel> = emptyList()
)
