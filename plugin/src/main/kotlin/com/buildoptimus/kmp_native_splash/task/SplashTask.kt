package com.buildoptimus.kmp_native_splash.task

import com.buildoptimus.kmp_native_splash.color.PlatformColor
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*

internal interface SplashTask {
    @get:InputFile
    @get:PathSensitive(PathSensitivity.RELATIVE)
    val lightIconFile: RegularFileProperty

    @get:Input
    val lightBackgroundColor: Property<PlatformColor>

    @get:InputFile
    @get:PathSensitive(PathSensitivity.RELATIVE)
    @get:Optional
    val darkIconFile: RegularFileProperty

    @get:Input
    @get:Optional
    val darkBackgroundColor: Property<PlatformColor>

    @get:Input
    @get:Optional
    val fullscreen: Property<Boolean>

    @get:OutputDirectory
    val outputDirectory: DirectoryProperty
}
