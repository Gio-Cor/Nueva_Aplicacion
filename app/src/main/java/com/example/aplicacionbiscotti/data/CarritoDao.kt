package com.example.aplicacionbiscotti.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CarritoDao {

    @Insert
    suspend fun agregarAlCarrito(carrito: Carrito)

    @Query("SELECT * FROM carrito WHERE usuarioId = :usuarioId")
    fun obtenerCarritoPorUsuario(usuarioId: Int): Flow<List<Carrito>>

    @Query("DELETE FROM carrito WHERE id = :carritoId")
    suspend fun eliminarDelCarrito(carritoId: Int)

    @Query("DELETE FROM carrito WHERE usuarioId = :usuarioId")
    suspend fun vaciarCarrito(usuarioId: Int)

    @Query("UPDATE carrito SET cantidad = :cantidad WHERE id = :carritoId")
    suspend fun actualizarCantidad(carritoId: Int, cantidad: Int)
}