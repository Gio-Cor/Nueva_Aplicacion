package com.example.aplicacionbiscotti.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicacionbiscotti.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val database = BaseDatos_Biscotti.getDatabase(application)
    private val usuarioDao = database.usuarioDao()
    private val sesionDao = database.sesionDao()

    private val _estadoSesion = MutableStateFlow<Sesion?>(null)
    val estadoSesion: StateFlow<Sesion?> = _estadoSesion.asStateFlow()

    private val _mensajeError = MutableStateFlow<String?>(null)
    val mensajeError: StateFlow<String?> = _mensajeError.asStateFlow()

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    init {
        verificarSesion()
    }

    private fun verificarSesion() {
        viewModelScope.launch {
            val sesion = sesionDao.obtenerSesion()
            _estadoSesion.value = sesion
        }
    }

    fun iniciarSesion(nombreUsuario: String, contrasena: String) {
        viewModelScope.launch {
            _cargando.value = true
            _mensajeError.value = null

            if (nombreUsuario.isBlank() || contrasena.isBlank()) {
                _mensajeError.value = "Por favor completa todos los campos"
                _cargando.value = false
                return@launch
            }

            val usuario = usuarioDao.iniciarSesion(nombreUsuario, contrasena)
            if (usuario != null) {
                val sesion = Sesion(
                    usuarioId = usuario.id,
                    nombreUsuario = usuario.nombreUsuario,
                    esAdmin = usuario.esAdmin
                )
                sesionDao.guardarSesion(sesion)
                _estadoSesion.value = sesion
            } else {
                _mensajeError.value = "Usuario o contraseña incorrectos"
            }

            _cargando.value = false
        }
    }

    fun registrarUsuario(nombreUsuario: String, email: String, contrasena: String, confirmarContrasena: String) {
        viewModelScope.launch {
            _cargando.value = true
            _mensajeError.value = null

            // Validaciones
            if (nombreUsuario.isBlank() || email.isBlank() || contrasena.isBlank()) {
                _mensajeError.value = "Por favor completa todos los campos"
                _cargando.value = false
                return@launch
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                _mensajeError.value = "Email inválido"
                _cargando.value = false
                return@launch
            }

            if (contrasena.length < 6) {
                _mensajeError.value = "La contraseña debe tener al menos 6 caracteres"
                _cargando.value = false
                return@launch
            }

            if (contrasena != confirmarContrasena) {
                _mensajeError.value = "Las contraseñas no coinciden"
                _cargando.value = false
                return@launch
            }

            // Verificar si el usuario ya existe
            val usuarioExistente = usuarioDao.obtenerUsuarioPorNombre(nombreUsuario)
            if (usuarioExistente != null) {
                _mensajeError.value = "El nombre de usuario ya está en uso"
                _cargando.value = false
                return@launch
            }

            val emailExistente = usuarioDao.obtenerUsuarioPorEmail(email)
            if (emailExistente != null) {
                _mensajeError.value = "El email ya está registrado"
                _cargando.value = false
                return@launch
            }

            // Registrar usuario
            val nuevoUsuario = Usuario(
                nombreUsuario = nombreUsuario,
                email = email,
                contrasena = contrasena,
                esAdmin = false
            )

            usuarioDao.insertarUsuario(nuevoUsuario)

            // Iniciar sesión automáticamente
            iniciarSesion(nombreUsuario, contrasena)
        }
    }

    fun cerrarSesion() {
        viewModelScope.launch {
            sesionDao.cerrarSesion()
            _estadoSesion.value = null
        }
    }

    fun limpiarError() {
        _mensajeError.value = null
    }
}