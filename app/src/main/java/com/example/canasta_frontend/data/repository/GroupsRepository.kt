package com.example.canasta_frontend.data.repository

import android.content.Context
import com.example.canasta_frontend.data.database.CanastaDatabase
import com.example.canasta_frontend.data.database.entity.GrupoEntity
import com.example.canasta_frontend.data.network.RetrofitClient
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
}
