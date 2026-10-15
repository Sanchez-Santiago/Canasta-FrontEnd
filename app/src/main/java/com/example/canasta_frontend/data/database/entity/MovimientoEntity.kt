package com.example.canasta_frontend.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movimientos")
data class MovimientoEntity(
    @PrimaryKey val id_movimiento: Long,
    val id_usuario_creador: Long? = null,
    val id_grupo: Long,
    val nombre: String? = null,
    val monto: Int = 0, // INTEGER centavos
    val descripcion: String? = null,
    val tipo: String? = null, // "ingreso" u "gasto"
    val categoria: String? = null,
    val foto_url: String? = null,
    val fecha_movimiento: String? = null,
    val fecha_creacion: String? = null
)
