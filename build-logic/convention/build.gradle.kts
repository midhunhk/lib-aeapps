plugins {
    `kotlin-dsl`
}

kotlin {
    jvmToolchain(libs.versions.jvmTarget.get().toInt())
}

dependencies {
    // compileOnly: the main build puts these plugins on the classpath at the versions in the
    // catalog; the convention plugin only needs their APIs to compile against.
    compileOnly(libs.android.gradle.plugin)
    compileOnly(libs.kotlin.gradle.plugin)
    compileOnly(libs.dokka.gradle.plugin)
}

gradlePlugin {
    plugins {
        register("androidLibrary") {
            id = libs.plugins.aeapps.android.library.get().pluginId
            implementationClass = "AndroidLibraryConventionPlugin"
        }
    }
}
