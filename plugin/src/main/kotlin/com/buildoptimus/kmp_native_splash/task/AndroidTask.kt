package com.buildoptimus.kmp_native_splash.task

import com.buildoptimus.kmp_native_splash.image.*
import com.buildoptimus.kmp_native_splash.template.AndroidTemplates
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File

internal abstract class AndroidTask : DefaultTask(), SplashTask {
    init {
        group = TaskDefaults.GROUP
        description = "Generates Android splash screen resources."
    }

    @TaskAction
    fun generate() {
        val targetDirectory = outputDirectory.get().asFile
        val imageProcessor = ImageProcessor()

        processLightIcon(targetDirectory = targetDirectory, imageProcessor = imageProcessor)

        val lightBackgroundColorHex = lightBackgroundColor.get().toAndroidHex()
        generateLightResources(targetDirectory = targetDirectory, lightBackgroundColorHex = lightBackgroundColorHex)

        if (darkIconFile.isPresent) {
            processDarkIcon(targetDirectory = targetDirectory, imageProcessor = imageProcessor)
        }

        if (darkBackgroundColor.isPresent) {
            val darkBackgroundColorHex = darkBackgroundColor.get().toAndroidHex()
            generateDarkResources(targetDirectory = targetDirectory, darkBackgroundColorHex = darkBackgroundColorHex)
        }

        logger.lifecycle("Generated Android splash resources in: ${targetDirectory.absolutePath}")
    }

    private fun processLightIcon(targetDirectory: File, imageProcessor: ImageProcessor) {
        val lightIcon = lightIconFile.get().asFile

        if (lightIcon.imageFormat() == ImageFormat.XML) {
            val drawableDirectory = targetDirectory
                .resolve("drawable")
                .apply { mkdirs() }

            val file = drawableDirectory.resolve("${ImageDefaults.SPLASH_ICON_FILE_PREFIX}.xml")

            lightIcon.copyTo(target = file, overwrite = true)
        } else {
            val imageInfo = imageProcessor.read(lightIcon)

            AndroidDensity.toVariants(isDark = false).forEach { variant ->
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

    private fun processDarkIcon(targetDirectory: File, imageProcessor: ImageProcessor) {
        val darkIcon = darkIconFile.get().asFile

        if (darkIcon.imageFormat() == ImageFormat.XML) {
            val nightDrawableDirectory = targetDirectory
                .resolve("drawable-night")
                .apply { mkdirs() }

            val file = nightDrawableDirectory.resolve("${ImageDefaults.SPLASH_ICON_FILE_PREFIX}.xml")

            darkIcon.copyTo(target = file, overwrite = true)
        } else {
            val imageInfo = imageProcessor.read(darkIcon)

            AndroidDensity.toVariants(isDark = true).forEach { variant ->
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

    private fun generateLightResources(targetDirectory: File, lightBackgroundColorHex: String) {
        val valuesDirectory = targetDirectory.resolve("values").apply { mkdirs() }

        val colorsFile = valuesDirectory.resolve("splash_colors.xml")
        colorsFile.writeText(AndroidTemplates.colorsXml(lightBackgroundColorHex))

        val drawableDirectory = targetDirectory.resolve("drawable").apply { mkdirs() }

        val drawableFile = drawableDirectory.resolve("launch_background.xml")
        drawableFile.writeText(AndroidTemplates.launchBackgroundXml())

        val styleFile = valuesDirectory.resolve("splash_styles.xml")
        styleFile.writeText(AndroidTemplates.stylesXml(fullscreen = fullscreen.get(), isV31 = false))

        val v31ValuesDirectory = targetDirectory.resolve("values-v31").apply { mkdirs() }

        val v31StyleFile = v31ValuesDirectory.resolve("splash_styles.xml")
        v31StyleFile.writeText(AndroidTemplates.stylesXml(fullscreen = fullscreen.get(), isV31 = true))
    }

    private fun generateDarkResources(targetDirectory: File, darkBackgroundColorHex: String) {
        val nightValuesDirectory = targetDirectory.resolve("values-night").apply { mkdirs() }

        val nightColorsFile = nightValuesDirectory.resolve("splash_colors.xml")
        nightColorsFile.writeText(AndroidTemplates.colorsXml(darkBackgroundColorHex))

        val nightDrawableDirectory = targetDirectory.resolve("drawable-night").apply { mkdirs() }

        val nightDrawableFile = nightDrawableDirectory.resolve("launch_background.xml")
        nightDrawableFile.writeText(AndroidTemplates.launchBackgroundXml())
    }
}