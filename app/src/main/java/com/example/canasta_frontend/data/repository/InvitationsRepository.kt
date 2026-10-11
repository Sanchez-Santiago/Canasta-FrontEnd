package com.example.canasta_frontend.data.repository

import android.content.Context
import com.example.canasta_frontend.data.database.entity.InvitacionGrupoEntity
import com.example.canasta_frontend.data.network.RetrofitClient
import com.example.canasta_frontend.data.network.parseBackendError

sealed interface InviteResult {
    data object Success : InviteResult
    data class Failure(val message: String) : InviteResult
}

class InvitationsRepository(private val context: Context) {
    private fun api() = RetrofitClient.createService(context)

    suspend fun getRecibidas(): List<InvitacionGrupoEntity> {
        return try {
            val resp = api().getInvitacionesRecibidas()
            resp.data?.values?.flatten()?.filter { it.estado == "pendiente" } ?: emptyList()
        } catch (_: Exception) { emptyList() }
    }

    suspend fun responder(idInvitacion: Long, aceptar: Boolean): InviteResult {
        return try {
            val resp = api().responderInvitacion(idInvitacion, mapOf("estado" to if (aceptar) "aceptada" else "rechazada"))
            if (resp.ok) InviteResult.Success
            else InviteResult.Failure(resp.error ?: resp.mensaje ?: "No se pudo responder")
        } catch (e: Exception) {
            InviteResult.Failure(parseBackendError(e))
        }
    }

    suspend fun invitar(idGrupo: Long, emailDestino: String): InviteResult {
        return try {
            val resp = api().crearInvitacion(idGrupo, mapOf("email_destino" to emailDestino.lowercase().trim()))
            if (resp.ok) InviteResult.Success
            else InviteResult.Failure(resp.error ?: resp.mensaje ?: "No se pudo enviar")
        } catch (e: Exception) {
            InviteResult.Failure(parseBackendError(e))
        }
    }
}
