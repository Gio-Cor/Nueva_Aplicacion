package com.example.aplicacionbiscotti.ui.views

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.aplicacionbiscotti.viewmodel.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaConfirmarPedido(
    navController: NavController,
    carritoViewModel: CarritoViewModel,
    authViewModel: AuthViewModel,
    pedidoViewModel: PedidoViewModel
) {
    val context = LocalContext.current
    val sesion by authViewModel.estadoSesion.collectAsState()
    val itemsCarrito by carritoViewModel.items.collectAsState()
    val total by carritoViewModel.total.collectAsState()


    LaunchedEffect(sesion) {
        sesion?.let {
            carritoViewModel.obtenerCarrito(it.usuarioId)
        }
    }

    var metodoPago by remember { mutableStateOf("Efectivo") }
    var expandidoMetodos by remember { mutableStateOf(false) }
    var procesando by remember { mutableStateOf(false) }

    val rosaBiscotti = Color(0xFFFF6B9D)
    val blancoBiscotti = Color(0xFFFFFFFF)
    val grisTextoBiscotti = Color(0xFF666666)
    val grisOscuroBiscotti = Color(0xFF333333)

    val metodosPago = listOf("Efectivo", "Transferencia", "Tarjeta de débito", "Tarjeta de crédito")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Confirmar Pedido", fontWeight = FontWeight.Bold) },
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
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            Text(
                text = "Resumen del Pedido",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = grisOscuroBiscotti
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = blancoBiscotti),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    itemsCarrito.forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.nombreProducto,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = grisOscuroBiscotti
                                )
                                Text(
                                    text = "Cantidad: ${item.cantidad}",
                                    fontSize = 14.sp,
                                    color = grisTextoBiscotti
                                )
                            }
                            Text(
                                text = "$${String.format("%,.0f", item.precioUnitario * item.cantidad)}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = rosaBiscotti
                            )
                        }
                        if (itemsCarrito.last() != item) {
                            Divider(modifier = Modifier.padding(vertical = 8.dp))
                        }
                    }

                    Divider(
                        modifier = Modifier.padding(vertical = 12.dp),
                        thickness = 2.dp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("TOTAL:", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = grisOscuroBiscotti)
                        Text(
                            text = "$${String.format("%,.0f", total)}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = rosaBiscotti
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            ExposedDropdownMenuBox(
                expanded = expandidoMetodos,
                onExpandedChange = { expandidoMetodos = !expandidoMetodos }
            ) {
                OutlinedTextField(
                    value = metodoPago,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Selecciona método") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandidoMetodos) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = rosaBiscotti,
                        focusedLabelColor = rosaBiscotti
                    )
                )
                ExposedDropdownMenu(
                    expanded = expandidoMetodos,
                    onDismissRequest = { expandidoMetodos = false }
                ) {
                    metodosPago.forEach { metodo ->
                        DropdownMenuItem(
                            text = { Text(metodo) },
                            onClick = {
                                metodoPago = metodo
                                expandidoMetodos = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (!procesando) {
                        procesando = true
                        sesion?.let { s ->
                            pedidoViewModel.crearPedido(

                                usuarioId = s.usuarioId,
                                nombreUsuario = s.nombreUsuario,
                                total = total,
                                metodoPago = metodoPago
                            ) { pedidoId ->
                                Toast.makeText(context, "¡Pedido realizado con éxito!", Toast.LENGTH_LONG).show()
                                procesando = false
                                navController.navigate("mis_pedidos") {
                                    popUpTo("carrito") { inclusive = true }
                                }
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = rosaBiscotti),
                shape = RoundedCornerShape(12.dp),
                enabled = !procesando && itemsCarrito.isNotEmpty()
            ) {
                if (procesando) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, null, modifier = Modifier.padding(end = 8.dp))
                        Text("Confirmar Pedido", fontSize = 16.sp)
                    }
                }
            }
        }
    }
}
