package com.example.canasta_frontend.data.schemas

data class ListaSchema(
    val id_lista: Long,
    val id_grupo: Long,
    val id_usuario_creador: Long,
    val nombre: String,
    val descripcion: String? = null,
    val estado: String = "activa",
    val fecha_creacion: String,
    val fecha_actualizacion: String
)
