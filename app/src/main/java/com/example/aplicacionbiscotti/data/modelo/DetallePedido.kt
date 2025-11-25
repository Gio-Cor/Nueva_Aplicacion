package com.example.aplicacionbiscotti.data.modelo

data class DetallePedido(
    val id: Int? = null,
    val pedidoId: Int,
    val producto: String,
    val cantidad: Int,
    val precioUnitario: Double
)
