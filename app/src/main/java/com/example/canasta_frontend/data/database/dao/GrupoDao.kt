package com.example.canasta_frontend.data.database.dao

import androidx.room.*
import com.example.canasta_frontend.data.database.entity.GrupoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GrupoDao {
    @Query("SELECT * FROM grupos")
    fun getAllGrupos(): Flow<List<GrupoEntity>>

    @Query("SELECT * FROM grupos WHERE id_grupo = :id")
    suspend fun getGrupoById(id: Long): GrupoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGrupos(grupos: List<GrupoEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGrupo(grupo: GrupoEntity)

    @Query("DELETE FROM grupos")
    suspend fun deleteAll()
}
