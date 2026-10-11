package com.example.canasta_frontend.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movimientos")
data class MovimientoEntity(
    @PrimaryKey val id_movimiento: Long,
    val id_usuario_creador: Long,
    val id_grupo: Long,
    val nombre: String,
    val monto: Int, // INTEGER centavos
    val descripcion: String? = null,
    val tipo: String, // "ingreso" u "gasto"
    val categoria: String? = null,
    val foto_url: String? = null,
    val fecha_movimiento: String,
    val fecha_creacion: String
)
