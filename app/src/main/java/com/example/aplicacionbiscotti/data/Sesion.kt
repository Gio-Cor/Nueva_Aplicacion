package com.example.aplicacionbiscotti.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sesion")
data class Sesion(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val usuarioId: Int,
    val nombreUsuario: String,
    val email: String = "",
    val esAdmin: Boolean,
    val fotoPerfilRuta: String = "",
    val token: String = ""
)