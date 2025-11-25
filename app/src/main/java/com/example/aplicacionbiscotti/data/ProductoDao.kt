package com.example.aplicacionbiscotti.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductoDao {
    @Insert
    suspend fun insertarProducto(producto: Producto)

    @Update
    suspend fun actualizarProducto(producto: Producto)

    @Delete
    suspend fun eliminarProducto(producto: Producto)

    @Query("SELECT * FROM producto")
    fun obtenerTodosLosProductos(): Flow<List<Producto>>

    @Query("SELECT * FROM producto WHERE id = :id")
    suspend fun obtenerProductoPorId(id: Int): Producto?
}