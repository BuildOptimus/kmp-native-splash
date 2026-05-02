package com.buildoptimus.kmp_native_splash.image

internal enum class AndroidDensity(val qualifier: String, val baseSizePx: Int) {
    MDPI(qualifier = "mdpi", baseSizePx = 192),
    HDPI(qualifier = "hdpi", baseSizePx = 288),
    XHDPI(qualifier = "xhdpi", baseSizePx = 384),
    XXHDPI(qualifier = "xxhdpi", baseSizePx = 576),
    XXXHDPI(qualifier = "xxxhdpi", baseSizePx = 768);

    companion object {
        fun toVariants(isDark: Boolean = false): List<ScaleVariant> {
            return entries.map { density ->
                val directoryName = if (isDark) {
                    "drawable-night-${density.qualifier}"
                } else {
                    "drawable-${density.qualifier}"
                }

                ScaleVariant(
                    directoryName = directoryName,
                    fileName = "${ImageDefaults.SPLASH_ICON_FILE_PREFIX}.png",
                    sizePx = density.baseSizePx
                )
            }
        }
    }
}