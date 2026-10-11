package com.example.canasta_frontend.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuario_grupos")
data class UsuarioGrupoEntity(
    @PrimaryKey val id_usuario_grupo: Long,
    val id_usuario: Long,
    val id_grupo: Long,
    val rol: String,
    val fecha_ingreso: String
)
