package com.example.aplicacionbiscotti.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "detalles_pedido")
data class DetallePedido(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val pedidoId: Int,
    val productoId: Int,
    val nombreProducto: String,
    val cantidad: Int,
    val precioUnitario: Double,
    val subtotal: Double
)