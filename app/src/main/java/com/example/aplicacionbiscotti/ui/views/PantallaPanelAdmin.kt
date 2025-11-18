package com.example.aplicacionbiscotti.ui.views

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
    authViewModel: AuthViewModel
) {
    val sesion by authViewModel.estadoSesion.collectAsState()
    val productos by productoViewModel.productos.collectAsState()
    var mostrarDialogoEliminar by remember { mutableStateOf(false) }
    var productoAEliminar by remember { mutableStateOf<Producto?>(null) }
    val context = LocalContext.current

    val rosaBiscotti = Color(0xFFFF6B9D)
    val rosaClaroBiscotti = Color(0xFFFFB3D0)
    val grisTextoBiscotti = Color(0xFF666666)

    // Verificar que sea admin
    if (sesion?.esAdmin != true) {
        LaunchedEffect(Unit) {
            navController.popBackStack()
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Panel de Administrador",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = rosaBiscotti,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("agregar_producto") },
                containerColor = rosaBiscotti
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar producto", tint = Color.White)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Estadísticas
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = rosaClaroBiscotti.copy(alpha = 0.3f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${productos.size}",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = rosaBiscotti
                        )
                        Text(
                            text = "Productos",
                            fontSize = 14.sp,
                            color = grisTextoBiscotti
                        )
                    }

                    Divider(
                        modifier = Modifier
                            .height(60.dp)
                            .width(1.dp)
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "✓",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = rosaBiscotti
                        )
                        Text(
                            text = "Admin",
                            fontSize = 14.sp,
                            color = grisTextoBiscotti
                        )
                    }
                }
            }

            // Lista de productos
            if (productos.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "📦",
                            fontSize = 60.sp
                        )
                        Text(
                            text = "No hay productos",
                            fontSize = 18.sp,
                            color = grisTextoBiscotti
                        )
                        Text(
                            text = "Toca + para agregar uno",
                            fontSize = 14.sp,
                            color = grisTextoBiscotti
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
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
                            context = context
                        )
                    }
                }
            }
        }
    }

    // Diálogo de confirmación para eliminar
    if (mostrarDialogoEliminar && productoAEliminar != null) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoEliminar = false },
            title = { Text("Eliminar Producto") },
            text = { Text("¿Estás seguro de que deseas eliminar '${productoAEliminar!!.nombre}'?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        productoViewModel.eliminarProducto(productoAEliminar!!)
                        mostrarDialogoEliminar = false
                        productoAEliminar = null
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color.Red
                    )
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        mostrarDialogoEliminar = false
                        productoAEliminar = null
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun TarjetaProductoAdmin(
    producto: Producto,
    onEliminar: () -> Unit,
    context: android.content.Context
) {
    val rosaBiscotti = Color(0xFFFF6B9D)
    val grisOscuroBiscotti = Color(0xFF333333)
    val grisTextoBiscotti = Color(0xFF666666)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(
                    id = context.resources.getIdentifier(
                        producto.imagenUrl,
                        "drawable",
                        context.packageName
                    )
                ),
                contentDescription = producto.nombre,
                modifier = Modifier
                    .width(120.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp)),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(12.dp)
            ) {
                Text(
                    text = producto.nombre,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = grisOscuroBiscotti,
                    maxLines = 1
                )

                Text(
                    text = producto.categoria,
                    fontSize = 12.sp,
                    color = grisTextoBiscotti
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "$${String.format("%,.0f", producto.precio)}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = rosaBiscotti
                )
            }

            Column(
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.Center
            ) {
                IconButton(
                    onClick = onEliminar,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = Color.Red
                    )
                }
            }
        }
    }
}