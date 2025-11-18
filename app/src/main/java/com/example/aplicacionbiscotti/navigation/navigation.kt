package com.example.aplicacionbiscotti.navigation

import androidx.compose.runtime.Composable
import com.example.aplicacionbiscotti.ui.views.*
import com.example.aplicacionbiscotti.viewmodel.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun AppNavigation(navController: NavHostController) {

    val authViewModel: AuthViewModel = viewModel()
    val productoViewModel: ProductoViewModel = viewModel()
    val carritoViewModel: CarritoViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") {
            PantallaSplash(
                navController = navController,
                authViewModel = authViewModel
            )
        }

        composable("login") {
            PantallaLogin(
                navController = navController,
                viewModel = authViewModel
            )
        }

        composable("registro") {
            PantallaRegistro(
                navController = navController,
                viewModel = authViewModel
            )
        }

        composable("inicio") {
            PantallaInicio(
                navController = navController,
                authViewModel = authViewModel,
                productoViewModel = productoViewModel
            )
        }

        composable("productos") {
            PantallaProductos(
                navController = navController,
                productoViewModel = productoViewModel,
                carritoViewModel = carritoViewModel,
                authViewModel = authViewModel
            )
        }

        composable("carrito") {
            PantallaCarrito(
                navController = navController,
                carritoViewModel = carritoViewModel,
                authViewModel = authViewModel
            )
        }

        composable("panel_admin") {
            PantallaPanelAdmin(
                navController = navController,
                productoViewModel = productoViewModel,
                authViewModel = authViewModel
            )
        }

        composable("agregar_producto") {
            PantallaAgregarProducto(
                navController = navController,
                viewModel = productoViewModel
            )
        }
    }
}