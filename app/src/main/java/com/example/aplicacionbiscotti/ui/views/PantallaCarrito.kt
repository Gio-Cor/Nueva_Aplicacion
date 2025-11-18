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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.aplicacionbiscotti.data.Carrito
import com.example.aplicacionbiscotti.data.Producto
import com.example.aplicacionbiscotti.viewmodel.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaCarrito(
    navController: NavController,
    carritoViewModel: CarritoViewModel,
    authViewModel: AuthViewModel
) {
    val sesion by authViewModel.estadoSesion.collectAsState()
    val itemsCarrito by carritoViewModel.itemsCarrito.collectAsState()
    val productosCarrito by carritoViewModel.productosCarrito.collectAsState()
    val total by carritoViewModel.total.collectAsState()
    val context = LocalContext.current

    val rosaBiscotti = Color(0xFFFF6B9D)
    val grisTextoBiscotti = Color(0xFF666666)
    val grisOscuroBiscotti = Color(0xFF333333)

    LaunchedEffect(sesion) {
        sesion?.let {
            carritoViewModel.cargarCarrito(it.usuarioId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Mi Carrito",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    if (itemsCarrito.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                sesion?.let {
                                    carritoViewModel.vaciarCarrito(it.usuarioId)
                                }
                            }
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Vaciar carrito")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = rosaBiscotti,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        },
        bottomBar = {
            if (itemsCarrito.isNotEmpty()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shadowElevation = 8.dp,
                    color = Color.White
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Total:",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = grisOscuroBiscotti
                            )
                            Text(
                                text = "$${String.format("%,.0f", total)}",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = rosaBiscotti
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                // Aquí iría la lógica de pagar
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = rosaBiscotti
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text("Realizar Pedido", fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        if (itemsCarrito.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(32.dp)
                ) {
                    Text(
                        text = "🛒",
                        fontSize = 80.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Tu carrito está vacío",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = grisOscuroBiscotti
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Agrega productos para comenzar tu pedido",
                        fontSize = 14.sp,
                        color = grisTextoBiscotti,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { navController.popBackStack() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = rosaBiscotti
                        )
                    ) {
                        Text("Ver Productos")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(itemsCarrito) { item ->
                    val producto = productosCarrito.find { it.id == item.productoId }
                    if (producto != null) {
                        ItemCarrito(
                            item = item,
                            producto = producto,
                            onEliminar = {
                                carritoViewModel.eliminarDelCarrito(item.id)
                            },
                            onCantidadChange = { nuevaCantidad ->
                                carritoViewModel.actualizarCantidad(item.id, nuevaCantidad)
                            },
                            context = context
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ItemCarrito(
    item: Carrito,
    producto: Producto,
    onEliminar: () -> Unit,
    onCantidadChange: (Int) -> Unit,
    context: android.content.Context
) {
    val rosaBiscotti = Color(0xFFFF6B9D)
    val grisOscuroBiscotti = Color(0xFF333333)
    val grisTextoBiscotti = Color(0xFF666666)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
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
                    .width(140.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp)),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = producto.nombre,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = grisOscuroBiscotti,
                        maxLines = 2,
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(
                        onClick = onEliminar,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Eliminar",
                            tint = Color.Red,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "$${String.format("%,.0f", producto.precio)}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = rosaBiscotti
                )

                Spacer(modifier = Modifier.weight(1f))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = { onCantidadChange(item.cantidad - 1) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Remove,
                            contentDescription = "Disminuir",
                            tint = rosaBiscotti
                        )
                    }

                    Text(
                        text = "${item.cantidad}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    IconButton(
                        onClick = { onCantidadChange(item.cantidad + 1) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Aumentar",
                            tint = rosaBiscotti
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = "$${String.format("%,.0f", producto.precio * item.cantidad)}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = grisOscuroBiscotti
                    )
                }
            }
        }
    }
}