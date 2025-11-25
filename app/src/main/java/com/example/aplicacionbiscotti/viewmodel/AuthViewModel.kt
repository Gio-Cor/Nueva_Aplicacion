package com.example.aplicacionbiscotti.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicacionbiscotti.data.*
import com.example.aplicacionbiscotti.data.network.RetrofitClient
import com.example.aplicacionbiscotti.data.repository.AuthRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.net.ConnectException
import java.net.SocketTimeoutException

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val authRepository = AuthRepository()
    private val sesionDao = BaseDatos_Biscotti.getDatabase(application).sesionDao()

    private val _estadoSesion = MutableStateFlow<Sesion?>(null)
    val estadoSesion: StateFlow<Sesion?> = _estadoSesion.asStateFlow()

    private val _mensajeError = MutableStateFlow<String?>(null)
    val mensajeError: StateFlow<String?> = _mensajeError.asStateFlow()

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    init {
        verificarSesionLocal()
    }

    private fun verificarSesionLocal() {
        viewModelScope.launch {
            _estadoSesion.value = sesionDao.obtenerSesion()
        }
    }

    fun iniciarSesion(email: String, contrasena: String) {
        viewModelScope.launch {
            _cargando.value = true
            _mensajeError.value = null

            try {
                val response = authRepository.login(email, contrasena)

                if (response.isSuccessful && response.body() != null) {
                    val userResponse = response.body()!!

                    var token = response.headers()["Authorization"]?.removePrefix("Bearer ")

                    if (token.isNullOrEmpty()) {
                        token = userResponse.token 
                    }
                    if (token.isNullOrEmpty()) {
                        token = userResponse.accessToken 
                    }

                    if (token.isNullOrEmpty()) {
                        _mensajeError.value = "Error: El servidor no devolvió un token."
                        _cargando.value = false
                        return@launch
                    }

                    val nuevaSesion = Sesion(
                        usuarioId = userResponse.id ?: 0,
                        nombreUsuario = userResponse.nombreUsuario ?: "",
                        email = email,
                        token = token,
                        esAdmin = userResponse.rol == "ROLE_ADMIN",
                        fotoPerfilRuta = ""
                    )

                    sesionDao.guardarSesion(nuevaSesion)
                    _estadoSesion.value = nuevaSesion

                } else {
                    _mensajeError.value = "Credenciales incorrectas (código: ${response.code()})"
                }

            } catch (e: Exception) {
                manejarErrorConexion(e)
            } finally {
                _cargando.value = false
            }
        }
    }

    fun actualizarFotoPerfil(usuarioId: Int, rutaFoto: String) {
        viewModelScope.launch {
            val sesionActual = _estadoSesion.value

            if (sesionActual != null) {
                val sesionActualizada = sesionActual.copy(fotoPerfilRuta = rutaFoto)
                sesionDao.guardarSesion(sesionActualizada)
                _estadoSesion.value = sesionActualizada
            }
        }
    }

    fun registrarUsuario(username: String, email: String, password: String, confirmarPassword: String) {
        viewModelScope.launch {
            if (password != confirmarPassword) {
                _mensajeError.value = "Las contraseñas no coinciden"
                return@launch
            }
            _cargando.value = true
            _mensajeError.value = null
            try {
                val request = RegisterRequest(
                    username = username,
                    email = email,
                    password = password
                )
                
                val response = authRepository.register(request)
                
                if (response.isSuccessful) {
                    iniciarSesion(email, password)
                } else {
                    _mensajeError.value = "Error al registrar (código: ${response.code()}): ${response.errorBody()?.string() ?: "Sin detalles"}"
                }
            } catch (e: Exception) {
                manejarErrorConexion(e)
            } finally {
                _cargando.value = false
            }
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

    private fun manejarErrorConexion(e: Exception) {
        e.printStackTrace()

        _mensajeError.value = when (e) {
            is SocketTimeoutException ->
                "Tiempo de espera agotado. Revisa tu conexión y el Firewall."
            is ConnectException ->
                "Error de conexión. ¿La IP en RetrofitClient es correcta y el servidor está encendido?"
            else ->
                "Error de red inesperado: ${e.message}"
        }
    }
}