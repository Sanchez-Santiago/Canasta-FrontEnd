package com.example.canasta_frontend.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class UsuarioEntity(
    @PrimaryKey val id_usuario: Long,
    val nombre: String? = null,
    val email: String? = null,
    val contrasena: String? = null,
    val localidad: String? = null,
    val provincia: String? = null,
    val pais: String? = null,
    val domicilio: String? = null,
    val avatar_url: String? = null,
    val fecha_creacion: String? = null
)
