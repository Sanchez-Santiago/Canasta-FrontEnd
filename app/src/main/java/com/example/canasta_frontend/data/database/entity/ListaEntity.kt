package com.example.canasta_frontend.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "listas")
data class ListaEntity(
    @PrimaryKey val id_lista: Long,
    val id_grupo: Long,
    val id_usuario_creador: Long? = null,
    val id_usuario_administrador: Long? = null,
    val nombre: String? = null,
    val descripcion: String? = null,
    val estado: String? = null,
    val fecha_creacion: String? = null,
    val fecha_actualizacion: String? = null
)
