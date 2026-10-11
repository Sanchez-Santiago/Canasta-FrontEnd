package com.example.canasta_frontend.data.repository

import android.content.Context
import com.example.canasta_frontend.data.database.entity.UsuarioEntity
import com.example.canasta_frontend.data.network.AuthRequest
import com.example.canasta_frontend.data.network.RegisterRequest
import com.example.canasta_frontend.data.network.RetrofitClient
import com.example.canasta_frontend.data.network.SecureTokenManager

class AuthRepository(private val context: Context) {
    private val api = RetrofitClient.createService(context)
    private val tokenManager = SecureTokenManager(context)

    suspend fun login(email: String, contrasena: String): Result<UsuarioEntity> {
        return try {
            val response = api.login(AuthRequest(email, contrasena))
            val token = response.data?.token
            val user = response.data?.usuario
            if (response.ok && !token.isNullOrBlank() && user != null) {
                tokenManager.saveToken(token)
                Result.success(user)
            } else {
                Result.failure(Exception(response.error ?: response.mensaje ?: "Error en autenticación"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun registrar(nombre: String, email: String, contrasena: String, localidad: String? = null): Result<UsuarioEntity> {
        return try {
            val response = api.registrar(RegisterRequest(nombre, email, contrasena, localidad))
            val token = response.data?.token
            val user = response.data?.usuario
            if (response.ok && !token.isNullOrBlank() && user != null) {
                tokenManager.saveToken(token)
                Result.success(user)
            } else {
                Result.failure(Exception(response.error ?: response.mensaje ?: "Error en registro"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getYo(): Result<UsuarioEntity> {
        return try {
            val response = api.getYo()
            val user = response.data?.usuario
            if (response.ok && user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("No se pudo obtener usuario"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        tokenManager.clearToken()
    }

    fun hasToken(): Boolean {
        return !tokenManager.getToken().isNullOrEmpty()
    }
}
