package com.buildoptimus.kmp_native_splash.image

internal enum class IosScale(val suffix: String, val multiplier: Int) {
    X1(suffix = "1x", multiplier = 1),
    X2(suffix = "2x", multiplier = 2),
    X3(suffix = "3x", multiplier = 3);

    companion object {
        fun toVariants(isDark: Boolean = false): List<ScaleVariant> {
            return entries.map { scale ->
                val size = ImageDefaults.IOS_BASE_SIZE_PX * scale.multiplier

                val fileName = if (isDark) {
                    "${ImageDefaults.SPLASH_ICON_FILE_PREFIX}_dark_${scale.suffix}.png"
                } else {
                    "${ImageDefaults.SPLASH_ICON_FILE_PREFIX}_${scale.suffix}.png"
                }

                ScaleVariant(
                    directoryName = ".",
                    fileName = fileName,
                    sizePx = size
                )
            }
        }
    }
}