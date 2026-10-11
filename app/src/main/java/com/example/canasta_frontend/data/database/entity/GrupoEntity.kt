package com.example.canasta_frontend.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "grupos")
data class GrupoEntity(
    @PrimaryKey val id_grupo: Long,
    val nombre: String,
    val descripcion: String? = null,
    val categoria: String? = null,
    val tipo: String,
    val presupuesto_mensual: Double? = null,
    val rol: String? = null,
    val fecha_ingreso: String? = null,
    val fecha_creacion: String
)
