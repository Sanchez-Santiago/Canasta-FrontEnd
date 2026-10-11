package com.example.canasta_frontend.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.canasta_frontend.data.database.dao.*
import com.example.canasta_frontend.data.database.entity.*

@Database(
    entities = [
        UsuarioEntity::class,
        GrupoEntity::class,
        UsuarioGrupoEntity::class,
        ListaEntity::class,
        ProductoListaEntity::class,
        MovimientoEntity::class,
        OfertaEntity::class,
        InvitacionGrupoEntity::class,
        NotificacionEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class CanastaDatabase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    abstract fun grupoDao(): GrupoDao
    abstract fun usuarioGrupoDao(): UsuarioGrupoDao
    abstract fun listaDao(): ListaDao
    abstract fun productoListaDao(): ProductoListaDao
    abstract fun movimientoDao(): MovimientoDao
    abstract fun ofertaDao(): OfertaDao
    abstract fun invitacionDao(): InvitacionDao
    abstract fun notificacionDao(): NotificacionDao

    companion object {
        @Volatile
        private var INSTANCE: CanastaDatabase? = null

        fun getDatabase(context: Context): CanastaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CanastaDatabase::class.java,
                    "canasta_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
