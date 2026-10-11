package com.example.canasta_frontend.data.schemas

data class UsuarioSchema(
    val id_usuario: Long,
    val nombre: String,
    val email: String,
    val localidad: String? = null,
    val provincia: String? = null,
    val pais: String? = null,
    val domicilio: String? = null,
    val fecha_creacion: String
)
