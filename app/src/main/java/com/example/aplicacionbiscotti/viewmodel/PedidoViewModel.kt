package com.example.aplicacionbiscotti.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicacionbiscotti.data.*
import com.example.aplicacionbiscotti.data.repository.PedidoRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class PedidoViewModel(application: Application) : AndroidViewModel(application) {

    private val database = BaseDatos_Biscotti.getDatabase(application)
    private val pedidoDao = database.pedidoDao()
    private val detallePedidoDao = database.detallePedidoDao()
    private val carritoDao = database.carritoDao()


    private val pedidoRepository = PedidoRepository()


    private val _pedidos = MutableStateFlow<List<Pedido>>(emptyList())
    val pedidos: StateFlow<List<Pedido>> = _pedidos.asStateFlow()
    private val _todosLosPedidos = MutableStateFlow<List<Pedido>>(emptyList())
    val todosLosPedidos: StateFlow<List<Pedido>> = _todosLosPedidos.asStateFlow()
    private val _pedidoSeleccionado = MutableStateFlow<Pedido?>(null)
    val pedidoSeleccionado: StateFlow<Pedido?> = _pedidoSeleccionado.asStateFlow()
    private val _detallesPedido = MutableStateFlow<List<DetallePedido>>(emptyList())
    val detallesPedido: StateFlow<List<DetallePedido>> = _detallesPedido.asStateFlow()
    private val _mensajeExito = MutableStateFlow<String?>(null)
    val mensajeExito: StateFlow<String?> = _mensajeExito.asStateFlow()
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()


    fun cargarPedidosPorUsuario(usuarioId: Int) {
        viewModelScope.launch {
            try {
                val response = pedidoRepository.obtenerPedidosPorUsuario(usuarioId)
                if (response.isSuccessful && response.body() != null) {
                    _pedidos.value = response.body()!!
                } else {
                    pedidoDao.obtenerPedidosPorUsuario(usuarioId).collect { _pedidos.value = it }
                }
            } catch (e: Exception) {
                pedidoDao.obtenerPedidosPorUsuario(usuarioId).collect { _pedidos.value = it }
            }
        }
    }

    fun cargarTodosLosPedidos() {
        viewModelScope.launch {
            try {
                val response = pedidoRepository.obtenerTodosLosPedidos()
                if (response.isSuccessful && response.body() != null) {
                    _todosLosPedidos.value = response.body()!!
                } else {
                    pedidoDao.obtenerTodosPedidos().collect { _todosLosPedidos.value = it }
                }
            } catch (e: Exception) {
                pedidoDao.obtenerTodosPedidos().collect { _todosLosPedidos.value = it }
            }
        }
    }

    fun crearPedido(
        usuarioId: Int,
        nombreUsuario: String,
        total: Double,
        metodoPago: String,
        onPedidoCreado: (Long) -> Unit
    ) {
        viewModelScope.launch {
            val itemsCarrito = carritoDao.obtenerCarritoPorUsuario(usuarioId).first()
            if (itemsCarrito.isEmpty()) {
                _mensajeExito.value = "El carrito está vacío"
                return@launch
            }
            val numeroBoleta = "BOL-${System.currentTimeMillis()}"
            val pedido = Pedido(0, usuarioId, nombreUsuario, System.currentTimeMillis(), total, "Pendiente", numeroBoleta, metodoPago)
            

            val pedidoIdLocal = pedidoDao.insertarPedido(pedido)
            val detalles = itemsCarrito.map { DetallePedido(0, pedidoIdLocal.toInt(), it.productoId, it.nombreProducto, it.cantidad, it.precioUnitario, it.precioUnitario * it.cantidad) }
            detallePedidoDao.insertarDetalles(detalles)
            
            try {

                val response = pedidoRepository.crearPedido(pedido)
                if (response.isSuccessful) {
                    _mensajeExito.value = "Pedido enviado al servidor exitosamente"
                } else {
                    _mensajeExito.value = "Pedido guardado localmente (Error servidor: ${response.code()})"
                }
            } catch (e: Exception) {
                _error.value = "Error de red. Pedido guardado solo localmente."
            }


            carritoDao.vaciarCarrito(usuarioId)
            onPedidoCreado(pedidoIdLocal)
        }
    }

    fun actualizarEstadoPedido(pedidoId: Int, nuevoEstado: String) {
        viewModelScope.launch {

            val pedidoLocal = pedidoDao.obtenerPedidoPorId(pedidoId)
            if (pedidoLocal != null) {
                val pedidoActualizado = pedidoLocal.copy(estado = nuevoEstado)
                pedidoDao.actualizarPedido(pedidoActualizado)
                cargarTodosLosPedidos()
            }

            try {

                pedidoRepository.actualizarEstadoPedido(pedidoId, nuevoEstado)
            } catch (e: Exception) {
                _error.value = "No se pudo sincronizar el cambio de estado con el servidor."
            }
        }
    }

    fun cargarDetallesPedido(pedidoId: Int) {
        viewModelScope.launch {
            detallePedidoDao.obtenerDetallesPorPedido(pedidoId).collect { _detallesPedido.value = it }
        }
    }

    fun seleccionarPedido(pedido: Pedido) {
        _pedidoSeleccionado.value = pedido
        cargarDetallesPedido(pedido.id)
    }

    suspend fun buscarPedidoPorBoleta(numeroBoleta: String): Pedido? {
        return pedidoDao.obtenerPedidoPorBoleta(numeroBoleta)
    }

    fun limpiarMensaje() {
        _mensajeExito.value = null
        _error.value = null
    }
}