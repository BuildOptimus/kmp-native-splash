package com.buildoptimus.kmp_native_splash.extension

import org.gradle.api.Action
import org.gradle.api.model.ObjectFactory
import org.gradle.kotlin.dsl.newInstance
import javax.inject.Inject

abstract class Extension @Inject constructor(objectFactory: ObjectFactory) {
    internal val defaultConfig: DefaultExtensionConfig = objectFactory.newInstance()
    internal val androidConfig: AndroidExtensionConfig = objectFactory.newInstance()
    internal val iosConfig: IosExtensionConfig = objectFactory.newInstance()

    fun default(action: Action<DefaultExtensionConfig>) {
        action.execute(defaultConfig)
    }

    fun android(action: Action<AndroidExtensionConfig>) {
        action.execute(androidConfig)
    }

    fun ios(action: Action<IosExtensionConfig>) {
        action.execute(iosConfig)
    }
}