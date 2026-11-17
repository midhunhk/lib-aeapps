plugins {
    /**
     * Use `apply false` in the top-level build.gradle file to add a Gradle
     * plugin as a build dependency but not apply it to the current (root)
     * project. Don't use `apply false` in sub-projects. For more information,
     * see Applying external plugins with same version to subprojects.
     */

    // https://developer.android.com/build/releases/gradle-plugin
    id("com.android.application") version "9.4.1" apply false
    id("com.android.library") version "9.4.1" apply false
    id("org.jetbrains.kotlin.android") version "2.2.10" apply false
    // API documentation, see "API documentation" in DEVELOPMENT.md
    id("org.jetbrains.dokka") version "2.2.0"
    id("maven-publish")
}

// Combine the API docs of every published library module into one site.
// The sample app and the aeapps umbrella (no sources) are left out on purpose.
dependencies {
    dokka(project(":core"))
    dokka(project(":utilities"))
    dokka(project(":contacts-api"))
    dokka(project(":sms-api"))
    dokka(project(":runtime-permissions"))
    dokka(project(":multi-contact"))
    dokka(project(":database-helpers"))
    dokka(project(":billing-client"))
    dokka(project(":mocks"))
}

dokka {
    moduleName.set("lib-aeapps")
    moduleVersion.set(com.ae.apps.lib.Publish.VERSION_STRING)
    dokkaPublications.html {
        outputDirectory.set(layout.buildDirectory.dir("dokka/html"))
    }
}

// Dokka needs help with this project's mix of Java-only and Kotlin Android modules:
//  - Kotlin modules get the same sources registered twice (Kotlin's "main" and Android's
//    "release"), which Dokka rejects, so the "release" copy is suppressed.
//  - Java-only modules get no source set at all, so one is registered from src/main/java,
//    with the Android and AndroidX classes on its classpath so links and supertypes resolve.
subprojects {
    pluginManager.withPlugin("org.jetbrains.dokka") {
        afterEvaluate {
            val dokkaExtension = extensions.getByType<org.jetbrains.dokka.gradle.DokkaExtension>()
            // Without this, every page lists hundreds of members inherited from Activity and View
            dokkaExtension.suppressInheritedMembers.set(true)
            // Protected members are part of the API: subclasses of the base Activities implement them
            dokkaExtension.dokkaSourceSets.configureEach {
                documentedVisibilities(
                    org.jetbrains.dokka.gradle.engine.parameters.VisibilityModifier.Public,
                    org.jetbrains.dokka.gradle.engine.parameters.VisibilityModifier.Protected,
                )
            }
            if (plugins.hasPlugin("org.jetbrains.kotlin.android")) {
                dokkaExtension.dokkaSourceSets.named("release") { suppress.set(true) }
            } else {
                val android = extensions.getByType<com.android.build.gradle.LibraryExtension>()
                val androidClasses = configurations.named("releaseCompileClasspath").map { config ->
                    config.incoming.artifactView {
                        attributes {
                            attribute(Attribute.of("artifactType", String::class.java), "android-classes-jar")
                        }
                    }.files
                }
                dokkaExtension.dokkaSourceSets.register("main") {
                    sourceRoots.from(file("src/main/java"))
                    classpath.from(android.bootClasspath)
                    classpath.from(androidClasses)
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------------------------
// Documentation site (docs/, served by GitHub Pages). Run before each release:
//
//     ./gradlew updateSite
//
// See "Documentation site" in DEVELOPMENT.md.
// ---------------------------------------------------------------------------------------------

val siteDir = layout.projectDirectory.dir("docs")

// Copies the generated API reference into docs/api, removing pages for classes that no longer exist
val updateApiDocs by tasks.registering(Sync::class) {
    group = "documentation"
    description = "Regenerates the API reference with Dokka and copies it into docs/api."
    dependsOn("dokkaGenerate")
    from(layout.buildDirectory.dir("dokka/html"))
    into(siteDir.dir("api"))
    // Gradle ignores files it did not write when deciding whether this is up to date, so pages
    // left behind by an older version would survive. The copy is cheap; always run it.
    outputs.upToDateWhen { false }
}

// Rebuilds docs/release_notes.html from VersionHistory.md
val updateReleaseNotes by tasks.registering(Exec::class) {
    group = "documentation"
    description = "Regenerates docs/release_notes.html from VersionHistory.md."
    val python = providers.gradleProperty("pythonExecutable").orElse(
        if (System.getProperty("os.name").startsWith("Windows")) "python" else "python3"
    )
    inputs.file("VersionHistory.md")
    inputs.file("scripts/release_notes.py")
    outputs.file(siteDir.file("release_notes.html"))
    workingDir = layout.projectDirectory.asFile
    executable = python.get()
    args("scripts/release_notes.py")
}

// Keeps the version in the landing page's dependency snippet in step with Publish.kt
val updateSiteVersion by tasks.registering {
    group = "documentation"
    description = "Sets the version in the docs/index.html dependency snippet from Publish.VERSION_STRING."
    val indexFile = siteDir.file("index.html").asFile
    val tag = "v" + com.ae.apps.lib.Publish.VERSION_STRING
    inputs.property("tag", tag)
    doLast {
        val pattern = Regex("""val libAeAppsVersion = "[^"]*"""")
        val original = indexFile.readText()
        check(pattern.containsMatchIn(original)) {
            "Could not find the libAeAppsVersion line in ${indexFile.path}"
        }
        val updated = original.replace(pattern, "val libAeAppsVersion = \"$tag\"")
        if (updated != original) {
            indexFile.writeText(updated)
            println("docs/index.html now uses $tag")
        } else {
            println("docs/index.html already uses $tag")
        }
    }
}

tasks.register("updateSite") {
    group = "documentation"
    description = "Updates the docs/ site for a release: API reference, release notes and version."
    dependsOn(updateApiDocs, updateReleaseNotes, updateSiteVersion)
}
