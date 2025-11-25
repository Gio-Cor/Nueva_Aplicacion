package com.example.aplicacionbiscotti.ui.views

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
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
import com.example.aplicacionbiscotti.R
import com.example.aplicacionbiscotti.data.Carrito
import com.example.aplicacionbiscotti.viewmodel.AuthViewModel
import com.example.aplicacionbiscotti.viewmodel.CarritoViewModel
import com.example.aplicacionbiscotti.viewmodel.PedidoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaCarrito(
    navController: NavController,
    authViewModel: AuthViewModel,
    carritoViewModel: CarritoViewModel,
    pedidoViewModel: PedidoViewModel
) {
    val sesion by authViewModel.estadoSesion.collectAsState()
    val itemsCarrito by carritoViewModel.items.collectAsState()
    val total by carritoViewModel.total.collectAsState()

    val context = LocalContext.current
    var procesandoPedido by remember { mutableStateOf(false) }


    val metodosDePago = listOf("Efectivo", "Tarjeta de Débito", "Tarjeta de Crédito", "Transferencia")
    var metodoSeleccionado by remember { mutableStateOf(metodosDePago[0]) }
    var menuExpandido by remember { mutableStateOf(false) }



    LaunchedEffect(sesion) {
        sesion?.let {
            carritoViewModel.obtenerCarrito(it.usuarioId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Carrito de Compras", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        if (itemsCarrito.isEmpty()) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Tu carrito está vacío",
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        } else {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(itemsCarrito) { item ->
                        ItemCarrito(
                            item = item,
                            onSumar = { carritoViewModel.actualizarCantidad(item.id, item.cantidad + 1) },
                            onRestar = { if (item.cantidad > 1) carritoViewModel.actualizarCantidad(item.id, item.cantidad - 1) else carritoViewModel.eliminarDelCarrito(item.id) },
                            onEliminar = { carritoViewModel.eliminarDelCarrito(item.id) }
                        )
                    }
                }


                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                    elevation = CardDefaults.cardElevation(8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(Modifier.padding(20.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total a Pagar:", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            Text("$${String.format("%,.0f", total)}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }

                        Spacer(modifier = Modifier.height(20.dp))


                        ExposedDropdownMenuBox(
                            expanded = menuExpandido,
                            onExpandedChange = { menuExpandido = !menuExpandido }
                        ) {
                            OutlinedTextField(
                                value = metodoSeleccionado,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Método de Pago") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuExpandido) },
                                modifier = Modifier.fillMaxWidth().menuAnchor(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                                    cursorColor = MaterialTheme.colorScheme.primary
                                )
                            )
                            ExposedDropdownMenu(
                                expanded = menuExpandido,
                                onDismissRequest = { menuExpandido = false }
                            ) {
                                metodosDePago.forEach { metodo ->
                                    DropdownMenuItem(
                                        text = { Text(metodo) },
                                        onClick = {
                                            metodoSeleccionado = metodo
                                            menuExpandido = false
                                        }
                                    )
                                }
                            }
                        }


                        Spacer(modifier = Modifier.height(20.dp))


                        Button(
                            onClick = {
                                procesandoPedido = true
                                sesion?.let { s ->
                                    pedidoViewModel.crearPedido(
                                        usuarioId = s.usuarioId,
                                        nombreUsuario = s.nombreUsuario,
                                        total = total,
                                        metodoPago = metodoSeleccionado,
                                        onPedidoCreado = {
                                            Toast.makeText(context, "¡Pedido realizado con éxito!", Toast.LENGTH_LONG).show()
                                            carritoViewModel.vaciarCarrito(s.usuarioId)
                                            procesandoPedido = false
                                            navController.navigate("mis_pedidos") {
                                                popUpTo("carrito") { inclusive = true }
                                            }
                                        }
                                    )
                                } ?: run {
                                    Toast.makeText(context, "Error: Sesión no encontrada", Toast.LENGTH_SHORT).show()
                                    procesandoPedido = false
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            enabled = !procesandoPedido,
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            if (procesandoPedido) {
                                CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
                            } else {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CheckCircle, null, tint = MaterialTheme.colorScheme.onPrimary)
                                    Spacer(Modifier.width(8.dp))
                                    Text("Confirmar Pedido", color = MaterialTheme.colorScheme.onPrimary)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ItemCarrito(
    item: Carrito,
    onSumar: () -> Unit,
    onRestar: () -> Unit,
    onEliminar: () -> Unit
) {
    Card(
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. Imagen
            val context = LocalContext.current
            val imageId = remember(item.imagenUrl) {
                context.resources.getIdentifier(item.imagenUrl, "drawable", context.packageName)
            }
            Image(
                painter = painterResource(id = if (imageId != 0) imageId else R.drawable.ic_launcher_background),
                contentDescription = item.nombreProducto,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.width(16.dp))

            // 2. Datos del producto
            Column(modifier = Modifier.weight(1f)) {
                Text(item.nombreProducto, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                Text("$${String.format("%,.0f", item.precioUnitario)} c/u", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            // 3. Botones de cantidad
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onRestar) { Icon(Icons.Default.RemoveCircleOutline, "Restar", tint = MaterialTheme.colorScheme.primary) }
                Text("${item.cantidad}", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface)
                IconButton(onClick = onSumar) { Icon(Icons.Default.AddCircleOutline, "Sumar", tint = MaterialTheme.colorScheme.primary) }
                IconButton(onClick = onEliminar) { Icon(Icons.Default.Delete, "Eliminar", tint = Color.Red) }
            }
        }
    }
}