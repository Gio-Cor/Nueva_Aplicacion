package com.example.aplicacionbiscotti.viewmodel

import com.example.aplicacionbiscotti.data.Receta
import com.example.aplicacionbiscotti.data.repository.RepositorioRecetas
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

@OptIn(ExperimentalCoroutinesApi::class)
class RecetaViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: RecetaViewModel
    private val repositorio = mockk<RepositorioRecetas>()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = RecetaViewModel()

        val field = RecetaViewModel::class.java.getDeclaredField("repositorio")
        field.isAccessible = true
        field.set(viewModel, repositorio)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `buscar actualiza recetas correctamente`() = runTest(testDispatcher) {
        val recetasMock = listOf(
            Receta("1", "Pan de Chocolate", "Panadería", "Francia", "Hornear 20 min", ""),
            Receta("2", "Pan Integral", "Panadería", "México", "Hornear 25 min", "")
        )

        coEvery { repositorio.buscarRecetasPorNombre("Pan") } returns recetasMock

        viewModel.buscar("Pan")
        testDispatcher.scheduler.advanceUntilIdle() // Espera a que la corrutina termine

        val resultado = viewModel.recetas.value
        assertNotNull(resultado)
        assertEquals(2, resultado!!.size)
        assertEquals("Pan de Chocolate", resultado[0].strMeal)
        assertEquals("Pan Integral", resultado[1].strMeal)
    }
}
