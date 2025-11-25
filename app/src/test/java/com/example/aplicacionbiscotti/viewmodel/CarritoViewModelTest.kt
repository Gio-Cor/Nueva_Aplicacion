package com.example.aplicacionbiscotti.viewmodel

import android.app.Application
import com.example.aplicacionbiscotti.data.*
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CarritoViewModelTest {

    private lateinit var viewModel: CarritoViewModel
    private lateinit var carritoDao: CarritoDao
    private lateinit var app: Application
    

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        app = mockk(relaxed = true)
        carritoDao = mockk(relaxed = true)


        mockkObject(BaseDatos_Biscotti)
        every { BaseDatos_Biscotti.getDatabase(app).carritoDao() } returns carritoDao
        

        coEvery { carritoDao.obtenerCarritoPorUsuario(any()) } returns flowOf(emptyList())


        viewModel = CarritoViewModel(app, carritoDao)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `agregarAlCarrito - producto nuevo - lo inserta en el carrito`() = runTest {

        val productoNuevo = Producto(id = 1, nombre = "Galleta Nueva", precio = 1000.0, descripcion = "", categoria = "", imagenUrl = "")
        val usuarioId = 1
        coEvery { carritoDao.obtenerProductoEnCarrito(usuarioId, productoNuevo.id) } returns null
        coEvery { carritoDao.agregarAlCarrito(any()) } just Runs

        // Act
        viewModel.agregarAlCarrito(productoNuevo, usuarioId)
        
        // Assert
        coVerify(exactly = 1) { carritoDao.agregarAlCarrito(any()) }
    }

    @Test
    fun `agregarAlCarrito - producto existente - actualiza la cantidad`() = runTest {

        val productoId = 1
        val usuarioId = 1
        val itemExistente = Carrito(id = 10, productoId = productoId, usuarioId = usuarioId, cantidad = 2)
        
        coEvery { carritoDao.obtenerProductoEnCarrito(usuarioId, productoId) } returns itemExistente
        coEvery { carritoDao.actualizarCantidad(any(), any()) } just Runs

        // Act
        val producto = Producto(id = productoId, nombre = "Test", precio = 0.0, descripcion = "", categoria = "", imagenUrl = "")
        viewModel.agregarAlCarrito(producto, usuarioId)

        // Assert
        coVerify(exactly = 1) { carritoDao.actualizarCantidad(itemExistente.id, 3) } // 2 (existente) + 1
        coVerify(exactly = 0) { carritoDao.agregarAlCarrito(any()) } // No debe agregar uno nuevo
    }

    @Test
    fun `total - se calcula correctamente con varios items`() = runTest {

        val items = listOf(
            Carrito(id = 1, productoId = 1, usuarioId = 1, cantidad = 2, precioUnitario = 100.0), // 200
            Carrito(id = 2, productoId = 2, usuarioId = 1, cantidad = 3, precioUnitario = 50.0)  // 150
        )

        every { carritoDao.obtenerCarritoPorUsuario(1) } returns flowOf(items)


        viewModel.obtenerCarrito(1)
        val totalCalculado = viewModel.total.first() // Obtenemos el primer valor emitido por el StateFlow
        

        assertEquals(350.0, totalCalculado, 0.0)
    }

    @Test
    fun `actualizarCantidad - con cantidad mayor a 0 - llama a dao_actualizarCantidad`() = runTest {
        // Act
        viewModel.actualizarCantidad(1, 5)


        coVerify(exactly = 1) { carritoDao.actualizarCantidad(1, 5) }
        coVerify(exactly = 0) { carritoDao.eliminarDelCarrito(any()) }
    }

    @Test
    fun `actualizarCantidad - con cantidad 0 - llama a dao_eliminarDelCarrito`() = runTest {

        viewModel.actualizarCantidad(1, 0)
        

        coVerify(exactly = 1) { carritoDao.eliminarDelCarrito(1) }
        coVerify(exactly = 0) { carritoDao.actualizarCantidad(any(), any()) }
    }

    @Test
    fun `vaciarCarrito - llama a dao_vaciarCarrito`() = runTest {

        viewModel.vaciarCarrito(1)


        coVerify(exactly = 1) { carritoDao.vaciarCarrito(1) }
    }
}