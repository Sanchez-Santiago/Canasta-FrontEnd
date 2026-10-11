package com.example.canasta_frontend.data.database.dao

import androidx.room.*
import com.example.canasta_frontend.data.database.entity.OfertaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OfertaDao {
    @Query("SELECT * FROM ofertas")
    fun getAllOfertas(): Flow<List<OfertaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOfertas(ofertas: List<OfertaEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOferta(oferta: OfertaEntity)

    @Query("DELETE FROM ofertas")
    suspend fun deleteAll()
}
