package com.example.onepicture.utils

import android.content.Context
import java.io.File

object FileUtils {
    fun createImageFile(context: Context): File {
        val outputDir = context.cacheDir
        return File(outputDir, "captured_image_${System.currentTimeMillis()}.jpg")
    }
}