package com.example.aplicacionbiscotti.data.repository

import com.example.aplicacionbiscotti.data.modelo.DetallePedido
import com.example.aplicacionbiscotti.data.network.RetrofitClient

class DetallePedidoRepository {
    private val apiService = RetrofitClient.detallePedidoService

    suspend fun obtenerDetallesPorPedido(pedidoId: Int) = apiService.obtenerDetallesPorPedido(pedidoId)
    suspend fun insertarDetalle(detalle: DetallePedido) = apiService.insertarDetalle(detalle)
    suspend fun insertarDetalles(detalles: List<DetallePedido>) = apiService.insertarDetalles(detalles)
    suspend fun borrarDetalle(id: Int) = apiService.borrarDetalle(id)
}
