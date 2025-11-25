package com.example.aplicacionbiscotti.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "carrito")
data class Carrito(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val productoId: Int,
    val usuarioId: Int,
    val cantidad: Int = 1,
    val nombreProducto: String = "",
    val precioUnitario: Double = 0.0,
    val imagenUrl: String = ""
)