package com.example.aplicacionbiscotti.data.repository

import com.example.aplicacionbiscotti.data.Receta
import com.example.aplicacionbiscotti.data.network.ClienteRecetas

class RepositorioRecetas {
    suspend fun buscarRecetasPorNombre(nombre: String): List<Receta>? {
        val respuesta = ClienteRecetas.servicio.buscarRecetas(nombre)
        return respuesta.meals
    }

    suspend fun obtenerRecetaPorId(id: String): Receta? {
        val respuesta = ClienteRecetas.servicio.recetaPorId(id)
        return respuesta.meals?.firstOrNull()
    }
}