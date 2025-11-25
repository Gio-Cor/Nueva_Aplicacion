package com.example.aplicacionbiscotti.data

import android.content.Context
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class GeneradorBoleta {

    companion object {
        fun generarBoletaPDF(
            context: Context,
            pedido: Pedido,
            detalles: List<DetallePedido>
        ): String? {
            try {
                val pdfDocument = PdfDocument()
                val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4
                val page = pdfDocument.startPage(pageInfo)
                val canvas = page.canvas

                val paint = Paint()
                paint.textSize = 12f

                var y = 50f

                // Título
                paint.textSize = 20f
                paint.isFakeBoldText = true
                canvas.drawText("BISCOTTI CORDANO", 50f, y, paint)
                y += 30f

                paint.textSize = 14f
                paint.isFakeBoldText = false
                canvas.drawText("Galletas Artesanales", 50f, y, paint)
                y += 40f

                // Información de boleta
                paint.textSize = 12f
                canvas.drawText("BOLETA DE VENTA", 50f, y, paint)
                y += 25f

                canvas.drawText("N° Boleta: ${pedido.numeroBoleta}", 50f, y, paint)
                y += 20f

                val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                canvas.drawText("Fecha: ${dateFormat.format(Date(pedido.fecha))}", 50f, y, paint)
                y += 20f

                canvas.drawText("Cliente: ${pedido.nombreUsuario}", 50f, y, paint)
                y += 40f

                // Línea separadora
                canvas.drawLine(50f, y, 545f, y, paint)
                y += 30f

                // Encabezados de tabla
                paint.isFakeBoldText = true
                canvas.drawText("Producto", 50f, y, paint)
                canvas.drawText("Cant.", 320f, y, paint)
                canvas.drawText("Precio", 400f, y, paint)
                canvas.drawText("Subtotal", 480f, y, paint)
                y += 25f

                paint.isFakeBoldText = false
                canvas.drawLine(50f, y, 545f, y, paint)
                y += 20f

                // Detalles de productos
                for (detalle in detalles) {
                    canvas.drawText(detalle.nombreProducto, 50f, y, paint)
                    canvas.drawText("${detalle.cantidad}", 330f, y, paint)
                    canvas.drawText("$${String.format("%.0f", detalle.precioUnitario)}", 400f, y, paint)
                    canvas.drawText("$${String.format("%.0f", detalle.subtotal)}", 480f, y, paint)
                    y += 20f
                }

                y += 10f
                canvas.drawLine(50f, y, 545f, y, paint)
                y += 30f

                // Total
                paint.textSize = 14f
                paint.isFakeBoldText = true
                canvas.drawText("TOTAL:", 400f, y, paint)
                canvas.drawText("$${String.format("%,.0f", pedido.total)}", 480f, y, paint)
                y += 40f

                // Método de pago
                paint.textSize = 12f
                paint.isFakeBoldText = false
                canvas.drawText("Método de pago: ${pedido.metodoPago}", 50f, y, paint)
                y += 30f

                // Pie de página
                y = 780f
                paint.textSize = 10f
                canvas.drawText("Gracias por su compra", 50f, y, paint)
                canvas.drawText("¡Vuelva pronto!", 50f, y + 15f, paint)

                pdfDocument.finishPage(page)

                // Guardar archivo
                val directory = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    "Biscotti"
                )
                if (!directory.exists()) {
                    directory.mkdirs()
                }

                val fileName = "Boleta_${pedido.numeroBoleta}.pdf"
                val file = File(directory, fileName)

                val outputStream = FileOutputStream(file)
                pdfDocument.writeTo(outputStream)
                pdfDocument.close()
                outputStream.close()

                return file.absolutePath

            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }
        }
    }
}