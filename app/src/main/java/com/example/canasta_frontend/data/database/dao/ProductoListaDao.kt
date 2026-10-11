package com.example.canasta_frontend.data.database.dao

import androidx.room.*
import com.example.canasta_frontend.data.database.entity.ProductoListaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductoListaDao {
    @Query("SELECT * FROM producto_listas WHERE id_lista = :idLista")
    fun getProductosForLista(idLista: Long): Flow<List<ProductoListaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProductos(productos: List<ProductoListaEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducto(producto: ProductoListaEntity)

    @Query("DELETE FROM producto_listas")
    suspend fun deleteAll()
}
