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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.aplicacionbiscotti.viewmodel.PedidoViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaAdminDetallePedido(
    navController: NavController,
    pedidoViewModel: PedidoViewModel
) {
    val context = LocalContext.current
    val pedido by pedidoViewModel.pedidoSeleccionado.collectAsState()
    val detalles by pedidoViewModel.detallesPedido.collectAsState()
    val mensajeExito by pedidoViewModel.mensajeExito.collectAsState()

    var mostrarDialogoCambiarEstado by remember { mutableStateOf(false) }

    val rosaBiscotti = Color(0xFFFF6B9D)
    val blancoBiscotti = Color(0xFFFFFFFF)
    val grisTextoBiscotti = Color(0xFF666666)
    val grisOscuroBiscotti = Color(0xFF333333)

    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    val colorEstado = when (pedido?.estado) {
        "Pendiente" -> Color(0xFFFFA726)
        "En preparación" -> Color(0xFF42A5F5)
        "Completado" -> Color(0xFF66BB6A)
        "Cancelado" -> Color(0xFFEF5350)
        else -> Color.Gray
    }

    LaunchedEffect(mensajeExito) {
        if (mensajeExito != null) {
            Toast.makeText(context, mensajeExito, Toast.LENGTH_SHORT).show()
            pedidoViewModel.limpiarMensaje()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Gestionar Pedido",
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
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            pedido?.let { p ->
                // Información del pedido
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = blancoBiscotti
                    ),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Pedido #${p.id}",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = grisOscuroBiscotti
                            )

                            Surface(
                                color = colorEstado.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = p.estado,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colorEstado,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        InfoRow(icono = Icons.Default.Person, titulo = "Cliente", valor = p.nombreUsuario)
                        InfoRow(icono = Icons.Default.CalendarToday, titulo = "Fecha", valor = dateFormat.format(Date(p.fecha)))
                        InfoRow(icono = Icons.Default.Receipt, titulo = "N° Boleta", valor = p.numeroBoleta)
                        InfoRow(icono = Icons.Default.Payment, titulo = "Método de pago", valor = p.metodoPago)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Productos
                Text(
                    text = "Productos del Pedido",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = grisOscuroBiscotti
                )

                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = blancoBiscotti
                    ),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        detalles.forEach { detalle ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = detalle.nombreProducto,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = grisOscuroBiscotti
                                    )
                                    Text(
                                        text = "Cantidad: ${detalle.cantidad} × $${String.format("%.0f", detalle.precioUnitario)}",
                                        fontSize = 14.sp,
                                        color = grisTextoBiscotti
                                    )
                                }
                                Text(
                                    text = "$${String.format("%,.0f", detalle.subtotal)}",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = rosaBiscotti
                                )
                            }
                            if (detalles.last() != detalle) {
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
                            Text(
                                text = "TOTAL:",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = grisOscuroBiscotti
                            )
                            Text(
                                text = "$${String.format("%,.0f", p.total)}",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = rosaBiscotti
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))


                Button(
                    onClick = { mostrarDialogoCambiarEstado = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = rosaBiscotti
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text("Cambiar Estado del Pedido", fontSize = 16.sp)
                }
            }
        }
    }


    if (mostrarDialogoCambiarEstado && pedido != null) {
        val estados = listOf("Pendiente", "En preparación", "Completado", "Cancelado")
        var estadoSeleccionado by remember { mutableStateOf(pedido!!.estado) }

        AlertDialog(
            onDismissRequest = { mostrarDialogoCambiarEstado = false },
            title = { Text("Cambiar Estado") },
            text = {
                Column {
                    Text("Selecciona el nuevo estado del pedido:")
                    Spacer(modifier = Modifier.height(16.dp))
                    estados.forEach { estado ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { estadoSeleccionado = estado }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = estadoSeleccionado == estado,
                                onClick = { estadoSeleccionado = estado }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(estado)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        pedidoViewModel.actualizarEstadoPedido(pedido!!.id, estadoSeleccionado)
                        mostrarDialogoCambiarEstado = false
                    }
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { mostrarDialogoCambiarEstado = false }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

// IMPLEMENTACIÓN CORREGIDA DE INFOROW
@Composable
fun InfoRow(icono: ImageVector, titulo: String, valor: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icono,
            contentDescription = null,
            tint = Color(0xFFFF6B9D), // Color Rosa Biscotti
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = titulo,
                fontSize = 12.sp,
                color = Color(0xFF666666) // Gris texto
            )
            Text(
                text = valor,
                fontSize = 16.sp,
                color = Color(0xFF333333), // Gris oscuro
                fontWeight = FontWeight.Medium
            )
        }
    }
}
