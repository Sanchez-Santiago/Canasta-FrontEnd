package com.example.canasta_frontend.ui.screens.ofertasScreens

data class OfertaModel(
    val id: Long,
    val productoNombre: String,
    val comercio: String,
    val localidad: String,
    val porcentajeDescuento: Double,
    val precioOriginal: Int,
    val precioOferta: Int,
    val prioridad: String,
    val fechaFin: String
)
