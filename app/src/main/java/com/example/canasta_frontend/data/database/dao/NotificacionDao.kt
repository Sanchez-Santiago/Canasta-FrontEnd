package com.example.canasta_frontend.data.database.dao

import androidx.room.*
import com.example.canasta_frontend.data.database.entity.NotificacionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificacionDao {
    @Query("SELECT * FROM notificaciones WHERE id_grupo = :idGrupo ORDER BY fecha_creacion DESC")
    fun getNotificacionesForGrupo(idGrupo: Long): Flow<List<NotificacionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotificaciones(notificaciones: List<NotificacionEntity>)

    @Query("DELETE FROM notificaciones")
    suspend fun deleteAll()
}
