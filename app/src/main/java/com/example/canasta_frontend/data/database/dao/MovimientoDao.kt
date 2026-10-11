package com.example.canasta_frontend.data.database.dao

import androidx.room.*
import com.example.canasta_frontend.data.database.entity.MovimientoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MovimientoDao {
    @Query("SELECT * FROM movimientos WHERE id_grupo = :idGrupo ORDER BY fecha_movimiento DESC")
    fun getMovimientosForGrupo(idGrupo: Long): Flow<List<MovimientoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovimientos(movimientos: List<MovimientoEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovimiento(movimiento: MovimientoEntity)

    @Query("DELETE FROM movimientos")
    suspend fun deleteAll()
}
