package com.example.canasta_frontend.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notificaciones")
data class NotificacionEntity(
    @PrimaryKey val id_notificacion: Long,
    val id_grupo: Long,
    val tipo: String,
    val titulo: String,
    val descripcion: String? = null,
    val leida: Boolean = false,
    val fecha_creacion: String
)
