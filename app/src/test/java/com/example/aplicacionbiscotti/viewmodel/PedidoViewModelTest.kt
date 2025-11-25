package com.example.aplicacionbiscotti.viewmodel

import android.app.Application
import com.example.aplicacionbiscotti.data.*
import com.example.aplicacionbiscotti.data.repository.PedidoRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class PedidoViewModelTest {

    private lateinit var viewModel: PedidoViewModel
    private lateinit var pedidoDao: PedidoDao
    private lateinit var detallePedidoDao: DetallePedidoDao
    private lateinit var carritoDao: CarritoDao
    private lateinit var productoDao: ProductoDao
    private lateinit var app: Application

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        app = mockk(relaxed = true)
        pedidoDao = mockk(relaxed = true)
        detallePedidoDao = mockk(relaxed = true)
        carritoDao = mockk(relaxed = true)
        productoDao = mockk(relaxed = true)


        mockkObject(BaseDatos_Biscotti)
        every { BaseDatos_Biscotti.getDatabase(app).pedidoDao() } returns pedidoDao
        every { BaseDatos_Biscotti.getDatabase(app).detallePedidoDao() } returns detallePedidoDao
        every { BaseDatos_Biscotti.getDatabase(app).carritoDao() } returns carritoDao
        every { BaseDatos_Biscotti.getDatabase(app).productoDao() } returns productoDao


        mockkConstructor(PedidoRepository::class)
        

        coEvery { anyConstructed<PedidoRepository>().crearPedido(any()) } returns Response.success(mockk(relaxed = true))

        viewModel = PedidoViewModel(app)
    }

    @After
    fun tearDown() {
        unmockkAll()
        Dispatchers.resetMain()
    }

    @Test
    fun `crearPedido - carrito vacio retorna mensaje`() = runTest {

        coEvery { carritoDao.obtenerCarritoPorUsuario(1) } returns flowOf(emptyList())

        var pedidoIdGenerado: Long? = null
        viewModel.crearPedido(1, "Usuario", 100.0, "Efectivo") { id ->
            pedidoIdGenerado = id
        }

        advanceUntilIdle()

        assertEquals("El carrito está vacío", viewModel.mensajeExito.value)
        assertNull(pedidoIdGenerado)
    }


    @Test
    fun `crearPedido - carrito con items crea pedido local y llama al backend`() = runTest {

        val carritoItem = Carrito(1, 1, 2, cantidad = 2, nombreProducto = "Pan", precioUnitario = 10.0)
        coEvery { carritoDao.obtenerCarritoPorUsuario(1) } returns flowOf(listOf(carritoItem))


        coEvery { pedidoDao.insertarPedido(any()) } returns 100L
        coEvery { detallePedidoDao.insertarDetalles(any()) } just Runs
        coEvery { carritoDao.vaciarCarrito(1) } just Runs


        val pedidoResponse = Pedido(id = 200, usuarioId = 1, nombreUsuario = "Usuario", total = 20.0)
        coEvery { anyConstructed<PedidoRepository>().crearPedido(any()) } returns Response.success(pedidoResponse)

        var pedidoIdGenerado: Long? = null
        viewModel.crearPedido(1, "Usuario", 20.0, "Efectivo") { id ->
            pedidoIdGenerado = id
        }

        advanceUntilIdle()


        coVerify { pedidoDao.insertarPedido(any()) }
        coVerify { detallePedidoDao.insertarDetalles(any()) }
        coVerify { anyConstructed<PedidoRepository>().crearPedido(any()) }
        coVerify { carritoDao.vaciarCarrito(1) }


        assertEquals("Pedido enviado al servidor exitosamente", viewModel.mensajeExito.value)
        assertEquals(100L, pedidoIdGenerado)
    }
}