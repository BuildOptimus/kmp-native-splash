package com.buildoptimus.kmp_native_splash.extension

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.property
import javax.inject.Inject

abstract class DefaultExtensionConfig @Inject constructor(objectFactory: ObjectFactory) :
    PlatformExtensionConfig(objectFactory) {
    val composeResourceDirectory: Property<String> =
        objectFactory.property<String>().convention("src/commonMain/composeResources/drawable")
}