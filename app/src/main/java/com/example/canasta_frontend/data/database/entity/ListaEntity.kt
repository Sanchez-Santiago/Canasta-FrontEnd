package com.example.canasta_frontend.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "listas")
data class ListaEntity(
    @PrimaryKey val id_lista: Long,
    val id_grupo: Long,
    val id_usuario_creador: Long,
    val id_usuario_administrador: Long? = null,
    val nombre: String,
    val descripcion: String? = null,
    val estado: String,
    val fecha_creacion: String,
    val fecha_actualizacion: String
)
