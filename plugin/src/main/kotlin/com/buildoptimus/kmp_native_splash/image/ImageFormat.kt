package com.buildoptimus.kmp_native_splash.image

import java.io.File
import java.util.*

internal enum class ImageFormat {
    RASTER,
    XML,
    VECTOR
}

internal fun File.imageFormat(): ImageFormat {
    return when (extension.lowercase(Locale.US)) {
        "xml" -> ImageFormat.XML
        "svg", "pdf" -> ImageFormat.VECTOR
        else -> ImageFormat.RASTER
    }
}