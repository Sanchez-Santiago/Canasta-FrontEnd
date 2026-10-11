package com.example.canasta_frontend.data.database.dao

import androidx.room.*
import com.example.canasta_frontend.data.database.entity.UsuarioEntity

@Dao
interface UsuarioDao {
    @Query("SELECT * FROM usuarios WHERE id_usuario = :id")
    suspend fun getUsuarioById(id: Long): UsuarioEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsuario(usuario: UsuarioEntity)

    @Query("DELETE FROM usuarios")
    suspend fun deleteAll()
}
