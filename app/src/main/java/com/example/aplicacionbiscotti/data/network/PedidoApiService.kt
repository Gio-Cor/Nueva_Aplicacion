package com.example.aplicacionbiscotti.data.network

import com.example.aplicacionbiscotti.data.Pedido
import retrofit2.Response
import retrofit2.http.*

interface PedidoApiService {
    @POST("/api/v1/pedidos")
    suspend fun crearPedido(@Body pedido: Pedido): Response<Pedido>

    @GET("/api/v1/pedidos")
    suspend fun obtenerTodosLosPedidos(): Response<List<Pedido>>

    @GET("/api/v1/pedidos/usuario/{usuarioId}")
    suspend fun obtenerPedidosPorUsuario(@Path("usuarioId") usuarioId: Int): Response<List<Pedido>>

    // Para cambiar solo el estado (Admin)
    @PUT("/api/v1/pedidos/{id}")
    suspend fun actualizarEstadoPedido(
        @Path("id") id: Int,
        @Query("estado") estado: String
    ): Response<Pedido>

    @GET("/api/v1/pedidos/{id}")
    suspend fun obtenerPedidoPorId(@Path("id") pedidoId: Int): Response<Pedido>

    @DELETE("/api/v1/pedidos/{id}")
    suspend fun borrarPedido(@Path("id") pedidoId: Int): Response<Void>
}