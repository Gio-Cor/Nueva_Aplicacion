package com.example.aplicacionbiscotti.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DetallePedidoDao {

    @Insert
    suspend fun insertarDetalle(detalle: DetallePedido)

    @Insert
    suspend fun insertarDetalles(detalles: List<DetallePedido>)

    @Query("SELECT * FROM detalles_pedido WHERE pedidoId = :pedidoId")
    fun obtenerDetallesPorPedido(pedidoId: Int): Flow<List<DetallePedido>>

    @Query("SELECT * FROM detalles_pedido WHERE pedidoId = :pedidoId")
    suspend fun obtenerDetallesPorPedidoDirecto(pedidoId: Int): List<DetallePedido>
}