plugins {
    `kotlin-dsl`
}

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
