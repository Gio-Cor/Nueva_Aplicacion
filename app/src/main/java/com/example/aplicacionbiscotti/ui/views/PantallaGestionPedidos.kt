package com.example.aplicacionbiscotti.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.aplicacionbiscotti.data.Pedido
import com.example.aplicacionbiscotti.viewmodel.PedidoViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaGestionPedidos(
    navController: NavController,
    pedidoViewModel: PedidoViewModel
) {
    val listaPedidos by pedidoViewModel.todosLosPedidos.collectAsState(initial = emptyList())
    
    LaunchedEffect(Unit) {
        pedidoViewModel.cargarTodosLosPedidos()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Pedidos", fontWeight = FontWeight.Bold) },
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
        }
    ) { paddingValues ->
        if (listaPedidos.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues).background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay pedidos registrados", color = MaterialTheme.colorScheme.onBackground)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues).background(MaterialTheme.colorScheme.background),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(listaPedidos) { pedido ->
                    ItemPedidoAdmin(pedido = pedido, viewModel = pedidoViewModel)
                }
            }
        }
    }
}

@Composable
fun ItemPedidoAdmin(pedido: Pedido, viewModel: PedidoViewModel) {
    var expandido by remember { mutableStateOf(false) }
    val fechaFormato = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    
    Card(
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = pedido.numeroBoleta,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                EstadoChip(estado = pedido.estado)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text("Cliente: ${pedido.nombreUsuario}", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("Fecha: ${fechaFormato.format(Date(pedido.fecha))}", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("Total: $${String.format("%,.0f", pedido.total)}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

            Spacer(modifier = Modifier.height(12.dp))

            Box {
                Button(
                    onClick = { expandido = true },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.onSurfaceVariant)
                ) {
                    Text("Cambiar Estado")
                    Icon(Icons.Default.ArrowDropDown, null)
                }

                DropdownMenu(
                    expanded = expandido,
                    onDismissRequest = { expandido = false }
                ) {
                    listOf("Pendiente", "En preparación", "Completado", "Cancelado").forEach { nuevoEstado ->
                        DropdownMenuItem(
                            text = { Text(nuevoEstado) },
                            onClick = {
                                viewModel.actualizarEstadoPedido(pedido.id, nuevoEstado)
                                expandido = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EstadoChip(estado: String) {
    val colorFondo = when (estado) {
        "Pendiente" -> Color(0xFFFFE082) // Amarillo
        "En preparación" -> Color(0xFF90CAF9) // Azul claro
        "Completado" -> Color(0xFFA5D6A7) // Verde claro
        "Cancelado" -> Color(0xFFEF9A9A) // Rojo claro
        else -> Color.LightGray
    }
    
    Surface(
        color = colorFondo,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.padding(4.dp)
    ) {
        Text(
            text = estado,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}