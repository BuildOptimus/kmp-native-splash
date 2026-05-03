package com.buildoptimus.kmp_native_splash.extension

import org.gradle.api.model.ObjectFactory
import org.gradle.kotlin.dsl.newInstance
import javax.inject.Inject

@ExtensionConfigDslMarker
abstract class Extension @Inject constructor(objectFactory: ObjectFactory) {
    internal val defaultConfig: DefaultExtensionConfig = objectFactory.newInstance()
    internal val androidConfig: AndroidExtensionConfig = objectFactory.newInstance()
    internal val iosConfig: IosExtensionConfig = objectFactory.newInstance()

    fun default(action: DefaultExtensionConfig.() -> Unit) {
        action(defaultConfig)
    }

    fun android(action: AndroidExtensionConfig.() -> Unit) {
        action(androidConfig)
    }

    fun ios(action: IosExtensionConfig.() -> Unit) {
        action(iosConfig)
    }
}