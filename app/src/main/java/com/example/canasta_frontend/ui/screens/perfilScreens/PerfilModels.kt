package com.example.canasta_frontend.ui.screens.perfilScreens

data class MiembroModel(
    val id: Long? = null,
    val nombre: String,
    val email: String,
    val rol: String // "administrador" o "miembro"
)
