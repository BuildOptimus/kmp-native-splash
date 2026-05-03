package com.buildoptimus.kmp_native_splash.extension

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.kotlin.dsl.newInstance
import javax.inject.Inject

@ExtensionConfigDslMarker
abstract class PlatformExtensionConfig @Inject constructor(objectFactory: ObjectFactory) {
    @get:Input
    val lightConfig: ExtensionConfig = objectFactory.newInstance()

    @get:Input
    val darkConfig: ExtensionConfig = objectFactory.newInstance()

    @get:Input
    abstract val fullscreen: Property<Boolean>

    fun light(action: ExtensionConfig.() -> Unit) {
        action(lightConfig)
    }

    fun dark(action: ExtensionConfig.() -> Unit) {
        action(darkConfig)
    }
}