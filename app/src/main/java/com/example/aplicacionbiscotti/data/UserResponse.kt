package com.example.aplicacionbiscotti.data

import com.google.gson.annotations.SerializedName

data class UserResponse(
    val id: Int?,

    @SerializedName("username")
    val nombreUsuario: String?,

    val email: String?,

    val roles: List<String>? = null,

    val token: String? = null,

    @SerializedName("access_token")
    val accessToken: String? = null
) {
    val rol: String
        get() = if (roles?.contains("ROLE_ADMIN") == true) "ROLE_ADMIN" else "ROLE_USER"
}