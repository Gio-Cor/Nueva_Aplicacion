package com.example.aplicacionbiscotti.data

import androidx.room.*

@Dao
interface SesionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardarSesion(sesion: Sesion)

    @Query("SELECT * FROM sesion WHERE id = 1")
    suspend fun obtenerSesion(): Sesion?

    @Query("DELETE FROM sesion")
    suspend fun cerrarSesion()
}