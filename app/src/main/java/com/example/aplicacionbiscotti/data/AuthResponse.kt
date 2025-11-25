package com.example.aplicacionbiscotti.data

data class AuthResponse(
    val token: String?,
    val nombreUsuario: String?,
    val id: Int?,
    val rol: String?
)