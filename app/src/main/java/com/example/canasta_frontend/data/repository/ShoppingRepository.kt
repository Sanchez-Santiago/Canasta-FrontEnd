package com.example.canasta_frontend.data.repository

import android.content.Context
import com.example.canasta_frontend.data.database.CanastaDatabase
import com.example.canasta_frontend.data.database.entity.ListaEntity
import com.example.canasta_frontend.data.database.entity.ProductoListaEntity
import com.example.canasta_frontend.data.network.RetrofitClient
import com.example.canasta_frontend.data.network.parseBackendError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

sealed interface RepoResult<out T> {
    data class Ok<T>(val value: T) : RepoResult<T>
    data class Err(val message: String) : RepoResult<Nothing>
}

class ShoppingRepository(private val context: Context) {
    private val api = RetrofitClient.createService(context)
    private val db = try { CanastaDatabase.getDatabase(context) } catch (_: Exception) { null }

    fun observeListas(idGrupo: Long): Flow<List<ListaEntity>> =
        db?.listaDao()?.getListasForGrupo(idGrupo) ?: flowOf(emptyList())

    fun observeProductos(idLista: Long): Flow<List<ProductoListaEntity>> =
        db?.productoListaDao()?.getProductosForLista(idLista) ?: flowOf(emptyList())

    suspend fun syncListas(idGrupo: Long) {
        try {
            val response = api.getListas(idGrupo)
            val remote = response.data?.listas ?: emptyList()
            if (remote.isNotEmpty()) {
                db?.listaDao()?.insertListas(remote)
            }
        } catch (_: Exception) { }
    }

    suspend fun syncProductos(idLista: Long) {
        try {
            val response = api.getProductos(idLista)
            val remote = response.data?.productos ?: emptyList()
            if (remote.isNotEmpty()) {
                db?.productoListaDao()?.insertProductos(remote)
            }
        } catch (_: Exception) { }
    }

    /** Crea lista en server y devuelve la entidad real (ID server). Null si falla. */
    suspend fun saveLista(idGrupo: Long, nombre: String, descripcion: String?): ListaEntity? {
        return try {
            val map = mutableMapOf("nombre" to nombre)
            if (!descripcion.isNullOrBlank()) map["descripcion"] = descripcion
            val resp = api.createLista(idGrupo, map)
            val created = resp.data?.lista
            if (created != null) {
                try { db?.listaDao()?.insertLista(created) } catch (_: Exception) { }
            }
            created
        } catch (_: Exception) { null }
    }

    /** Crea producto con payload mínimo que acepta el backend (sin IDs cliente). */
    suspend fun saveProducto(
        idLista: Long,
        nombre: String,
        descripcion: String?,
        prioridad: String,
        cantidad: Double,
        precioUnitario: Int?,
        codigoProducto: String?,
        estado: String = "pendiente"
    ): ProductoListaEntity? {
        return try {
            val map = mutableMapOf<String, Any?>(
                "nombre" to nombre,
                "cantidad" to cantidad,
                "prioridad" to prioridad,
                "estado" to estado
            )
            if (!descripcion.isNullOrBlank()) map["descripcion"] = descripcion
            if (precioUnitario != null) map["precio_estimado_unitario"] = precioUnitario
            if (!codigoProducto.isNullOrBlank()) map["codigo_producto"] = codigoProducto
            val resp = api.createProducto(idLista, map)
            val created = resp.data?.producto
            if (created != null) {
                try { db?.productoListaDao()?.insertProducto(created) } catch (_: Exception) { }
            }
            created
        } catch (_: Exception) { null }
    }

    suspend fun toggleProductoEstado(idProducto: Long, nuevoEstado: String): RepoResult<ProductoListaEntity> {
        return try {
            val resp = api.cambiarEstadoProducto(idProducto, mapOf("estado" to nuevoEstado))
            val p = resp.data?.producto ?: return RepoResult.Err("Sin respuesta del servidor")
            try { db?.productoListaDao()?.insertProducto(p) } catch (_: Exception) { }
            RepoResult.Ok(p)
        } catch (e: Exception) { RepoResult.Err(parseBackendError(e)) }
    }

    suspend fun updateProducto(idProducto: Long, body: Map<String, Any?>): RepoResult<ProductoListaEntity> {
        return try {
            val resp = api.updateProducto(idProducto, body)
            val p = resp.data?.producto ?: return RepoResult.Err("Sin respuesta del servidor")
            try { db?.productoListaDao()?.insertProducto(p) } catch (_: Exception) { }
            RepoResult.Ok(p)
        } catch (e: Exception) { RepoResult.Err(parseBackendError(e)) }
    }

    suspend fun deleteProducto(idProducto: Long): RepoResult<Unit> {
        return try {
            val resp = api.deleteProducto(idProducto)
            if (resp.isSuccessful) RepoResult.Ok(Unit) else RepoResult.Err("No se pudo eliminar (${resp.code()})")
        } catch (e: Exception) { RepoResult.Err(parseBackendError(e)) }
    }

    suspend fun updateLista(idLista: Long, nombre: String, descripcion: String?): RepoResult<ListaEntity> {
        return try {
            val map = mutableMapOf("nombre" to nombre)
            if (descripcion != null) map["descripcion"] = descripcion
            val resp = api.updateLista(idLista, map)
            val l = resp.data?.lista ?: return RepoResult.Err("Sin respuesta")
            try { db?.listaDao()?.insertLista(l) } catch (_: Exception) { }
            RepoResult.Ok(l)
        } catch (e: Exception) { RepoResult.Err(parseBackendError(e)) }
    }

    suspend fun deleteLista(idLista: Long): RepoResult<Unit> {
        return try {
            val resp = api.deleteLista(idLista)
            if (resp.isSuccessful) RepoResult.Ok(Unit) else RepoResult.Err("No se pudo eliminar (${resp.code()})")
        } catch (e: Exception) { RepoResult.Err(parseBackendError(e)) }
    }
}
