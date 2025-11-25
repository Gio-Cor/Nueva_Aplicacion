package com.example.aplicacionbiscotti.data.network

import com.example.aplicacionbiscotti.data.modelo.DetallePedido
import retrofit2.Response
import retrofit2.http.*

interface DetallePedidoApiService {

    @GET("/api/v1/detallePedidos/{pedidoId}")
    suspend fun obtenerDetallesPorPedido(@Path("pedidoId") pedidoId: Int): Response<List<DetallePedido>>

    @POST("/api/v1/detallePedidos")
    suspend fun insertarDetalle(@Body detalle: DetallePedido): Response<DetallePedido>

    @POST("/api/v1/detallePedidos/lote")
    suspend fun insertarDetalles(@Body detalles: List<DetallePedido>): Response<List<DetallePedido>>

    @DELETE("/api/v1/detallePedidos/{id}")
    suspend fun borrarDetalle(@Path("id") id: Int): Response<Void>
}
