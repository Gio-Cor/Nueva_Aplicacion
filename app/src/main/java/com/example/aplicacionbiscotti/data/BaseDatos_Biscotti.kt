package com.example.aplicacionbiscotti.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        Usuario::class,
        Producto::class,
        Sesion::class,
        Carrito::class,
        Pedido::class,
        DetallePedido::class
    ],
    version = 14,
    exportSchema = false
)
abstract class BaseDatos_Biscotti : RoomDatabase() {

    abstract fun pedidoDao(): PedidoDao
    abstract fun detallePedidoDao(): DetallePedidoDao
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
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}