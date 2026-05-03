package com.buildoptimus.kmp_native_splash

import com.buildoptimus.kmp_native_splash.color.PlatformColor
import com.buildoptimus.kmp_native_splash.extension.Extension
import com.buildoptimus.kmp_native_splash.task.AndroidTask
import com.buildoptimus.kmp_native_splash.task.IosTask
import com.buildoptimus.kmp_native_splash.task.TaskDefaults
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.register
import java.io.File

class KmpNativeSplashPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val extension = project.extensions.create<Extension>("kmpNativeSplash")
        registerAndroidTask(project = project, extension = extension)
        registerIosTask(project = project, extension = extension)
        registerLifecycleTask(project)
    }

    private fun registerAndroidTask(project: Project, extension: Extension) {
        listOf(ANDROID_LIBRARY_PLUGIN_NAME, ANDROID_APPLICATION_PLUGIN_NAME).forEach { pluginId ->
            project.plugins.withId(pluginId) {
                val task = project.tasks.register<AndroidTask>(ANDROID_TASK_NAME) {
                    val defaultConfig = extension.defaultConfig
                    val androidConfig = extension.androidConfig

                    val lightIconFileProvider = getExtensionIcon(
                        project = project,
                        composeResourceDirectory = defaultConfig.composeResourceDirectory.get(),
                        defaultComposeResource = defaultConfig.lightConfig.composeResource,
                        defaultIcon = defaultConfig.lightConfig.icon,
                        platformComposeResource = androidConfig.lightConfig.composeResource,
                        platformIcon = androidConfig.lightConfig.icon
                    ) ?: throw IllegalArgumentException(
                        "Icon was not provided, kindly specify an icon or composeResource in " +
                                "the default or android light configuration block"
                    )

                    lightIconFile.set(lightIconFileProvider)

                    val lightBackgroundColorValue = androidConfig.lightConfig.background.orNull
                        ?: defaultConfig.lightConfig.background.orNull
                        ?: throw IllegalArgumentException(
                            "Background color was not provided, kindly specify a background color in the default or " +
                                    "android light configuration block"
                        )

                    lightBackgroundColor.set(PlatformColor.parseHex(lightBackgroundColorValue))

                    val darkIconFileProvider = getExtensionIcon(
                        project = project,
                        composeResourceDirectory = defaultConfig.composeResourceDirectory.get(),
                        defaultComposeResource = defaultConfig.darkConfig.composeResource,
                        defaultIcon = defaultConfig.darkConfig.icon,
                        platformComposeResource = androidConfig.darkConfig.composeResource,
                        platformIcon = androidConfig.darkConfig.icon
                    )

                    if (darkIconFileProvider != null) {
                        darkIconFile.set(darkIconFileProvider)
                    }

                    val darkBackgroundColorValue = androidConfig.darkConfig.background.orNull
                        ?: defaultConfig.darkConfig.background.orNull

                    if (darkBackgroundColorValue != null) {
                        darkBackgroundColor.set(PlatformColor.parseHex(darkBackgroundColorValue))
                    }

                    val fullscreenValue = androidConfig.fullscreen.orNull
                        ?: defaultConfig.fullscreen.orNull
                        ?: false

                    fullscreen.set(fullscreenValue)

                    outputDirectory.set(
                        androidConfig.resourceDirectory.map {
                            project.layout.projectDirectory.dir(it)
                        }
                    )
                }

                project.tasks.matching {
                    it.name == "preBuild"
                }.configureEach {
                    dependsOn(task)
                }
            }
        }
    }

    private fun registerIosTask(project: Project, extension: Extension) {
        val task = project.tasks.register<IosTask>(IOS_TASK_NAME) {
            val defaultConfig = extension.defaultConfig
            val iosConfig = extension.iosConfig

            val lightIconFileProvider = getExtensionIcon(
                project = project,
                composeResourceDirectory = defaultConfig.composeResourceDirectory.get(),
                defaultComposeResource = defaultConfig.lightConfig.composeResource,
                defaultIcon = defaultConfig.lightConfig.icon,
                platformComposeResource = iosConfig.lightConfig.composeResource,
                platformIcon = iosConfig.lightConfig.icon
            ) ?: throw IllegalArgumentException(
                "Icon was not provided, kindly specify an icon or composeResource in " +
                        "the default or ios light configuration block"
            )

            lightIconFile.set(lightIconFileProvider)

            val lightBackgroundColorValue = iosConfig.lightConfig.background.orNull
                ?: defaultConfig.lightConfig.background.orNull
                ?: throw IllegalArgumentException(
                    "Background color was not provided, kindly specify a background color in the default or ios " +
                            "configuration block"
                )

            lightBackgroundColor.set(PlatformColor.parseHex(lightBackgroundColorValue))

            val darkIconFileProvider = getExtensionIcon(
                project = project,
                composeResourceDirectory = defaultConfig.composeResourceDirectory.get(),
                defaultComposeResource = defaultConfig.darkConfig.composeResource,
                defaultIcon = defaultConfig.darkConfig.icon,
                platformComposeResource = iosConfig.darkConfig.composeResource,
                platformIcon = iosConfig.darkConfig.icon
            )

            if (darkIconFileProvider != null) {
                darkIconFile.set(darkIconFileProvider)
            }

            val darkBackgroundColorValue = iosConfig.darkConfig.background.orNull
                ?: defaultConfig.darkConfig.background.orNull

            if (darkBackgroundColorValue != null) {
                darkBackgroundColor.set(PlatformColor.parseHex(darkBackgroundColorValue))
            }

            val fullscreenValue = iosConfig.fullscreen.orNull
                ?: defaultConfig.fullscreen.orNull
                ?: true

            fullscreen.set(fullscreenValue)

            outputDirectory.set(
                iosConfig.projectDirectory.flatMap { directory ->
                    iosConfig.targetName.map { target ->
                        project.layout.projectDirectory.dir(directory).dir(target)
                    }
                }
            )
        }

        project.tasks.matching {
            it.name.startsWith("compileKotlinIos") ||
            (it.name.startsWith("link") && it.name.contains("Ios")) ||
            it.name.contains("embedAndSignAppleFrameworkForXcode")
        }.configureEach {
            dependsOn(task)
        }
    }

    private fun registerLifecycleTask(project: Project) {
        project.tasks.register(LIFECYCLE_TASK_NAME) {
            group = TaskDefaults.GROUP
            description = TaskDefaults.LIFECYCLE_TASK_DESCRIPTION

            dependsOn(ANDROID_TASK_NAME, IOS_TASK_NAME)
        }
    }

    private fun getExtensionIcon(
        project: Project,
        composeResourceDirectory: String,
        defaultComposeResource: Property<String>,
        defaultIcon: Property<String>,
        platformComposeResource: Property<String>,
        platformIcon: Property<String>
    ): Provider<RegularFile>? {
        return when {
            platformComposeResource.isPresent -> {
                project.layoutFile(path = platformComposeResource.get(), parent = composeResourceDirectory)
            }

            platformIcon.isPresent -> {
                project.layoutFile(path = platformIcon.get())
            }

            defaultComposeResource.isPresent -> {
                project.layoutFile(path = defaultComposeResource.get(), parent = composeResourceDirectory)
            }

            defaultIcon.isPresent -> {
                project.layoutFile(path = defaultIcon.get())
            }

            else -> null
        }
    }

    private fun Project.layoutFile(path: String, parent: String? = null): Provider<RegularFile> {
        return layout.file(project.provider { File(parent, path) })
    }

    companion object {
        private const val LIFECYCLE_TASK_NAME = "generateSplash"
        private const val ANDROID_TASK_NAME = "generateAndroidSplash"
        private const val IOS_TASK_NAME = "generateIosSplash"

        private const val ANDROID_LIBRARY_PLUGIN_NAME = "com.android.library"
        private const val ANDROID_APPLICATION_PLUGIN_NAME = "com.android.application"
    }
}
