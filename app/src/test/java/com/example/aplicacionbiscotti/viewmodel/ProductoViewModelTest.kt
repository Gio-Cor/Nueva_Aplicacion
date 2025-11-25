package com.example.aplicacionbiscotti.viewmodel

import android.app.Application
import com.example.aplicacionbiscotti.data.*
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProductoViewModelTest {

    private lateinit var viewModel: ProductoViewModel
    private lateinit var dao: ProductoDao
    private lateinit var app: Application

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        // Mock Application
        app = mockk(relaxed = true)

        // Mock Dao
        dao = mockk(relaxed = true)

        // Mock Database
        mockkObject(BaseDatos_Biscotti)
        every { BaseDatos_Biscotti.getDatabase(app).productoDao() } returns dao

        // Mock flujo productos
        every { dao.obtenerTodosLosProductos() } returns MutableStateFlow(emptyList())

        viewModel = ProductoViewModel(app)
    }

    @Test
    fun `agregarProducto - valida campos vacios`() = runTest {
        viewModel.agregarProducto("", "", "", "", "")

        advanceUntilIdle() // 🔥 IMPORTANTE

        assertEquals("Por favor completa todos los campos", viewModel.mensajeError.value)
    }

    @Test
    fun `agregarProducto - valida precio invalido`() = runTest {

        viewModel.agregarProducto("Pan", "desc", "abc", "", "cat")


        advanceUntilIdle()


        assertEquals("Precio inválido", viewModel.mensajeError.value)
        assertFalse(viewModel.cargando.value)
    }

    @Test
    fun `actualizarProducto - precio invalido`() = runTest {
        viewModel.actualizarProducto(1, "Pan", "desc", "abc", "img", "cat")

        advanceUntilIdle()

        assertEquals("Precio inválido", viewModel.mensajeError.value)
    }


    @Test
    fun `actualizarProducto - llama a actualizarProducto cuando datos validos`() = runTest {
        coEvery { dao.actualizarProducto(any()) } just Runs

        viewModel.actualizarProducto(1, "Pan", "desc", "3.0", "img", "cat")

        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 1) { dao.actualizarProducto(any()) }
    }

    @Test
    fun `eliminarProducto - llama a dao_eliminarProducto`() = runTest {
        val producto = Producto(1, "Pan", "Desc", 5.0, "", "cat")

        coEvery { dao.eliminarProducto(any()) } just Runs

        viewModel.eliminarProducto(producto)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { dao.eliminarProducto(producto) }
    }

    @Test
    fun `obtenerProductoPorId - retorna producto correcto`() = runTest {
        val producto = Producto(1, "Pan", "Desc", 8.0, "", "cat")

        coEvery { dao.obtenerProductoPorId(1) } returns producto

        val resultado = viewModel.obtenerProductoPorId(1)

        assertEquals(producto, resultado)
    }
}
