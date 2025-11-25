package com.example.aplicacionbiscotti.ui.views

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.aplicacionbiscotti.data.Producto
import com.example.aplicacionbiscotti.viewmodel.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPanelAdmin(
    navController: NavController,
    productoViewModel: ProductoViewModel,
    authViewModel: AuthViewModel,
    pedidoViewModel: PedidoViewModel
) {
    val sesion by authViewModel.estadoSesion.collectAsState()
    val productos by productoViewModel.productos.collectAsState()
    val listaPedidos by pedidoViewModel.todosLosPedidos.collectAsState(initial = emptyList())

    var mostrarDialogoEliminar by remember { mutableStateOf(false) }
    var productoAEliminar by remember { mutableStateOf<Producto?>(null) }

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        pedidoViewModel.cargarTodosLosPedidos()
    }

    if (sesion?.email != "admin@biscotti.com") {
        LaunchedEffect(Unit) {
            navController.popBackStack()
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel de Administrador", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("agregar_producto") },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar", tint = MaterialTheme.colorScheme.onPrimary)
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).background(MaterialTheme.colorScheme.background)) {
            // 1. Estadísticas
            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${productos.size}", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Text("Productos", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Divider(modifier = Modifier.height(60.dp).width(1.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${listaPedidos.size}", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Text("Pedidos", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            // BOTÓN GESTIÓN PEDIDOS
            Button(
                onClick = { navController.navigate("gestion_pedidos") },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Settings, null, modifier = Modifier.padding(end = 8.dp), tint = MaterialTheme.colorScheme.onPrimary)
                Text("Gestión de Pedidos", fontSize = 16.sp, color = MaterialTheme.colorScheme.onPrimary)
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            // 2. Lista de productos
            if (productos.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay productos", color = MaterialTheme.colorScheme.onBackground)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(productos) { producto ->
                        TarjetaProductoAdmin(
                            producto = producto,
                            onEliminar = {
                                productoAEliminar = producto
                                mostrarDialogoEliminar = true
                            },
                            onEditar = {
                                productoViewModel.seleccionarProductoAEditar(producto)
                                navController.navigate("editar_producto")
                            },
                            context = context
                        )
                    }
                }
            }
        }
    }

    if (mostrarDialogoEliminar && productoAEliminar != null) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoEliminar = false },
            title = { Text("Eliminar Producto") },
            text = { Text("¿Eliminar '${productoAEliminar!!.nombre}'?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        productoViewModel.eliminarProducto(productoAEliminar!!)
                        mostrarDialogoEliminar = false
                        productoAEliminar = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                ) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoEliminar = false }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
fun TarjetaProductoAdmin(
    producto: Producto,
    onEliminar: () -> Unit,
    onEditar: () -> Unit,
    context: android.content.Context
) {
    Card(
        modifier = Modifier.fillMaxWidth().height(120.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            val imageId = remember(producto.imagenUrl) {
                context.resources.getIdentifier(producto.imagenUrl ?: "", "drawable", context.packageName)
            }
            if (imageId != 0) {
                Image(
                    painter = painterResource(id = imageId),
                    contentDescription = null,
                    modifier = Modifier.width(120.dp).fillMaxHeight().clip(RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(modifier = Modifier.width(120.dp).fillMaxHeight().background(Color.LightGray))
            }

            Column(modifier = Modifier.weight(1f).padding(12.dp)) {
                Text(producto.nombre, fontWeight = FontWeight.Bold, maxLines = 1, color = MaterialTheme.colorScheme.onSurface)
                Text(producto.categoria, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.weight(1f))
                Text("$${String.format("%,.0f", producto.precio)}", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
            }

            Column(
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.Center
            ) {
                IconButton(onClick = onEditar) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Color.Blue)
                }
                IconButton(onClick = onEliminar) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Red)
                }
            }
        }
    }
}