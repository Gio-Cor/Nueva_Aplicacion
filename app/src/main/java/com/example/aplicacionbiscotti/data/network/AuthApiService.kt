package com.example.aplicacionbiscotti.data.network

import com.example.aplicacionbiscotti.data.LoginRequest
import com.example.aplicacionbiscotti.data.RegisterRequest
import com.example.aplicacionbiscotti.data.UserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("/api/v1/auth/login")
    suspend fun iniciarSesion(@Body request: LoginRequest): Response<UserResponse>

    @POST("/api/v1/auth/register")
    suspend fun registrarUsuario(@Body request: RegisterRequest): Response<Void>
}