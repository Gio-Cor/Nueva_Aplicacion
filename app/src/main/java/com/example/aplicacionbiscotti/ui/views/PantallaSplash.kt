package com.example.aplicacionbiscotti.ui.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.aplicacionbiscotti.R
import com.example.aplicacionbiscotti.viewmodel.AuthViewModel
import kotlinx.coroutines.delay

@Composable
fun PantallaSplash(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    LaunchedEffect(Unit) {
        delay(2000)
        val sesion = authViewModel.estadoSesion.value
        if (sesion != null) {
            navController.navigate("inicio") { popUpTo("splash") { inclusive = true } }
        } else {
            navController.navigate("login") { popUpTo("splash") { inclusive = true } }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary), // Fondo Marrón
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Imagen del Logo
            Image(
                painter = painterResource(id = R.drawable.fotologo),
                contentDescription = "Logo de Biscotti Cordano",
                modifier = Modifier.size(200.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Biscotti Cordano",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}