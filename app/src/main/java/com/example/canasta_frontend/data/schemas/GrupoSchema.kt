package com.example.canasta_frontend.data.schemas

data class GrupoSchema(
    val id_grupo: Long,
    val nombre: String,
    val descripcion: String? = null,
    val categoria: String? = null,
    val tipo: String,
    val rol: String? = null,
    val fecha_ingreso: String? = null,
    val fecha_creacion: String
)
