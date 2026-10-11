package com.example.canasta_frontend.ui.screens.historialScreens

data class MovimientoModel(
    val id: Long,
    val nombre: String,
    val descripcion: String,
    val monto: Int,
    val tipo: String, // "ingreso" o "gasto"
    val categoria: String,
    val usuario: String,
    val fecha: String
)
