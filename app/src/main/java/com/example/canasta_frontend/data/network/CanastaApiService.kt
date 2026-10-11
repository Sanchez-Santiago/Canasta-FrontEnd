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

    @GET("grupos/{id}/listas")
    suspend fun getListas(@Path("id") idGrupo: Long): ListasResponse

    @POST("grupos/{id}/listas")
    suspend fun createLista(@Path("id") idGrupo: Long, @Body lista: Map<String, String>): ListaDetailResponse

    @GET("listas/{id}/productos")
    suspend fun getProductos(@Path("id") idLista: Long): ProductosResponse

    @POST("listas/{id}/productos")
    suspend fun createProducto(@Path("id") idLista: Long, @Body producto: Map<String, @JvmSuppressWildcards Any?>): ProductoDetailResponse

    @GET("grupos/{id}/movimientos")
    suspend fun getMovimientos(@Path("id") idGrupo: Long): MovimientosResponse

    @POST("grupos/{id}/movimientos")
    suspend fun createMovimiento(@Path("id") idGrupo: Long, @Body movimiento: MovimientoEntity): MovimientoDetailResponse

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
