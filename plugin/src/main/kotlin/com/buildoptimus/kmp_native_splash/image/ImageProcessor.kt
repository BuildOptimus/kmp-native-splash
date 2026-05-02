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

    fun resize(
        imageInfo: ImageInfo,
        outputDirectory: File,
        variants: List<ScaleVariant>
    ): List<File> {
        return variants.map { variant ->
            val directory = if (variant.directoryName == ".") {
                outputDirectory
            } else {
                outputDirectory.resolve(variant.directoryName)
            }

            directory.mkdirs()

            val file = directory.resolve(variant.fileName)
            val scaledImage = imageInfo.image.scale(variant.sizePx)

            ImageIO.write(scaledImage, file.extension, file)

            file
        }
    }

    private fun BufferedImage.scale(size: Int): BufferedImage = scale(width = size, height = size)

    private fun BufferedImage.scale(width: Int, height: Int): BufferedImage {
        val scaledImage = getScaledInstance(width, height, Image.SCALE_SMOOTH)
        val bufferedImage = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
        val graphics = bufferedImage.createGraphics()

        try {
            graphics.drawImage(scaledImage, 0, 0, null)
        } finally {
            graphics.dispose()
        }

        return bufferedImage
    }
}