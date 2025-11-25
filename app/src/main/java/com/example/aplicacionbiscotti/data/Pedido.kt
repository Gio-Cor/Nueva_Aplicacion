package com.example.aplicacionbiscotti.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pedidos")
data class Pedido(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val usuarioId: Int,
    val nombreUsuario: String,
    val fecha: Long = System.currentTimeMillis(),
    val total: Double,
    val estado: String = "Pendiente",
    val numeroBoleta: String = "",
    val metodoPago: String = "Efectivo"
)