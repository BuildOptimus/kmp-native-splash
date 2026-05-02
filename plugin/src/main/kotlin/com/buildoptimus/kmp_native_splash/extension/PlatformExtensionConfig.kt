package com.buildoptimus.kmp_native_splash.extension

import org.gradle.api.Action
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.kotlin.dsl.newInstance
import javax.inject.Inject

abstract class PlatformExtensionConfig @Inject constructor(objectFactory: ObjectFactory) {
    @get:Input
    val lightConfig: ExtensionConfig = objectFactory.newInstance()

    @get:Input
    val darkConfig: ExtensionConfig = objectFactory.newInstance()

    @get:Input
    abstract val fullscreen: Property<Boolean>

    fun light(action: Action<ExtensionConfig>) {
        action.execute(lightConfig)
    }

    fun dark(action: Action<ExtensionConfig>) {
        action.execute(darkConfig)
    }
}