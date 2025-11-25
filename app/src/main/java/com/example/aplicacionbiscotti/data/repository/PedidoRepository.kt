package com.example.aplicacionbiscotti.data.repository

import com.example.aplicacionbiscotti.data.Pedido
import com.example.aplicacionbiscotti.data.network.RetrofitClient
import retrofit2.Response

class PedidoRepository {
    private val apiService = RetrofitClient.pedidoService

    suspend fun crearPedido(pedido: Pedido): Response<Pedido> {
        return apiService.crearPedido(pedido)
    }

    suspend fun obtenerTodosLosPedidos(): Response<List<Pedido>> {
        return apiService.obtenerTodosLosPedidos()
    }

    suspend fun obtenerPedidosPorUsuario(usuarioId: Int): Response<List<Pedido>> {
        return apiService.obtenerPedidosPorUsuario(usuarioId)
    }

    suspend fun actualizarEstadoPedido(id: Int, estado: String): Response<Pedido> {
        return apiService.actualizarEstadoPedido(id, estado)
    }
}