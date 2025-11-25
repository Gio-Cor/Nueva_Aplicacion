package com.example.aplicacionbiscotti.data.repository

import com.example.aplicacionbiscotti.data.LoginRequest
import com.example.aplicacionbiscotti.data.RegisterRequest
import com.example.aplicacionbiscotti.data.UserResponse
import com.example.aplicacionbiscotti.data.network.RetrofitClient
import retrofit2.Response

class AuthRepository {


    private val apiService = RetrofitClient.authService

    //Registro
    suspend fun login(email: String, password: String): Response<UserResponse> {
        val request = LoginRequest(email = email, password = password)
        return apiService.iniciarSesion(request)
    }

    //Login
    suspend fun register(request: RegisterRequest): Response<Void> {
        return apiService.registrarUsuario(request)
    }
}