package com.example.aplicacionbiscotti.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicacionbiscotti.data.Receta
import com.example.aplicacionbiscotti.data.repository.RepositorioRecetas
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RecetaViewModel : ViewModel() {
    private val repositorio = RepositorioRecetas()

    private val _recetas = MutableStateFlow<List<Receta>?>(null)
    val recetas: StateFlow<List<Receta>?> = _recetas

    fun buscar(nombre: String) {
        viewModelScope.launch {
            _recetas.value = repositorio.buscarRecetasPorNombre(nombre)
        }
    }
}