package com.example.aplicacionbiscotti.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PedidoDao {

    @Insert
    suspend fun insertarPedido(pedido: Pedido): Long

    @Update
    suspend fun actualizarPedido(pedido: Pedido)

    @Query("SELECT * FROM pedidos WHERE usuarioId = :usuarioId ORDER BY fecha DESC")
    fun obtenerPedidosPorUsuario(usuarioId: Int): Flow<List<Pedido>>

    @Query("SELECT * FROM pedidos ORDER BY fecha DESC")
    fun obtenerTodosPedidos(): Flow<List<Pedido>>

    @Query("SELECT * FROM pedidos WHERE id = :pedidoId")
    suspend fun obtenerPedidoPorId(pedidoId: Int): Pedido?

    @Query("SELECT * FROM pedidos WHERE numeroBoleta = :numeroBoleta")
    suspend fun obtenerPedidoPorBoleta(numeroBoleta: String): Pedido?
}