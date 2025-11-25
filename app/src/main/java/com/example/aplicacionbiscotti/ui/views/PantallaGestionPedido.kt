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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.aplicacionbiscotti.data.Pedido
import com.example.aplicacionbiscotti.viewmodel.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.background
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaGestionPedidos(
    navController: NavController,
    authViewModel: AuthViewModel,
    pedidoViewModel: PedidoViewModel
) {
    val sesion by authViewModel.estadoSesion.collectAsState()
    val pedidos by pedidoViewModel.todosLosPedidos.collectAsState()
    var filtroEstado by remember { mutableStateOf("Todos") }
    var mostrarEscaner by remember { mutableStateOf(false) }


    val scope = rememberCoroutineScope()

    val rosaBiscotti = Color(0xFFFF6B9D)
    val grisTextoBiscotti = Color(0xFF666666)


    if (sesion?.esAdmin != true) {
        LaunchedEffect(Unit) {
            navController.popBackStack()
        }
        Box(modifier = Modifier.fillMaxSize())
    } else {


        LaunchedEffect(Unit) {
            pedidoViewModel.cargarTodosLosPedidos()
        }

        val pedidosFiltrados = if (filtroEstado == "Todos") {
            pedidos
        } else {
            pedidos.filter { it.estado == filtroEstado }
        }

        val estados = listOf("Todos", "Pendiente", "En preparación", "Completado", "Cancelado")

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text("Gestión de Pedidos", fontWeight = FontWeight.Bold)
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                        }
                    },
                    actions = {
                        IconButton(onClick = { mostrarEscaner = true }) {
                            Icon(
                                Icons.Default.QrCodeScanner,
                                contentDescription = "Escanear QR",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = rosaBiscotti,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White,
                        actionIconContentColor = Color.White
                    )
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Filtros
                ScrollableTabRow(
                    selectedTabIndex = estados.indexOf(filtroEstado),
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = Color.White,
                    contentColor = rosaBiscotti,
                    edgePadding = 0.dp
                ) {
                    estados.forEach { estado ->
                        Tab(
                            selected = filtroEstado == estado,
                            onClick = { filtroEstado = estado },
                            text = {
                                Text(
                                    text = estado,
                                    fontWeight = if (filtroEstado == estado) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        )
                    }
                }


                Card(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFB3D0).copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        EstadisticaItem("Total", "${pedidos.size}", rosaBiscotti)
                        Divider(modifier = Modifier.height(50.dp).width(1.dp))
                        EstadisticaItem(
                            "Pendientes",
                            "${pedidos.count { it.estado == "Pendiente" }}",
                            Color(0xFFFFA726)
                        )
                        Divider(modifier = Modifier.height(50.dp).width(1.dp))
                        EstadisticaItem(
                            "Completados",
                            "${pedidos.count { it.estado == "Completado" }}",
                            Color(0xFF66BB6A)
                        )
                    }
                }


                if (pedidosFiltrados.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("📋", fontSize = 60.sp)
                            Text(
                                "No hay pedidos con este estado",
                                fontSize = 16.sp,
                                color = grisTextoBiscotti,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(pedidosFiltrados) { pedido ->
                            TarjetaPedidoAdmin(
                                pedido = pedido,
                                onClick = {
                                    pedidoViewModel.seleccionarPedido(pedido)
                                    navController.navigate("admin_detalle_pedido")
                                }
                            )
                        }
                    }
                }
            }
        }


        if (mostrarEscaner) {
            androidx.compose.ui.window.Dialog(
                onDismissRequest = { mostrarEscaner = false },
                properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {


                    CamaraQR(
                        onQRDetectado = { codigoQR ->
                            mostrarEscaner = false // Cerrar cámara


                            scope.launch {
                                val pedido = pedidoViewModel.buscarPedidoPorBoleta(codigoQR)
                                if (pedido != null) {
                                    pedidoViewModel.seleccionarPedido(pedido)
                                    navController.navigate("admin_detalle_pedido")
                                } else {
                                    // Opcional: Toast
                                }
                            }
                        },
                        onCerrar = {
                            mostrarEscaner = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun EstadisticaItem(titulo: String, valor: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = valor, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = color)
        Text(text = titulo, fontSize = 14.sp, color = Color(0xFF666666))
    }
}

@Composable
fun TarjetaPedidoAdmin(pedido: Pedido, onClick: () -> Unit) {
    val rosaBiscotti = Color(0xFFFF6B9D)
    val grisOscuroBiscotti = Color(0xFF333333)
    val grisTextoBiscotti = Color(0xFF666666)

    val colorEstado = when (pedido.estado) {
        "Pendiente" -> Color(0xFFFFA726)
        "En preparación" -> Color(0xFF42A5F5)
        "Completado" -> Color(0xFF66BB6A)
        "Cancelado" -> Color(0xFFEF5350)
        else -> Color.Gray
    }
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Pedido #${pedido.id}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = grisOscuroBiscotti)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Cliente: ${pedido.nombreUsuario}", fontSize = 14.sp, color = grisTextoBiscotti)
                    Text(dateFormat.format(Date(pedido.fecha)), fontSize = 12.sp, color = grisTextoBiscotti)
                }
                Surface(color = colorEstado.copy(alpha = 0.2f), shape = RoundedCornerShape(8.dp)) {
                    Text(pedido.estado, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = colorEstado, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Divider()
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Total", fontSize = 14.sp, color = grisTextoBiscotti)
                    Text("$${String.format("%,.0f", pedido.total)}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = rosaBiscotti)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Gestionar", fontSize = 14.sp, color = rosaBiscotti, fontWeight = FontWeight.Bold)
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = rosaBiscotti, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}
