package com.example.canasta_frontend.data.schemas

data class MovimientoSchema(
    val id_movimiento: Long,
    val id_usuario_creador: Long,
    val id_grupo: Long,
    val nombre: String,
    val monto: Int,
    val descripcion: String? = null,
    val tipo: String,
    val categoria: String? = null,
    val foto_url: String? = null,
    val fecha_movimiento: String,
    val fecha_creacion: String
)
