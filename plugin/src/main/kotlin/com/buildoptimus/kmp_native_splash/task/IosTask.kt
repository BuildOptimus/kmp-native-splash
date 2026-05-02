package com.buildoptimus.kmp_native_splash.task

import com.buildoptimus.kmp_native_splash.image.*
import com.buildoptimus.kmp_native_splash.templates.IosTemplates
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException

internal abstract class IosTask : DefaultTask(), SplashTask {
    init {
        group = TaskDefaults.GROUP
        description = "Generates iOS splash screen resources."
    }

    @TaskAction
    fun generate() {
        val targetDirectory = outputDirectory.get().asFile
        targetDirectory.mkdirs()

        validateInputs()

        val imageProcessor = ImageProcessor()
        generateStoryboard(targetDirectory = targetDirectory, imageProcessor = imageProcessor)

        val assetsDirectory = targetDirectory.resolve("Assets.xcassets").apply { mkdirs() }
        val assetsFile = assetsDirectory.resolve("Contents.json")
        assetsFile.writeText(IosTemplates.assetCatalogRootContentsJson())

        val hasDarkVariant = darkIconFile.isPresent
        val isLightIconVector = lightIconFile.get().asFile.imageFormat() == ImageFormat.VECTOR
        val isDarkIconVector = if (hasDarkVariant) darkIconFile.get().asFile.imageFormat() == ImageFormat.VECTOR else false

        generateColorSet(assetsDirectory)

        generateImageSet(
            assetsDirectory = assetsDirectory,
            isLightIconVector = isLightIconVector,
            isDarkIconVector = isDarkIconVector,
            hasDarkVariant = hasDarkVariant
        )

        processLightIcon(
            targetDirectory = targetDirectory,
            imageProcessor = imageProcessor,
            isLightIconVector = isLightIconVector
        )

        if (hasDarkVariant) {
            processDarkIcon(
                targetDirectory = targetDirectory,
                imageProcessor = imageProcessor,
                isLightIconVector = isLightIconVector,
                isDarkIconVector = isDarkIconVector
            )
        }

        patchInfoPlist(
            infoPlist = targetDirectory.resolve("Info.plist"),
            isFullscreen = fullscreen.get()
        )

        logger.lifecycle("Generated iOS splash resources in: ${targetDirectory.absolutePath}")
    }

    private fun validateInputs() {
        val lightIcon = lightIconFile.get().asFile

        if (lightIcon.imageFormat() == ImageFormat.XML) {
            throw IllegalArgumentException(
                "iOS cannot process Android VectorDrawables (.xml). Please provide an " +
                        "iOS-compatible vector (.svg, .pdf) or raster (.png)."
            )
        }

        val darkIcon = darkIconFile.orNull?.asFile

        if (darkIcon?.imageFormat() == ImageFormat.XML) {
            throw IllegalArgumentException("iOS cannot process Android VectorDrawables (.xml) for dark mode.")
        }
    }

    private fun generateStoryboard(targetDirectory: File, imageProcessor: ImageProcessor) {
        val lightIcon = lightIconFile.get().asFile
        val isLightIconVector = lightIcon.imageFormat() == ImageFormat.VECTOR

        val lightImageInfo = if (isLightIconVector) {
            try {
                imageProcessor.read(lightIcon)
            } catch (exception: IOException) {
                logger.warn("Failed to read image dimensions for storyboard, falling back: ${exception.message}")

                ImageInfo(
                    size = ImageDefaults.IOS_BASE_SIZE_PX,
                    image = BufferedImage(1, 1, 1)
                )
            }
        } else {
            ImageInfo(
                size = ImageDefaults.IOS_BASE_SIZE_PX,
                image = BufferedImage(1, 1, 1)
            )
        }

        val storyboardFile = targetDirectory.resolve("LaunchScreen.storyboard")

        if (storyboardFile.exists()) {
            val backupFile = targetDirectory.resolve("LaunchScreen.storyboard.bak")
            storyboardFile.copyTo(target = backupFile, overwrite = true)
            logger.lifecycle("Backed up existing LaunchScreen.storyboard to .bak")
        }

        storyboardFile.writeText(
            IosTemplates.launchScreenStoryboard(
                imageWidth = lightImageInfo.width,
                imageHeight = lightImageInfo.height
            )
        )
    }

    private fun generateColorSet(assetsDirectory: File) {
        val colorSetDirectory = assetsDirectory.resolve("SplashBackground.colorset").apply { mkdirs() }
        val colorSetFile = colorSetDirectory.resolve("Contents.json")

        colorSetFile.writeText(
            IosTemplates.colorSetContentsJson(
                color = lightBackgroundColor.get(),
                darkColor = darkBackgroundColor.orNull
            )
        )
    }

    private fun generateImageSet(
        assetsDirectory: File,
        isLightIconVector: Boolean,
        isDarkIconVector: Boolean,
        hasDarkVariant: Boolean
    ) {
        val lightIcon = lightIconFile.get().asFile
        val darkIcon = darkIconFile.orNull?.asFile
        
        val isVectorMode = isLightIconVector && isDarkIconVector
        val darkIconExtension = darkIcon?.extension ?: "png"

        val imageSetDirectory = assetsDirectory.resolve("SplashIcon.imageset").apply { mkdirs() }
        val imageSetFile = imageSetDirectory.resolve("Contents.json")

        imageSetFile.writeText(
            IosTemplates.imageSetContentsJson(
                hasDarkVariant = hasDarkVariant,
                isVector = isVectorMode,
                lightExtension = lightIcon.extension,
                darkExtension = darkIconExtension
            )
        )
    }

    private fun processLightIcon(
        targetDirectory: File,
        imageProcessor: ImageProcessor,
        isLightIconVector: Boolean
    ) {
        val lightIcon = lightIconFile.get().asFile
        val assetsDirectory = targetDirectory.resolve("Assets.xcassets")
        val imageSetDirectory = assetsDirectory.resolve("SplashIcon.imageset")

        if (isLightIconVector) {
            val imageFileName = "${ImageDefaults.SPLASH_ICON_FILE_PREFIX}.${lightIcon.extension}"
            val imageFile = imageSetDirectory.resolve(imageFileName)
            lightIcon.copyTo(target = imageFile, overwrite = true)
        } else {
            val imageInfo = imageProcessor.read(lightIcon)

            IosScale.toVariants(isDark = false).forEach { variant ->
                val scaledImage = imageProcessor.scale(image = imageInfo.image, size = variant.sizePx)

                imageProcessor.write(
                    image = scaledImage,
                    outputDirectory = targetDirectory,
                    directoryName = variant.directoryName,
                    fileName = variant.fileName
                )
            }
        }
    }

    private fun processDarkIcon(
        targetDirectory: File,
        imageProcessor: ImageProcessor,
        isLightIconVector: Boolean,
        isDarkIconVector: Boolean
    ) {
        val darkIcon = darkIconFile.get().asFile
        val darkIconExtension = darkIcon.extension
        val isVectorMode = isLightIconVector && isDarkIconVector
        
        val assetsDirectory = targetDirectory.resolve("Assets.xcassets")
        val imageSetDirectory = assetsDirectory.resolve("SplashIcon.imageset")

        if (isDarkIconVector) {
            val darkImageFileName = if (isVectorMode) {
                "${ImageDefaults.SPLASH_ICON_FILE_PREFIX}.$darkIconExtension"
            } else {
                "${ImageDefaults.SPLASH_ICON_FILE_PREFIX}_dark.$darkIconExtension"
            }

            val darkImageFile = imageSetDirectory.resolve(darkImageFileName)

            darkIcon.copyTo(target = darkImageFile, overwrite = true)
        } else {
            val darkImageInfo = imageProcessor.read(darkIcon)

            IosScale.toVariants(isDark = true).forEach { variant ->
                val scaledImage = imageProcessor.scale(image = darkImageInfo.image, size = variant.sizePx)

                imageProcessor.write(
                    image = scaledImage,
                    outputDirectory = targetDirectory,
                    directoryName = variant.directoryName,
                    fileName = variant.fileName
                )
            }
        }
    }

    private fun patchInfoPlist(infoPlist: File, isFullscreen: Boolean) {
        if (!infoPlist.exists()) {
            return
        }

        val content = infoPlist.readText()
        val isStoryboardMissing = !content.contains("UILaunchStoryboardName")
        val isStatusBarMissing = isFullscreen && !content.contains("UIStatusBarHidden")

        if (!isStoryboardMissing && !isStatusBarMissing) {
            return
        }

        val storyboardTag =
            if (isStoryboardMissing) "\n\t<key>UILaunchStoryboardName</key>\n\t<string>LaunchScreen</string>" else ""
        val statusTag = if (isStatusBarMissing) "\n\t<key>UIStatusBarHidden</key>\n\t<true/>" else ""
        val insertion = "$storyboardTag$statusTag\n"

        val lastIndex = content.lastIndexOf("</dict>")

        if (lastIndex == -1) {
            return
        }

        val patched = buildString {
            append(content.substring(0, lastIndex))
            append(insertion)
            append(content.substring(lastIndex))
        }

        infoPlist.writeText(patched)

        logger.lifecycle("Patched Info.plist successfully")
    }
}