package com.example.aplicacionbiscotti.ui.views

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.aplicacionbiscotti.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaLogin(
    navController: NavController,
    viewModel: AuthViewModel
) {
    var nombreUsuario by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var mostrarContrasena by remember { mutableStateOf(false) }

    val sesion by viewModel.estadoSesion.collectAsState()
    val mensajeError by viewModel.mensajeError.collectAsState()
    val cargando by viewModel.cargando.collectAsState()

    val rosaBiscotti = Color(0xFFFF6B9D)
    val blancoBiscotti = Color(0xFFFFFFFF)
    val grisTextoBiscotti = Color(0xFF666666)
    val grisOscuroBiscotti = Color(0xFF333333)
    val rosaClaroBiscotti = Color(0xFFFFB3D0)
    val rosaOscuroBiscotti = Color(0xFFE91E63)

    LaunchedEffect(sesion) {
        if (sesion != null) {
            navController.navigate("inicio") {
                popUpTo("login") { inclusive = true }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(blancoBiscotti)
    ) {
        // Header rosa
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(rosaBiscotti),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "🍪",
                    fontSize = 60.sp
                )
                Text(
                    text = "Biscotti Cordano",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        // Formulario
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Iniciar Sesión",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = grisOscuroBiscotti
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Campo usuario
            OutlinedTextField(
                value = nombreUsuario,
                onValueChange = { nombreUsuario = it },
                label = { Text("Usuario") },
                leadingIcon = {
                    Icon(Icons.Default.Person, contentDescription = null)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = rosaBiscotti,
                    focusedLabelColor = rosaBiscotti,
                    cursorColor = rosaBiscotti
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo contraseña
            OutlinedTextField(
                value = contrasena,
                onValueChange = { contrasena = it },
                label = { Text("Contraseña") },
                leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = null)
                },
                trailingIcon = {
                    TextButton(onClick = { mostrarContrasena = !mostrarContrasena }) {
                        Text(
                            text = if (mostrarContrasena) "Ocultar" else "Mostrar",
                            color = rosaBiscotti,
                            fontSize = 14.sp
                        )
                    }
                },
                visualTransformation = if (mostrarContrasena)
                    VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = rosaBiscotti,
                    focusedLabelColor = rosaBiscotti,
                    cursorColor = rosaBiscotti
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            // Mensaje de error
            AnimatedVisibility(visible = mensajeError != null) {
                Text(
                    text = mensajeError ?: "",
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botón iniciar sesión
            Button(
                onClick = {
                    viewModel.iniciarSesion(nombreUsuario, contrasena)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = rosaBiscotti
                ),
                shape = RoundedCornerShape(12.dp),
                enabled = !cargando
            ) {
                if (cargando) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text("Iniciar Sesión", fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Link a registro
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "¿No tienes cuenta? ",
                    color = grisTextoBiscotti
                )
                Text(
                    text = "Regístrate",
                    color = rosaBiscotti,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable {
                        navController.navigate("registro")
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Info de admin
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = rosaClaroBiscotti.copy(alpha = 0.3f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "💡 Para probar:",
                        fontWeight = FontWeight.Bold,
                        color = rosaOscuroBiscotti
                    )
                    Text(
                        text = "Usuario:admin\nContraseña:admin123",
                        fontSize = 14.sp,
                        color = grisTextoBiscotti
                    )
                }
            }
        }
    }
}