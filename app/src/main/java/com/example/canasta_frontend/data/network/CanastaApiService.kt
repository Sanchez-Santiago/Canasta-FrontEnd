package com.example.canasta_frontend.data.network

import com.example.canasta_frontend.data.database.entity.*
import retrofit2.Response
import retrofit2.http.*

interface CanastaApiService {
    @POST("auth/registrar")
    suspend fun registrar(@Body request: RegisterRequest): AuthResponse

    @POST("auth/login")
    suspend fun login(@Body request: AuthRequest): AuthResponse

    @GET("auth/yo")
    suspend fun getYo(): YoResponse

    @PUT("auth/yo")
    suspend fun updateYo(@Body request: UpdateProfileRequest): YoResponse

    @PATCH("auth/contrasena")
    suspend fun updateContrasena(@Body request: UpdatePasswordRequest): ApiResponse<Map<String, Boolean>>

    @POST("auth/logout")
    suspend fun logout(): Response<Void>

    @GET("grupos")
    suspend fun getGrupos(): GruposResponse

    @POST("grupos/grupal")
    suspend fun createGrupo(@Body grupo: Map<String, String>): GrupoDetailResponse

    @GET("grupos/{id}")
    suspend fun getGrupoById(@Path("id") id: Long): GrupoDetailResponse

    @PUT("grupos/{id}")
    suspend fun updateGrupo(@Path("id") id: Long, @Body body: Map<String, String>): GrupoDetailResponse

    @DELETE("grupos/{idGrupo}/miembros/yo")
    suspend fun abandonarGrupo(@Path("idGrupo") id: Long): ApiResponse<Map<String, Boolean>>

    @DELETE("grupos/{idGrupo}/miembros/{idUsuario}")
    suspend fun expulsarMiembro(@Path("idGrupo") idGrupo: Long, @Path("idUsuario") idUsuario: Long): ApiResponse<Map<String, Boolean>>

    @GET("grupos/{id}/listas")
    suspend fun getListas(@Path("id") idGrupo: Long): ListasResponse

    @POST("grupos/{id}/listas")
    suspend fun createLista(@Path("id") idGrupo: Long, @Body lista: Map<String, String>): ListaDetailResponse

    @GET("listas/{id}/productos")
    suspend fun getProductos(@Path("id") idLista: Long): ProductosResponse

    @POST("listas/{id}/productos")
    suspend fun createProducto(@Path("id") idLista: Long, @Body producto: Map<String, @JvmSuppressWildcards Any?>): ProductoDetailResponse

    @PUT("productos-lista/{id}")
    suspend fun updateProducto(@Path("id") idProducto: Long, @Body body: Map<String, @JvmSuppressWildcards Any?>): ProductoDetailResponse

    @PATCH("productos-lista/{id}/estado")
    suspend fun cambiarEstadoProducto(@Path("id") idProducto: Long, @Body body: Map<String, String>): ProductoDetailResponse

    @DELETE("productos-lista/{id}")
    suspend fun deleteProducto(@Path("id") idProducto: Long): Response<Void>

    @PUT("listas/{id}")
    suspend fun updateLista(@Path("id") idLista: Long, @Body body: Map<String, String>): ListaDetailResponse

    @DELETE("listas/{id}")
    suspend fun deleteLista(@Path("id") idLista: Long): Response<Void>

    @GET("grupos/{id}/movimientos")
    suspend fun getMovimientos(@Path("id") idGrupo: Long): MovimientosResponse

    @POST("grupos/{id}/movimientos")
    suspend fun createMovimiento(@Path("id") idGrupo: Long, @Body movimiento: MovimientoEntity): MovimientoDetailResponse

    @PUT("movimientos/{id}")
    suspend fun updateMovimiento(@Path("id") idMov: Long, @Body body: Map<String, @JvmSuppressWildcards Any?>): MovimientoDetailResponse

    @DELETE("movimientos/{id}")
    suspend fun deleteMovimiento(@Path("id") idMov: Long): Response<Void>

    @GET("grupos/{idGrupo}/miembros")
    suspend fun getMiembros(@Path("idGrupo") idGrupo: Long): ApiResponse<Map<String, List<Map<String, Any?>>>>

    @GET("grupos/{idGrupo}/invitaciones")
    suspend fun getInvitaciones(@Path("idGrupo") idGrupo: Long): ApiResponse<Map<String, List<com.example.canasta_frontend.data.database.entity.InvitacionGrupoEntity>>>

    @POST("grupos/{idGrupo}/invitaciones")
    suspend fun crearInvitacion(@Path("idGrupo") idGrupo: Long, @Body body: Map<String, String>): ApiResponse<Map<String, com.example.canasta_frontend.data.database.entity.InvitacionGrupoEntity>>

    @GET("invitaciones/recibidas")
    suspend fun getInvitacionesRecibidas(): ApiResponse<Map<String, List<com.example.canasta_frontend.data.database.entity.InvitacionGrupoEntity>>>

    @PATCH("invitaciones/{idInvitacion}/responder")
    suspend fun responderInvitacion(@Path("idInvitacion") id: Long, @Body body: Map<String, String>): ApiResponse<Map<String, com.example.canasta_frontend.data.database.entity.InvitacionGrupoEntity>>

    @DELETE("invitaciones/{idInvitacion}")
    suspend fun eliminarInvitacion(@Path("idInvitacion") id: Long): retrofit2.Response<Void>

    @GET("grupos/{idGrupo}/notificaciones")
    suspend fun getNotificaciones(@Path("idGrupo") idGrupo: Long): ApiResponse<Map<String, List<com.example.canasta_frontend.data.database.entity.NotificacionEntity>>>

    @GET("ofertas")
    suspend fun getOfertas(): OfertasResponse

    @POST("ofertas")
    suspend fun createOferta(@Body oferta: OfertaEntity): OfertaDetailResponse
}
