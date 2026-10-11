package com.example.canasta_frontend.data.network

import org.json.JSONObject
import retrofit2.HttpException

fun parseBackendError(e: Exception): String {
    if (e is HttpException) {
        try {
            val body = e.response()?.errorBody()?.string()
            if (!body.isNullOrBlank()) {
                val json = JSONObject(body)
                val err = json.optString("error", "").ifBlank { json.optString("mensaje", "") }
                if (err.isNotBlank()) return err
            }
        } catch (_: Exception) { }
        return when (e.code()) {
            400 -> "Datos inválidos"
            401 -> "No autorizado, volvé a iniciar sesión"
            403 -> "No tenés permiso para esta acción"
            404 -> "No encontrado. Verificá los datos."
            409 -> "Conflicto: ya existe o ya fue procesado"
            else -> "Error ${e.code()}: ${e.message()}"
        }
    }
    return e.message ?: "Error de conexión"
}
