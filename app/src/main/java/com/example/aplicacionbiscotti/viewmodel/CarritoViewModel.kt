package com.example.aplicacionbiscotti.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicacionbiscotti.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CarritoViewModel(application: Application) : AndroidViewModel(application) {

    private val database = BaseDatos_Biscotti.getDatabase(application)
    private val carritoDao = database.carritoDao()
    private val productoDao = database.productoDao()

    private val _itemsCarrito = MutableStateFlow<List<Carrito>>(emptyList())
    val itemsCarrito: StateFlow<List<Carrito>> = _itemsCarrito.asStateFlow()

    private val _productosCarrito = MutableStateFlow<List<Producto>>(emptyList())
    val productosCarrito: StateFlow<List<Producto>> = _productosCarrito.asStateFlow()

    private val _total = MutableStateFlow(0.0)
    val total: StateFlow<Double> = _total.asStateFlow()

    fun cargarCarrito(usuarioId: Int) {
        viewModelScope.launch {
            carritoDao.obtenerCarritoPorUsuario(usuarioId).collect { items ->
                _itemsCarrito.value = items

                // Obtener los productos completos
                val productos = items.mapNotNull { item ->
                    productoDao.obtenerProductoPorId(item.productoId)
                }
                _productosCarrito.value = productos

                // Calcular total
                var totalCalculado = 0.0
                items.forEach { item ->
                    val producto = productoDao.obtenerProductoPorId(item.productoId)
                    if (producto != null) {
                        totalCalculado += producto.precio * item.cantidad
                    }
                }
                _total.value = totalCalculado
            }
        }
    }

    fun agregarAlCarrito(productoId: Int, usuarioId: Int) {
        viewModelScope.launch {
            val itemExistente = _itemsCarrito.value.find { it.productoId == productoId }

            if (itemExistente != null) {
                // Si ya existe, aumentar cantidad
                carritoDao.actualizarCantidad(itemExistente.id, itemExistente.cantidad + 1)
            } else {
                // Si no existe, agregar nuevo
                val nuevoItem = Carrito(
                    productoId = productoId,
                    usuarioId = usuarioId,
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

    fun actualizarCantidad(carritoId: Int, cantidad: Int) {
        viewModelScope.launch {
            if (cantidad > 0) {
                carritoDao.actualizarCantidad(carritoId, cantidad)
            } else {
                carritoDao.eliminarDelCarrito(carritoId)
            }
        }
    }

    fun vaciarCarrito(usuarioId: Int) {
        viewModelScope.launch {
            carritoDao.vaciarCarrito(usuarioId)
        }
    }
}