package com.example.aplicacionbiscotti.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sesion")
data class Sesion(
    @PrimaryKey
    val id: Int = 1,
    val usuarioId: Int,
    val nombreUsuario: String,
    val esAdmin: Boolean
)