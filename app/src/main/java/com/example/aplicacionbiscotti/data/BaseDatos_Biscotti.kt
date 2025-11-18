package com.example.aplicacionbiscotti.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Usuario::class, Producto::class, Sesion::class, Carrito::class],
    version = 1,
    exportSchema = false
)
abstract class BaseDatos_Biscotti : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao
    abstract fun productoDao(): ProductoDao
    abstract fun sesionDao(): SesionDao
    abstract fun carritoDao(): CarritoDao

    companion object {
        @Volatile
        private var INSTANCE: BaseDatos_Biscotti? = null

        fun getDatabase(context: Context): BaseDatos_Biscotti {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BaseDatos_Biscotti::class.java,
                    "biscotti_database"
                )
                    .addCallback(DatabaseCallback())
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }

    //Insertar datos iniciales
    private class DatabaseCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    poblarDatos(database)
                }
            }
        }

           suspend fun poblarDatos(database: BaseDatos_Biscotti) {
            val usuarioDao = database.usuarioDao()
            val productoDao = database.productoDao()

            // Crear usuario admin
            usuarioDao.insertarUsuario(
                Usuario(
                    nombreUsuario = "adminBiscotti",
                    contrasena = "admin123",
                    email = "admin@biscotti.com",
                    esAdmin = true
                )
            )

            // Crear productos
            productoDao.insertarProducto(
                Producto(
                    nombre = "Galletas de matrimonio",
                    descripcion = "Deliciosas galletas personalizadas",
                    precio = 7490.0,
                    imagenUrl = "fotoanillo",
                    categoria = "Matrimonio"
                )
            )

            productoDao.insertarProducto(
                Producto(
                    nombre = "Galletas de paw patrol",
                    descripcion = "Hermosas galletas de paw patrol",
                    precio = 20000.0,
                    imagenUrl = "paw",
                    categoria = "Infantil"
                )
            )

            productoDao.insertarProducto(
                Producto(
                    nombre = "Galletón de Homero y Marge",
                    descripcion = "Delicioso galletón de los simpson",
                    precio = 10490.0,
                    imagenUrl = "homer",
                    categoria = "Personajes"
                )
            )

            productoDao.insertarProducto(
                Producto(
                    nombre = "Galletas de goku",
                    descripcion = "5 Galletas de Dragon ball",
                    precio = 8990.0,
                    imagenUrl = "dragonball",
                    categoria = "Anime"
                )
            )

            productoDao.insertarProducto(
                Producto(
                    nombre = "Galletas de Lilo y Stich",
                    descripcion = "Galletas personalizadas de Lilo y Stich",
                    precio = 7000.0,
                    imagenUrl = "stitch",
                    categoria = "Disney"
                )
            )

            productoDao.insertarProducto(
                Producto(
                    nombre = "Galletas de navidad",
                    descripcion = "Hermosas galletas de navidad",
                    precio = 5990.0,
                    imagenUrl = "navidad",
                    categoria = "Celebración"
                )
            )
        }
    }
}