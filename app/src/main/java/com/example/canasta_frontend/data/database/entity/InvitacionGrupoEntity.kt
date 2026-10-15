package com.example.canasta_frontend.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "invitacion_grupos")
data class InvitacionGrupoEntity(
    @PrimaryKey val id_invitacion: Long,
    val id_usuario_origen: Long? = null,
    val id_usuario_destino: Long? = null,
    val id_grupo: Long,
    val estado: String? = null,
    val fecha_creacion: String? = null,
    val fecha_respuesta: String? = null
)
