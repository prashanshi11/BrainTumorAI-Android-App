package com.example.braintumorai.utils

import android.content.Context
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

object ImageUtils {

    fun uriToMultipart(context: Context, uri: Uri): MultipartBody.Part {

        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File(context.cacheDir, "upload.jpg")

        val outputStream = FileOutputStream(file)

        inputStream?.copyTo(outputStream)

        outputStream.close()
        inputStream?.close()

        val requestFile =
            file.asRequestBody("image/jpeg".toMediaTypeOrNull())

        return MultipartBody.Part.createFormData(
            "file",
            file.name,
            requestFile
        )
    }
}