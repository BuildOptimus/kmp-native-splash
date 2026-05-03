package com.buildoptimus.kmp_native_splash.extension

import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input

@ExtensionConfigDslMarker
interface ExtensionConfig {
    @get:Input
    val icon: Property<String>

    @get:Input
    val background: Property<String>

    @get:Input
    val composeResource: Property<String>
}