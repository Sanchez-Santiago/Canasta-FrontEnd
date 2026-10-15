package com.example.canasta_frontend.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "producto_listas")
data class ProductoListaEntity(
    @PrimaryKey val id_producto_lista: Long,
    val id_lista: Long,
    val id_usuario_creador: Long? = null,
    val nombre: String? = null,
    val descripcion: String? = null,
    val prioridad: String? = "secundario",
    val cantidad: Double = 1.0,
    val precio_estimado_unitario: Int? = null,
    val codigo_producto: String? = null,
    val estado: String? = "pendiente",
    val fecha_creacion: String? = null
)
