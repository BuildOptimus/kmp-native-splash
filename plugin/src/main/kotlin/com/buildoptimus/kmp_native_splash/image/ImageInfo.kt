package com.buildoptimus.kmp_native_splash.image

import java.awt.image.BufferedImage

internal data class ImageInfo(
    val width: Int,
    val height: Int,
    val image: BufferedImage
) {
    constructor(size: Int, image: BufferedImage) : this(size, size, image)
}