package com.example.canasta_frontend.data.database.dao

import androidx.room.*
import com.example.canasta_frontend.data.database.entity.UsuarioGrupoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioGrupoDao {
    @Query("SELECT * FROM usuario_grupos WHERE id_usuario = :idUsuario")
    fun getUsuarioGrupos(idUsuario: Long): Flow<List<UsuarioGrupoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsuarioGrupos(items: List<UsuarioGrupoEntity>)

    @Query("DELETE FROM usuario_grupos")
    suspend fun deleteAll()
}
