package com.example.aplicacionbiscotti.data

import androidx.room.*
import androidx.room.Dao

@Dao
interface UsuarioDao {
    @Insert
    suspend fun insertarUsuario(usuario: Usuario)

    @Query("SELECT * FROM usuarios WHERE nombreUsuario = :nombreUsuario AND contrasena = :contrasena")
    suspend fun iniciarSesion(nombreUsuario: String, contrasena: String): Usuario?

    @Query("SELECT * FROM usuarios WHERE nombreUsuario = :nombreUsuario")
    suspend fun obtenerUsuarioPorNombre(nombreUsuario: String): Usuario?

    @Query("SELECT * FROM usuarios WHERE email = :email")
    suspend fun obtenerUsuarioPorEmail(email: String): Usuario?
}