package com.example.aplicacionbiscotti.data.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "http://10.0.2.2:8080"


    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()


    val detallePedidoService: DetallePedidoApiService =
        retrofit.create(DetallePedidoApiService::class.java)
    
    val authService: AuthApiService = retrofit.create(AuthApiService::class.java)

    val pedidoService: PedidoApiService = retrofit.create(PedidoApiService::class.java)
}