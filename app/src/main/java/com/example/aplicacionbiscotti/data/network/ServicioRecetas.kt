package com.example.aplicacionbiscotti.data.network

import com.example.aplicacionbiscotti.data.RespuestaRecetas
import retrofit2.http.GET
import retrofit2.http.Query

interface ServicioRecetas {
    @GET("search.php")
    suspend fun buscarRecetas(@Query("s") nombre: String): RespuestaRecetas

    @GET("lookup.php")
    suspend fun recetaPorId(@Query("i") id: String): RespuestaRecetas
}