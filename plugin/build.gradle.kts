plugins {
    `kotlin-dsl`
    `maven-publish`
}

group = "com.buildoptimus"
version = "0.0.1"

repositories {
    mavenCentral()
}

gradlePlugin {
    plugins {
        register("kmpNativeSplash") {
            id = "com.buildoptimus.kmp.native.splash"
            implementationClass = "com.buildoptimus.kmp_native_splash.KmpNativeSplashPlugin"
            displayName = "KMP Native Splash"
            description = "Generates native splash screen resources from a unified Kotlin DSL."
            tags.set(listOf("kotlin", "multiplatform", "splash", "compose"))
        }
    }
}
