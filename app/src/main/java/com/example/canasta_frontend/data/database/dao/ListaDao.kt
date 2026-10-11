package com.example.canasta_frontend.data.database.dao

import androidx.room.*
import com.example.canasta_frontend.data.database.entity.ListaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ListaDao {
    @Query("SELECT * FROM listas WHERE id_grupo = :idGrupo")
    fun getListasForGrupo(idGrupo: Long): Flow<List<ListaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertListas(listas: List<ListaEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLista(lista: ListaEntity)

    @Query("DELETE FROM listas")
    suspend fun deleteAll()
}
