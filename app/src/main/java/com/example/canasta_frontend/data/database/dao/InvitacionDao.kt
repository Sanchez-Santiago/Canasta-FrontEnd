package com.example.canasta_frontend.data.database.dao

import androidx.room.*
import com.example.canasta_frontend.data.database.entity.InvitacionGrupoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InvitacionDao {
    @Query("SELECT * FROM invitacion_grupos WHERE id_usuario_destino = :idUsuario")
    fun getInvitacionesForUsuario(idUsuario: Long): Flow<List<InvitacionGrupoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvitaciones(invitaciones: List<InvitacionGrupoEntity>)

    @Query("DELETE FROM invitacion_grupos")
    suspend fun deleteAll()
}
