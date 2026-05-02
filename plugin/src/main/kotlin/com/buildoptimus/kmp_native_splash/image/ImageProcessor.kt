package com.buildoptimus.kmp_native_splash.image

import java.awt.Image
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

internal class ImageProcessor {
    fun read(source: File): ImageInfo {
        val image = ImageIO.read(source)
            ?: throw IllegalArgumentException("Cannot read image: ${source.absolutePath}")
        return ImageInfo(width = image.width, height = image.height, image = image)
    }

    fun scale(image: BufferedImage, size: Int): BufferedImage = scale(image = image, width = size, height = size)

    fun scale(image: BufferedImage, width: Int, height: Int): BufferedImage {
        val scaledImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH)
        val bufferedImage = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
        val graphics = bufferedImage.createGraphics()

        try {
            graphics.drawImage(scaledImage, 0, 0, null)
        } finally {
            graphics.dispose()
        }

        return bufferedImage
    }

    fun write(image: BufferedImage, outputDirectory: File, directoryName: String, fileName: String) {
        val directory = if (directoryName == ".") {
            outputDirectory
        } else {
            outputDirectory.resolve(directoryName)
        }

        directory.mkdirs()

        val file = directory.resolve(fileName)

        ImageIO.write(image, file.extension, file)
    }
}