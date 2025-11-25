package com.example.aplicacionbiscotti.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class Usuario(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombreUsuario: String,
    val contrasena: String,
    val email: String,
    val esAdmin: Boolean = false,
    val fotoPerfilRuta: String = ""
)
