package com.example.aplicacionbiscotti.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicacionbiscotti.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CarritoViewModel(
    application: Application,
    private val carritoDao: CarritoDao
) : AndroidViewModel(application) {

    private val _items = MutableStateFlow<List<Carrito>>(emptyList())
    val items = _items.asStateFlow()

    val total: StateFlow<Double> = _items.map { lista ->
        lista.sumOf { it.precioUnitario * it.cantidad }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        0.0
    )

    fun obtenerCarrito(usuarioId: Int) {
        viewModelScope.launch {
            carritoDao.obtenerCarritoPorUsuario(usuarioId).collect {
                _items.value = it
            }
        }
    }

    fun agregarAlCarrito(producto: Producto, usuarioId: Int) {
        viewModelScope.launch {
            val itemExistente = carritoDao.obtenerProductoEnCarrito(usuarioId, producto.id)

            if (itemExistente != null) {
                carritoDao.actualizarCantidad(itemExistente.id, itemExistente.cantidad + 1)
            } else {
                val nuevoItem = Carrito(
                    usuarioId = usuarioId,
                    productoId = producto.id,
                    nombreProducto = producto.nombre,
                    precioUnitario = producto.precio,
                    imagenUrl = producto.imagenUrl ?: "",
                    cantidad = 1
                )
                carritoDao.agregarAlCarrito(nuevoItem)
            }
        }
    }

    fun eliminarDelCarrito(carritoId: Int) {
        viewModelScope.launch {
            carritoDao.eliminarDelCarrito(carritoId)
        }
    }

    fun vaciarCarrito(usuarioId: Int) {
        viewModelScope.launch {
            carritoDao.vaciarCarrito(usuarioId)
        }
    }

    fun actualizarCantidad(carritoId: Int, cantidad: Int) {
        viewModelScope.launch {
            if (cantidad > 0) {
                carritoDao.actualizarCantidad(carritoId, cantidad)
            } else {
                eliminarDelCarrito(carritoId)
            }
        }
    }

    constructor(application: Application) : this(
        application,
        BaseDatos_Biscotti.getDatabase(application).carritoDao()
    )

}
