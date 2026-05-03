package com.buildoptimus.kmp_native_splash.template

import com.buildoptimus.kmp_native_splash.image.ImageDefaults


internal object AndroidTemplates {
    fun colorsXml(colorHex: String): String {
        return """
        |<?xml version="1.0" encoding="utf-8"?>
        |<resources>
        |    <color name="splash_background">$colorHex</color>
        |</resources>
        """.trimMargin()
    }

    fun launchBackgroundXml(): String {
        return """
        |<?xml version="1.0" encoding="utf-8"?>
        |<layer-list xmlns:android="http://schemas.android.com/apk/res/android">
        |    <item android:drawable="@color/splash_background" />
        |    <item>
        |        <bitmap
        |            android:gravity="center"
        |            android:src="@drawable/${ImageDefaults.SPLASH_ICON_FILE_PREFIX}" />
        |    </item>
        |</layer-list>
        """.trimMargin()
    }

    fun stylesXml(fullscreen: Boolean = false, isV31: Boolean = false): String {
        val fullscreenItem = if (fullscreen) {
            "\n        |        <item name=\"android:windowFullscreen\">true</item>"
        } else {
            ""
        }

        val items = if (isV31) {
            """
            |        <item name="android:windowSplashScreenBackground">@color/splash_background</item>
            |        <item name="android:windowSplashScreenAnimatedIcon">@drawable/${ImageDefaults.SPLASH_ICON_FILE_PREFIX}</item>$fullscreenItem
            """.trimMargin()
        } else {
            """
            |        <item name="android:windowBackground">@drawable/splash_background</item>$fullscreenItem
            """.trimMargin()
        }

        return """
        |<?xml version="1.0" encoding="utf-8"?>
        |<resources>
        |    <style name="Theme.App.SplashScreen" parent="android:Theme.Light.NoTitleBar">
        |$items
        |    </style>
        |</resources>
        """.trimMargin()
    }
}