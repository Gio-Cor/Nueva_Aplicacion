package com.example.aplicacionbiscotti.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ClienteRecetas {
    private const val BASE_URL = "https://www.themealdb.com/api/json/v1/1/"

    val servicio: ServicioRecetas by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ServicioRecetas::class.java)
    }
}
