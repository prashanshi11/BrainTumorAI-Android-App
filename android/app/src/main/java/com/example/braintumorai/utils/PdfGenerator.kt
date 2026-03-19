package com.example.braintumorai.utils

import android.content.Context
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import java.io.File
import java.io.FileOutputStream

object PdfGenerator {

    fun generate(context: Context, text: String) {
        val pdf = PdfDocument()
        val page = pdf.startPage(PdfDocument.PageInfo.Builder(300, 600, 1).create())

        val canvas = page.canvas
        val paint = Paint()

        canvas.drawText(text, 10f, 25f, paint)

        pdf.finishPage(page)

        val file = File(context.getExternalFilesDir(null), "report.pdf")
        pdf.writeTo(FileOutputStream(file))
        pdf.close()
    }
}
