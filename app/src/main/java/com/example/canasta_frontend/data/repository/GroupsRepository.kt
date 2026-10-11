package com.example.canasta_frontend.data.repository

import android.content.Context
import com.example.canasta_frontend.data.database.CanastaDatabase
import com.example.canasta_frontend.data.database.entity.GrupoEntity
import com.example.canasta_frontend.data.network.RetrofitClient
import com.example.canasta_frontend.data.network.parseBackendError
import kotlinx.coroutines.flow.first

class GroupsRepository(private val context: Context) {
    private val api = RetrofitClient.createService(context)
    private val db = try { CanastaDatabase.getDatabase(context) } catch (_: Exception) { null }

    suspend fun getGrupos(): List<GrupoEntity> {
        return try {
            val response = api.getGrupos()
            val remote = response.data?.grupos ?: emptyList()
            if (remote.isNotEmpty()) {
                try { db?.grupoDao()?.insertGrupos(remote) } catch (_: Exception) { }
            }
            remote
        } catch (_: Exception) {
            try { db?.grupoDao()?.getAllGrupos()?.first() ?: emptyList() } catch (_: Exception) { emptyList() }
        }
    }

    suspend fun createGrupo(nombre: String, descripcion: String? = null, categoria: String? = null): GrupoEntity? {
        return try {
            val map = mutableMapOf("nombre" to nombre)
            if (!descripcion.isNullOrBlank()) map["descripcion"] = descripcion
            if (!categoria.isNullOrBlank()) map["categoria"] = categoria
            val response = api.createGrupo(map)
            val created = response.data?.grupo
            if (created != null) {
                try { db?.grupoDao()?.insertGrupo(created) } catch (_: Exception) { }
            }
            created
        } catch (_: Exception) {
            null
        }
    }

    suspend fun updateGrupo(id: Long, nombre: String, descripcion: String?, categoria: String?): RepoResult<GrupoEntity> {
        return try {
            val map = mutableMapOf("nombre" to nombre)
            if (descripcion != null) map["descripcion"] = descripcion
            if (categoria != null) map["categoria"] = categoria
            val resp = api.updateGrupo(id, map)
            val g = resp.data?.grupo ?: return RepoResult.Err("Sin respuesta")
            try { db?.grupoDao()?.insertGrupo(g) } catch (_: Exception) { }
            RepoResult.Ok(g)
        } catch (e: Exception) { RepoResult.Err(parseBackendError(e)) }
    }

    suspend fun abandonarGrupo(idGrupo: Long): RepoResult<Unit> {
        return try {
            val resp = api.abandonarGrupo(idGrupo)
            if (resp.ok) RepoResult.Ok(Unit) else RepoResult.Err(resp.error ?: "No se pudo abandonar")
        } catch (e: Exception) { RepoResult.Err(parseBackendError(e)) }
    }

    suspend fun expulsarMiembro(idGrupo: Long, idUsuario: Long): RepoResult<Unit> {
        return try {
            val resp = api.expulsarMiembro(idGrupo, idUsuario)
            if (resp.ok) RepoResult.Ok(Unit) else RepoResult.Err(resp.error ?: "No se pudo expulsar")
        } catch (e: Exception) { RepoResult.Err(parseBackendError(e)) }
    }
}
