package com.example.aplicacionbiscotti.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.aplicacionbiscotti.ui.views.*
import com.example.aplicacionbiscotti.viewmodel.*

@Composable
fun AppNavigation(navController: NavHostController) {

    val context = LocalContext.current
    val application = context.applicationContext as android.app.Application
    val factory = androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.getInstance(application)

    val authViewModel: AuthViewModel = viewModel(factory = factory)
    val productoViewModel: ProductoViewModel = viewModel(factory = factory)
    val carritoViewModel: CarritoViewModel = viewModel(factory = factory)
    val pedidoViewModel: PedidoViewModel = viewModel(factory = factory)
    val recetaViewModel: RecetaViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") {
            PantallaSplash(navController = navController, authViewModel = authViewModel)
        }
        composable("login") {
            PantallaLogin(navController = navController, viewModel = authViewModel)
        }
        composable("registro") {
            PantallaRegistro(navController = navController, viewModel = authViewModel)
        }
        composable("inicio") {
            PantallaInicio(navController = navController, authViewModel = authViewModel, productoViewModel = productoViewModel)
        }
        composable("productos") {
            PantallaProductos(navController = navController, productoViewModel = productoViewModel, carritoViewModel = carritoViewModel, authViewModel = authViewModel)
        }
        composable("carrito") {
            PantallaCarrito(navController = navController, carritoViewModel = carritoViewModel, authViewModel = authViewModel, pedidoViewModel = pedidoViewModel)
        }
        composable("confirmar_pedido") {
            PantallaConfirmarPedido(navController = navController, carritoViewModel = carritoViewModel, authViewModel = authViewModel, pedidoViewModel = pedidoViewModel)
        }
        composable("mis_pedidos") {
            PantallaMisPedidos(navController = navController, authViewModel = authViewModel, pedidoViewModel = pedidoViewModel)
        }
        composable("detalle_pedido") {
            PantallaDetallePedido(navController = navController, pedidoViewModel = pedidoViewModel)
        }
        composable("perfil") {
            PantallaPerfil(navController = navController, authViewModel = authViewModel)
        }
        composable("panel_admin") {
            PantallaPanelAdmin(navController = navController, productoViewModel = productoViewModel, authViewModel = authViewModel, pedidoViewModel = pedidoViewModel)
        }
        composable("agregar_producto") {
            PantallaAgregarProducto(navController = navController, viewModel = productoViewModel)
        }
        // NUEVA RUTA AGREGADA
        composable("editar_producto") {
            PantallaEditarProducto(navController = navController, viewModel = productoViewModel)
        }

        composable("gestion_pedidos") {
            PantallaGestionPedidos(navController = navController, pedidoViewModel = pedidoViewModel)
        }
        
        composable("admin_detalle_pedido") {
            PantallaDetallePedido(navController = navController, pedidoViewModel = pedidoViewModel)
        }

        composable("recetas") {
            PantallaRecetas(
                navController = navController,
                viewModel = recetaViewModel
            )
        }
    }
}