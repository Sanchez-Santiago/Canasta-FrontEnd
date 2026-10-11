package com.example.canasta_frontend.data.network

import com.example.canasta_frontend.data.database.entity.*

data class AuthRequest(
    val email: String,
    val contrasena: String
)

data class RegisterRequest(
    val nombre: String,
    val email: String,
    val contrasena: String,
    val localidad: String? = null,
    val provincia: String? = null,
    val pais: String? = null,
    val domicilio: String? = null
)

data class UpdateProfileRequest(
    val nombre: String? = null,
    val localidad: String? = null,
    val provincia: String? = null,
    val pais: String? = null,
    val domicilio: String? = null
)

data class UpdatePasswordRequest(
    val contrasena_actual: String,
    val contrasena_nueva: String
)

data class ErrorDetail(
    val message: String? = null
)

data class ApiResponse<T>(
    val ok: Boolean,
    val data: T? = null,
    val error: String? = null,
    val mensaje: String? = null,
    val detalles: List<ErrorDetail>? = null
)

data class AuthData(
    val usuario: UsuarioEntity,
    val token: String
)

data class YoData(
    val usuario: UsuarioEntity
)

data class GruposData(
    val grupos: List<GrupoEntity>
)

data class GrupoDetailData(
    val grupo: GrupoEntity
)

data class ListasData(
    val listas: List<ListaEntity>
)

data class ListaDetailData(
    val lista: ListaEntity
)

data class ProductosData(
    val productos: List<ProductoListaEntity>
)

data class ProductoDetailData(
    val producto: ProductoListaEntity
)

data class MovimientosData(
    val movimientos: List<MovimientoEntity>
)

data class MovimientoDetailData(
    val movimiento: MovimientoEntity
)

data class OfertasData(
    val ofertas: List<OfertaEntity>
)

data class OfertaDetailData(
    val oferta: OfertaEntity
)

typealias AuthResponse = ApiResponse<AuthData>
typealias YoResponse = ApiResponse<YoData>
typealias GruposResponse = ApiResponse<GruposData>
typealias GrupoDetailResponse = ApiResponse<GrupoDetailData>
typealias ListasResponse = ApiResponse<ListasData>
typealias ListaDetailResponse = ApiResponse<ListaDetailData>
typealias ProductosResponse = ApiResponse<ProductosData>
typealias ProductoDetailResponse = ApiResponse<ProductoDetailData>
typealias MovimientosResponse = ApiResponse<MovimientosData>
typealias MovimientoDetailResponse = ApiResponse<MovimientoDetailData>
typealias OfertasResponse = ApiResponse<OfertasData>
typealias OfertaDetailResponse = ApiResponse<OfertaDetailData>
