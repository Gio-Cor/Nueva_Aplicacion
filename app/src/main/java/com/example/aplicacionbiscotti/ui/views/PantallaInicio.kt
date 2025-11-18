package com.example.aplicacionbiscotti.ui.views

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.navigation.NavController
import com.example.aplicacionbiscotti.viewmodel.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PantallaInicio(
    navController: NavController,
    authViewModel: AuthViewModel,
    productoViewModel: ProductoViewModel
) {
    val sesion by authViewModel.estadoSesion.collectAsState()
    val productos by productoViewModel.productos.collectAsState()
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    val rosaBiscotti = Color(0xFFFF6B9D)
    val rosaClaroBiscotti = Color(0xFFFFB3D0)
    val blancoBiscotti = Color(0xFFFFFFFF)
    val grisTextoBiscotti = Color(0xFF666666)
    val grisOscuroBiscotti = Color(0xFF333333)

    val pagerState = rememberPagerState(pageCount = { productos.take(5).size })
    val scope = rememberCoroutineScope()

    // Auto-scroll del carrusel
    LaunchedEffect(pagerState.currentPage) {
        delay(3000)
        if (pagerState.pageCount > 0) {
            val nextPage = (pagerState.currentPage + 1) % pagerState.pageCount
            scope.launch {
                pagerState.animateScrollToPage(nextPage)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(blancoBiscotti)
    ) {
        // Header con navegación
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(rosaBiscotti)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Biscotti Cordano",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Hola, ${sesion?.nombreUsuario ?: "Usuario"} 👋",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }

                Row {
                    IconButton(
                        onClick = { navController.navigate("carrito") }
                    ) {
                        Icon(
                            Icons.Default.ShoppingCart,
                            contentDescription = "Carrito",
                            tint = Color.White
                        )
                    }

                    if (sesion?.esAdmin == true) {
                        IconButton(
                            onClick = { navController.navigate("panel_admin") }
                        ) {
                            Icon(
                                Icons.Default.Settings,
                                contentDescription = "Admin",
                                tint = Color.White
                            )
                        }
                    }

                    IconButton(
                        onClick = {
                            authViewModel.cerrarSesion()
                            navController.navigate("login") {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    ) {
                        Icon(
                            Icons.Default.ExitToApp,
                            contentDescription = "Cerrar sesión",
                            tint = Color.White
                        )
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Sección de bienvenida
            Column(
                modifier = Modifier.padding(horizontal = 24.dp)
            ) {
                Text(
                    text = "Bienvenido a Biscotti Cordano",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = grisOscuroBiscotti,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Tus galletas artesanales favoritas, hechas con amor y los mejores ingredientes",
                    fontSize = 16.sp,
                    color = grisTextoBiscotti,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Carrusel de productos destacados
            if (productos.isNotEmpty()) {
                Text(
                    text = "✨ Productos Destacados",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = grisOscuroBiscotti,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(350.dp),
                    contentPadding = PaddingValues(horizontal = 32.dp),
                    pageSpacing = 16.dp
                ) { page ->
                    val producto = productos.take(5)[page]
                    val pageOffset = (
                            (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
                            ).absoluteValue

                    Card(
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                val scale = lerp(0.85f, 1f, 1f - pageOffset.coerceIn(0f, 1f))
                                scaleX = scale
                                scaleY = scale
                                alpha = lerp(0.5f, 1f, 1f - pageOffset.coerceIn(0f, 1f))
                            }
                            .clickable {
                                navController.navigate("productos")
                            },
                        shape = RoundedCornerShape(20.dp),
                        elevation = CardDefaults.cardElevation(8.dp)
                    ) {
                        Box {
                            Image(
                                painter = painterResource(
                                    id = context.resources.getIdentifier(
                                        producto.imagenUrl,
                                        "drawable",
                                        context.packageName
                                    )
                                ),
                                contentDescription = producto.nombre,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                Color.Transparent,
                                                Color.Black.copy(alpha = 0.7f)
                                            )
                                        )
                                    )
                            )

                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(20.dp)
                            ) {
                                Text(
                                    text = producto.nombre,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = producto.descripcion,
                                    fontSize = 14.sp,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "$${String.format("%,.0f", producto.precio)}",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = rosaClaroBiscotti
                                )
                            }
                        }
                    }
                }

                // Indicadores del carrusel
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(pagerState.pageCount) { iteration ->
                        val color = if (pagerState.currentPage == iteration)
                            rosaBiscotti else grisTextoBiscotti.copy(alpha = 0.3f)
                        Box(
                            modifier = Modifier
                                .padding(4.dp)
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(color)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Botón ver productos
            Button(
                onClick = { navController.navigate("productos") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = rosaBiscotti
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    Icons.Default.ShoppingCart,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text("Ver Todos los Productos", fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Sección de características
            Column(
                modifier = Modifier.padding(horizontal = 24.dp)
            ) {
                Text(
                    text = "¿Por qué elegirnos?",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = grisOscuroBiscotti
                )

                Spacer(modifier = Modifier.height(16.dp))

                CaracteristicaItem(
                    titulo = "Artesanales",
                    descripcion = "Hechas a mano con dedicación"
                )

                CaracteristicaItem(
                    titulo = "Con Amor",
                    descripcion = "Cada galleta lleva nuestro cariño"
                )

                CaracteristicaItem(
                    titulo = "Personalizadas",
                    descripcion = "Diseños únicos para cada ocasión"
                )

                CaracteristicaItem(
                    titulo = "Creativas",
                    descripcion = "Diseños innovadores y originales"
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun CaracteristicaItem(
    titulo: String,
    descripcion: String
) {
    val rosaClaroBiscotti = Color(0xFFFFB3D0)
    val grisOscuroBiscotti = Color(0xFF333333)
    val grisTextoBiscotti = Color(0xFF666666)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(rosaClaroBiscotti.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = titulo,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = grisOscuroBiscotti
            )
            Text(
                text = descripcion,
                fontSize = 14.sp,
                color = grisTextoBiscotti
            )
        }
    }
}