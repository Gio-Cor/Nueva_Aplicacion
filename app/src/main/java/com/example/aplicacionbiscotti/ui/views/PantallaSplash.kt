package com.example.aplicacionbiscotti.ui.views

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.aplicacionbiscotti.viewmodel.AuthViewModel
import kotlinx.coroutines.delay

@Composable
fun PantallaSplash(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val sesion by authViewModel.estadoSesion.collectAsState()
    val escala = remember { Animatable(0f) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        escala.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )

        delay(2000)

        if (sesion != null) {
            navController.navigate("inicio") {
                popUpTo("splash") { inclusive = true }
            }
        } else {
            navController.navigate("login") {
                popUpTo("splash") { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFF6B9D)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.scale(escala.value)
        ) {
            Image(
                painter = painterResource(
                    id = context.resources.getIdentifier(
                        "logopagina",
                        "drawable",
                        context.packageName
                    )
                ),
                contentDescription = "Logo Biscotti",
                modifier = Modifier
                    .size(120.dp)
                    .padding(bottom = 16.dp),
                contentScale = ContentScale.Fit
            )

            Text(
                text = "Biscotti Cordano",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "Galletas artesanales",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.9f)
            )
        }
    }
}