package com.example.canasta_frontend.data.repository

import android.content.Context
import com.example.canasta_frontend.data.database.CanastaDatabase
import com.example.canasta_frontend.data.database.entity.MovimientoEntity
import com.example.canasta_frontend.data.network.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class MovementsRepository(private val context: Context) {
    private val api = RetrofitClient.createService(context)
    private val db = try { CanastaDatabase.getDatabase(context) } catch (_: Exception) { null }

    fun observeMovimientos(idGrupo: Long): Flow<List<MovimientoEntity>> =
        db?.movimientoDao()?.getMovimientosForGrupo(idGrupo) ?: flowOf(emptyList())

    suspend fun syncMovimientos(idGrupo: Long) {
        try {
            val response = api.getMovimientos(idGrupo)
            val remote = response.data?.movimientos ?: emptyList()
            if (remote.isNotEmpty()) {
                db?.movimientoDao()?.insertMovimientos(remote)
            }
        } catch (_: Exception) { }
    }

    suspend fun saveMovimiento(movimiento: MovimientoEntity) {
        try { db?.movimientoDao()?.insertMovimiento(movimiento) } catch (_: Exception) { }
        try {
            val resp = api.createMovimiento(movimiento.id_grupo, movimiento)
            val created = resp.data?.movimiento
            if (created != null) {
                try { db?.movimientoDao()?.insertMovimiento(created) } catch (_: Exception) { }
            }
        } catch (_: Exception) { }
    }
}
