package com.example.canasta_frontend.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ofertas")
data class OfertaEntity(
    @PrimaryKey val id_oferta: Long,
    val id_producto_lista: Long,
    val nombre_comercio: String,
    val ubicacion: String? = null,
    val localidad: String? = null,
    val provincia: String? = null,
    val latitud: Double? = null,
    val longitud: Double? = null,
    val fecha_inicio: String,
    val fecha_fin: String,
    val porcentaje_descuento: Double? = null,
    val precio_original: Int? = null, // INTEGER centavos
    val precio_oferta: Int? = null, // INTEGER centavos
    val descripcion: String? = null,
    val fecha_creacion: String
)
