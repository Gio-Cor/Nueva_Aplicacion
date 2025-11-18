package com.example.aplicacionbiscotti.ui.views

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
fun PantallaRegistro(
    navController: NavController,
    viewModel: AuthViewModel
) {
    var nombreUsuario by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var confirmarContrasena by remember { mutableStateOf("") }
    var mostrarContrasena by remember { mutableStateOf(false) }

    val sesion by viewModel.estadoSesion.collectAsState()
    val mensajeError by viewModel.mensajeError.collectAsState()
    val cargando by viewModel.cargando.collectAsState()

    val rosaBiscotti = Color(0xFFFF6B9D)
    val blancoBiscotti = Color(0xFFFFFFFF)
    val grisTextoBiscotti = Color(0xFF666666)

    LaunchedEffect(sesion) {
        if (sesion != null) {
            navController.navigate("inicio") {
                popUpTo("registro") { inclusive = true }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(blancoBiscotti)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .background(rosaBiscotti),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.padding(start = 16.dp)
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                }
                Text(
                    text = "Crear Cuenta",
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
            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = nombreUsuario,
                onValueChange = { nombreUsuario = it },
                label = { Text("Nombre de usuario") },
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

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                leadingIcon = {
                    Icon(Icons.Default.Email, contentDescription = null)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = rosaBiscotti,
                    focusedLabelColor = rosaBiscotti,
                    cursorColor = rosaBiscotti
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(modifier = Modifier.height(16.dp))

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

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = confirmarContrasena,
                onValueChange = { confirmarContrasena = it },
                label = { Text("Confirmar contraseña") },
                leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = null)
                },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = rosaBiscotti,
                    focusedLabelColor = rosaBiscotti,
                    cursorColor = rosaBiscotti
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            AnimatedVisibility(visible = mensajeError != null) {
                Text(
                    text = mensajeError ?: "",
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    viewModel.registrarUsuario(nombreUsuario, email, contrasena, confirmarContrasena)
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
                    Text("Registrarse", fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "¿Ya tienes cuenta? ",
                    color = grisTextoBiscotti
                )
                Text(
                    text = "Inicia sesión",
                    color = rosaBiscotti,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}