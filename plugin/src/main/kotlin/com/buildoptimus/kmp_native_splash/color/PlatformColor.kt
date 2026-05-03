package com.buildoptimus.kmp_native_splash.color

import java.io.Serializable

internal data class PlatformColor(
    val red: Int = 0,
    val green: Int = 0,
    val blue: Int = 0,
    val alpha: Int = 255
) : Serializable {
    fun toAndroidHex(): String {
        return "#%02X%02X%02X%02X".format(alpha, red, green, blue)
    }

    fun toIosComponents(): IosColorComponents {
        return IosColorComponents(
            red = red / 255f,
            green = green / 255f,
            blue = blue / 255f,
            alpha = alpha / 255f
        )
    }

    companion object {
        fun parseHex(value: String): PlatformColor {
            val raw = value.removePrefix("#")

            return try {
                when (raw.length) {
                    3 -> {
                        val red = raw[0].digitToInt(16)
                        val green = raw[1].digitToInt(16)
                        val blue = raw[2].digitToInt(16)
                        PlatformColor(red * 17, green * 17, blue * 17)
                    }

                    6 -> {
                        PlatformColor(
                            red = raw.substring(0, 2).toInt(16),
                            green = raw.substring(2, 4).toInt(16),
                            blue = raw.substring(4, 6).toInt(16),
                        )
                    }

                    8 -> {
                        PlatformColor(
                            red = raw.substring(2, 4).toInt(16),
                            green = raw.substring(4, 6).toInt(16),
                            blue = raw.substring(6, 8).toInt(16),
                            alpha = raw.substring(0, 2).toInt(16),
                        )
                    }

                    else -> {
                        throw IllegalArgumentException(
                            "Invalid hex color '$value': expected #RGB, #RRGGBB, or #AARRGGBB."
                        )
                    }
                }
            } catch (exception: NumberFormatException) {
                throw IllegalArgumentException(
                    "Invalid hex color '$value': contains non-hex characters.",
                    exception
                )
            }
        }
    }
}
