package com.example.aplicacionbiscotti.data

data class Receta(
    val idMeal: String,
    val strMeal: String,
    val strCategory: String,
    val strArea: String,
    val strInstructions: String,
    val strMealThumb: String
)

data class RespuestaRecetas(
    val meals: List<Receta>?
)