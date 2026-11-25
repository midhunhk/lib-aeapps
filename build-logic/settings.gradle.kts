// Convention plugins for this project's modules. Included from the root settings.gradle.kts
// (pluginManagement { includeBuild("build-logic") }). See "Build configuration" in DEVELOPMENT.md.

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    versionCatalogs {
        // Share the main build's catalog so plugin versions are declared once
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}

rootProject.name = "build-logic"
include(":convention")
