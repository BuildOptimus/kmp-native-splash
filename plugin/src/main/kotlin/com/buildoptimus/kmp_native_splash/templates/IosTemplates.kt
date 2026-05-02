package com.buildoptimus.kmp_native_splash.templates

import com.buildoptimus.kmp_native_splash.color.PlatformColor
import com.buildoptimus.kmp_native_splash.image.ImageDefaults
import com.buildoptimus.kmp_native_splash.image.IosScale

internal object IosTemplates {

    fun launchScreenStoryboard(imageWidth: Int, imageHeight: Int): String {
        return """
            |<?xml version="1.0" encoding="UTF-8"?>
            |<document type="com.apple.InterfaceBuilder3.CocoaTouch.Storyboard.XIB" version="3.0" toolsVersion="24128" targetRuntime="iOS.CocoaTouch" propertyAccessControl="none" useAutolayout="YES" launchScreen="YES" useTraitCollections="YES" useSafeAreas="YES" colorMatched="YES" initialViewController="01J-lp-oVM">
            |    <device id="retina6_72" orientation="portrait" appearance="light"/>
            |    <dependencies>
            |        <plugIn identifier="com.apple.InterfaceBuilder.IBCocoaTouchPlugin" version="24112"/>
            |        <capability name="Named colors" minToolsVersion="9.0"/>
            |        <capability name="Safe area layout guides" minToolsVersion="9.0"/>
            |    </dependencies>
            |    <scenes>
            |        <scene sceneID="EHf-IW-A2E">
            |            <objects>
            |                <viewController id="01J-lp-oVM" sceneMemberID="viewController">
            |                    <view key="view" contentMode="scaleToFill" id="Ze5-6b-2t3">
            |                        <rect key="frame" x="0.0" y="0.0" width="393" height="852"/>
            |                        <autoresizingMask key="autoresizingMask" widthSizable="YES" heightSizable="YES"/>
            |                        <subviews>
            |                            <imageView clipsSubviews="YES" userInteractionEnabled="NO" contentMode="scaleAspectFit" image="SplashIcon" translatesAutoresizingMaskIntoConstraints="NO" id="Spx-Lg-1A2">
            |                                <rect key="frame" x="76.666666666666686" y="306" width="240" height="240"/>
            |                            </imageView>
            |                        </subviews>
            |                        <viewLayoutGuide key="safeArea" id="Bcu-3y-fUS"/>
            |                        <color key="backgroundColor" name="SplashBackground"/>
            |                        <constraints>
            |                            <constraint firstItem="Spx-Lg-1A2" firstAttribute="centerX" secondItem="Bcu-3y-fUS" secondAttribute="centerX" id="M5i-Xg-R4P"/>
            |                            <constraint firstItem="Spx-Lg-1A2" firstAttribute="centerY" secondItem="Bcu-3y-fUS" secondAttribute="centerY" id="TbT-Y9-P8o"/>
            |                        </constraints>
            |                    </view>
            |                </viewController>
            |                <placeholder placeholderIdentifier="IBFirstResponder" id="iYj-Kq-Ea1" userLabel="First Responder" sceneMemberID="firstResponder"/>
            |            </objects>
            |            <point key="canvasLocation" x="0.0" y="0.0"/>
            |        </scene>
            |    </scenes>
            |    <resources>
            |        <image name="SplashIcon" width="$imageWidth" height="$imageHeight"/>
            |        <namedColor name="SplashBackground"/>
            |    </resources>
            |</document>
        """.trimMargin()
    }

    fun colorSetContentsJson(color: PlatformColor, darkColor: PlatformColor? = null): String {
        val lightBlock = """
            |    {
            |      "color" : ${colorComponentsJson(color)},
            |      "idiom" : "universal"${if (darkColor != null) ",\n            |      \"appearances\" : [ { \"appearance\" : \"luminosity\", \"value\" : \"light\" } ]" else ""}
            |    }""".trimMargin()

        val darkBlock = if (darkColor != null) {
            """,
            |    {
            |      "color" : ${colorComponentsJson(darkColor)},
            |      "idiom" : "universal",
            |      "appearances" : [ { "appearance" : "luminosity", "value" : "dark" } ]
            |    }""".trimMargin()
        } else ""

        return """
            |{
            |  "colors" : [
            |$lightBlock$darkBlock
            |  ],
            |  "info" : {
            |    "author" : "kmp-native-splash",
            |    "version" : 1
            |  }
            |}
        """.trimMargin()
    }

    fun imageSetContentsJson(
        hasDarkVariant: Boolean = false,
        isVector: Boolean = false,
        lightExtension: String = "png",
        darkExtension: String = "png"
    ): String {
        if (isVector) {
            val lightVariant = """
                |    {
                |      "filename" : "${ImageDefaults.SPLASH_ICON_FILE_PREFIX}.$lightExtension",
                |      "idiom" : "universal"
                |    }""".trimMargin()

            return """
                |{
                |  "images" : [
                |$lightVariant
                |  ],
                |  "info" : {
                |    "author" : "kmp-native-splash",
                |    "version" : 1
                |  },
                |  "properties" : {
                |    "preserves-vector-representation" : true
                |  }
                |}
            """.trimMargin()
        }

        val variants = IosScale.entries.map { scale ->
            val appearanceTag = if (hasDarkVariant) {
                ",\n|      \"appearances\" : [ { \"appearance\" : \"luminosity\", \"value\" : \"light\" } ]"
            } else ""

            val lightVariant = """
                |    {
                |      "filename" : "${ImageDefaults.SPLASH_ICON_FILE_PREFIX}_${scale.suffix}.$lightExtension",
                |      "idiom" : "universal",
                |      "scale" : "${scale.suffix}"$appearanceTag
                |    }""".trimMargin()

            if (hasDarkVariant) {
                val darkVariant = """
                    |    {
                    |      "filename" : "${ImageDefaults.SPLASH_ICON_FILE_PREFIX}_dark_${scale.suffix}.$darkExtension",
                    |      "idiom" : "universal",
                    |      "scale" : "${scale.suffix}",
                    |      "appearances" : [ { "appearance" : "luminosity", "value" : "dark" } ]
                    |    }""".trimMargin()
                "$lightVariant,\n$darkVariant"
            } else {
                lightVariant
            }
        }.joinToString(",\n")

        return """
            |{
            |  "images" : [
            |$variants
            |  ],
            |  "info" : {
            |    "author" : "kmp-native-splash",
            |    "version" : 1
            |  }
            |}
        """.trimMargin()
    }

    fun assetCatalogRootContentsJson(): String {
        return """
            |{
            |  "info" : {
            |    "author" : "kmp-native-splash",
            |    "version" : 1
            |  }
            |}
        """.trimMargin()
    }

    private fun colorComponentsJson(color: PlatformColor): String {
        val components = color.toIosComponents()

        with("%.3f") {
            val red = format(components.red)
            val green = format(components.green)
            val blue = format(components.blue)
            val alpha = format(components.alpha)

            return """{
            |        "color-space" : "srgb",
            |        "components" : { "alpha" : "$alpha", "blue" : "$blue", "green" : "$green", "red" : "$red" }
            |      }""".trimMargin()
        }

    }
}