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


    private val _productoAEditar = MutableStateFlow<Producto?>(null)
    val productoAEditar: StateFlow<Producto?> = _productoAEditar.asStateFlow()

    init {

        inicializarDatosDePrueba()
    }

    private fun inicializarDatosDePrueba() {
        viewModelScope.launch {
            productoDao.obtenerTodosLosProductos().firstOrNull()?.let { listaActual ->
                if (listaActual.isEmpty()) {
                    insertarProductosEjemplo()
                }
            }
        }
    }

    private suspend fun insertarProductosEjemplo() {
        val productosEjemplo = listOf(
            Producto(
                nombre = "Galletas de matrimonio",
                descripcion = "Deliciosas galletas personalizadas",
                precio = 7490.0,
                imagenUrl = "fotoanillo",
                categoria = "Matrimonio"
            ),
            Producto(
                nombre = "Galletas de paw patrol",
                descripcion = "Hermosas galletas de paw patrol",
                precio = 20000.0,
                imagenUrl = "paw",
                categoria = "Infantil"
            ),
            Producto(
                nombre = "Galletón de Homero y Marge",
                descripcion = "Delicioso galletón de los simpson",
                precio = 10490.0,
                imagenUrl = "homer",
                categoria = "Personajes"
            ),
            Producto(
                nombre = "Galletas de goku",
                descripcion = "5 Galletas de Dragon ball",
                precio = 8990.0,
                imagenUrl = "dragonball",
                categoria = "Anime"
            ),
            Producto(
                nombre = "Galletas de Lilo y Stich",
                descripcion = "Galletas personalizadas de Lilo y Stich",
                precio = 7000.0,
                imagenUrl = "stitch",
                categoria = "Disney"
            ),
            Producto(
                nombre = "Galletas de navidad",
                descripcion = "Hermosas galletas de navidad",
                precio = 5990.0,
                imagenUrl = "navidad",
                categoria = "Celebración"
            )
        )
        
        productosEjemplo.forEach { productoDao.insertarProducto(it) }
    }

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
    

    fun seleccionarProductoAEditar(producto: Producto) {
        _productoAEditar.value = producto
    }

    fun limpiarProductoAEditar() {
        _productoAEditar.value = null
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