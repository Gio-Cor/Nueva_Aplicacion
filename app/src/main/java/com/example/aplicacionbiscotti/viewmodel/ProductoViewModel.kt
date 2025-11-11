package com.example.aplicacionbiscotti.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicacionbiscotti.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ProductoViewModel(application: Application) : AndroidViewModel(application) {

    private val database = BaseDatos_Biscotti.getDatabase(application)
    private val productoDao = database.productoDao()

    val productos: StateFlow<List<Producto>> = productoDao.obtenerTodosLosProductos()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _mensajeError = MutableStateFlow<String?>(null)
    val mensajeError: StateFlow<String?> = _mensajeError.asStateFlow()

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    fun agregarProducto(nombre: String, descripcion: String, precio: String, imagenUrl: String, categoria: String) {
        viewModelScope.launch {
            _cargando.value = true
            _mensajeError.value = null

            // Validaciones
            if (nombre.isBlank() || descripcion.isBlank() || precio.isBlank()) {
                _mensajeError.value = "Por favor completa todos los campos"
                _cargando.value = false
                return@launch
            }

            val precioDouble = precio.toDoubleOrNull()
            if (precioDouble == null || precioDouble <= 0) {
                _mensajeError.value = "Precio inválido"
                _cargando.value = false
                return@launch
            }

            val producto = Producto(
                nombre = nombre,
                descripcion = descripcion,
                precio = precioDouble,
                imagenUrl = imagenUrl.ifBlank { "" },
                categoria = categoria
            )

            productoDao.insertarProducto(producto)
            _cargando.value = false
        }
    }

    fun actualizarProducto(id: Int, nombre: String, descripcion: String, precio: String, imagenUrl: String, categoria: String) {
        viewModelScope.launch {
            _cargando.value = true
            _mensajeError.value = null

            val precioDouble = precio.toDoubleOrNull()
            if (precioDouble == null || precioDouble <= 0) {
                _mensajeError.value = "Precio inválido"
                _cargando.value = false
                return@launch
            }

            val producto = Producto(
                id = id,
                nombre = nombre,
                descripcion = descripcion,
                precio = precioDouble,
                imagenUrl = imagenUrl,
                categoria = categoria
            )

            productoDao.actualizarProducto(producto)
            _cargando.value = false
        }
    }

    fun eliminarProducto(producto: Producto) {
        viewModelScope.launch {
            productoDao.eliminarProducto(producto)
        }
    }

    suspend fun obtenerProductoPorId(id: Int): Producto? {
        return productoDao.obtenerProductoPorId(id)
    }
}