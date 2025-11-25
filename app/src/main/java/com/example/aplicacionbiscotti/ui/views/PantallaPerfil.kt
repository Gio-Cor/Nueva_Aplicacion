package com.example.aplicacionbiscotti.ui.views

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.aplicacionbiscotti.viewmodel.AuthViewModel
import com.google.android.gms.location.LocationServices
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPerfil(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val sesion by authViewModel.estadoSesion.collectAsState()
    val context = LocalContext.current

    var mostrarQR by remember { mutableStateOf(false) }
    var resultadoQR by remember { mutableStateOf("") }
    var ubicacion by remember { mutableStateOf("Obteniendo ubicación...") }

    val esAdmin = sesion?.email == "admin@biscotti.com"

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            guardarFotoEnAlmacenamiento(context, it) { rutaArchivo ->
                sesion?.usuarioId?.let { usuarioId ->
                    authViewModel.actualizarFotoPerfil(usuarioId, rutaArchivo)
                }
            }
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) obtenerUbicacion(context) { lat, lon -> ubicacion = "Lat: ${"%.4f".format(lat)}, Lon: ${"%.4f".format(lon)}" }
    }
    val cameraPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) mostrarQR = true
    }

    if (mostrarQR) {
        androidx.compose.ui.window.Dialog(
            onDismissRequest = { mostrarQR = false },
            properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
                CamaraQR(
                    onQRDetectado = { codigo ->
                        mostrarQR = false
                        resultadoQR = codigo
                        Toast.makeText(context, "Código: $codigo", Toast.LENGTH_SHORT).show()
                    },
                    onCerrar = { mostrarQR = false }
                )
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                },
                actions = {
                    if (esAdmin) {
                        IconButton(onClick = { navController.navigate("panel_admin") }) {
                            Icon(Icons.Default.Settings, contentDescription = "Panel Admin")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .background(MaterialTheme.colorScheme.background)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))

            Box(modifier = Modifier.size(150.dp), contentAlignment = Alignment.BottomEnd) {
                val rutaFoto = sesion?.fotoPerfilRuta
                if (!rutaFoto.isNullOrEmpty() && File(rutaFoto).exists()) {
                    AsyncImage(
                        model = File(rutaFoto),
                        contentDescription = "Foto de perfil",
                        modifier = Modifier.size(150.dp).clip(CircleShape).border(4.dp, MaterialTheme.colorScheme.primary, CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier.size(150.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)).border(4.dp, MaterialTheme.colorScheme.primary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, "Sin foto", Modifier.size(80.dp), tint = MaterialTheme.colorScheme.primary)
                    }
                }
                FloatingActionButton(
                    onClick = { imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                    modifier = Modifier.size(45.dp),
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Edit, "Cambiar foto", tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(20.dp))
                }
            }

            Spacer(Modifier.height(24.dp))

            sesion?.let { s ->
                Text(s.nombreUsuario, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                Text(s.email, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                if (esAdmin) {
                    Text("👑 Administrador", fontSize = 16.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(32.dp))

            Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(4.dp)) {
                Column(Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, "Ubicación", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Mi Ubicación", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            Text(ubicacion, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = {
                            when {
                                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED -> {
                                    obtenerUbicacion(context) { lat, lon -> ubicacion = "Lat: ${"%.4f".format(lat)}, Lon: ${"%.4f".format(lon)}" }
                                }
                                else -> {
                                    locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.MyLocation, null, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Actualizar Ubicación")
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(4.dp)) {
                Column(Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.QrCodeScanner, "QR", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Escanear QR", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            if (resultadoQR.isNotEmpty()) {
                                Text("Último: $resultadoQR", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            } else {
                                Text("No has escaneado ningún código", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = {
                            when {
                                ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED -> {
                                    mostrarQR = true
                                }
                                else -> {
                                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.CameraAlt, null, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Abrir Escáner")
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = {
                    authViewModel.cerrarSesion()
                    navController.navigate("login") {
                        popUpTo("inicio") { inclusive = true }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.8f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cerrar Sesión")
            }
        }
    }
}


fun guardarFotoEnAlmacenamiento(context: Context, uri: Uri, onRutaGuardada: (String) -> Unit) {
    try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val archivoSalida = File(context.filesDir, "perfil_${System.currentTimeMillis()}.jpg")
        val outputStream = archivoSalida.outputStream()
        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()
        onRutaGuardada(archivoSalida.absolutePath)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun obtenerUbicacion(context: Context, onUbicacionObtenida: (Double, Double) -> Unit) {
    try {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                onUbicacionObtenida(location.latitude, location.longitude)
            }
        }
    } catch (e: SecurityException) {
        e.printStackTrace()
    }
}
