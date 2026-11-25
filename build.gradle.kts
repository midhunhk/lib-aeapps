// Root build. Shared module configuration is in the aeapps.android.library convention plugin
// (build-logic/), and every version is in gradle/libs.versions.toml. See "Build configuration"
// in DEVELOPMENT.md.

plugins {
    // Put the plugins on the build classpath at the catalog's versions; modules apply them
    // through the convention plugin
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.binary.compatibility.validator) apply false
    alias(libs.plugins.ktlint) apply false

    // Applied here to combine every library module into one API reference and one coverage report
    alias(libs.plugins.dokka)
    alias(libs.plugins.kover)
}

val libraryVersion = providers.gradleProperty("aeapps.version")

// Combine the API docs and the test coverage of every published library module.
// The sample app and the aeapps umbrella (no sources) are left out on purpose.
dependencies {
    listOf(
        ":core",
        ":utilities",
        ":contacts-api",
        ":sms-api",
        ":runtime-permissions",
        ":multi-contact",
        ":billing-client",
        ":mocks",
    ).forEach { module ->
        dokka(project(module))
        kover(project(module))
    }
}

dokka {
    moduleName.set("lib-aeapps")
    moduleVersion.set(libraryVersion)
    dokkaPublications.html {
        outputDirectory.set(layout.buildDirectory.dir("dokka/html"))
    }
}

kover {
    reports {
        filters {
            excludes {
                // Generated classes
                classes("*.R", "*.R$*", "*.BuildConfig", "*.databinding.*")
            }
        }
    }
}

// ---------------------------------------------------------------------------------------------
// Documentation site (docs/, served by GitHub Pages). Built and deployed by
// .github/workflows/pages.yml; run locally to preview:
//
//     ./gradlew updateSite
//
// See "Documentation site" in DEVELOPMENT.md.
// ---------------------------------------------------------------------------------------------

val siteDir = layout.projectDirectory.dir("docs")

// Copies the generated API reference into docs/api, removing pages for classes that no longer exist
val updateApiDocs = tasks.register<Sync>("updateApiDocs") {
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
val updateReleaseNotes = tasks.register<Exec>("updateReleaseNotes") {
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

// Keeps the version in the landing page's dependency snippet in step with aeapps.version
val updateSiteVersion = tasks.register("updateSiteVersion") {
    group = "documentation"
    description = "Sets the version in the docs/index.html dependency snippet from aeapps.version."
    val indexFile = siteDir.file("index.html").asFile
    val tag = libraryVersion.map { "v$it" }
    inputs.property("tag", tag)
    doLast {
        val pattern = Regex("""val libAeAppsVersion = "[^"]*"""")
        val original = indexFile.readText()
        check(pattern.containsMatchIn(original)) {
            "Could not find the libAeAppsVersion line in ${indexFile.path}"
        }
        val updated = original.replace(pattern, "val libAeAppsVersion = \"${tag.get()}\"")
        if (updated != original) {
            indexFile.writeText(updated)
            println("docs/index.html now uses ${tag.get()}")
        } else {
            println("docs/index.html already uses ${tag.get()}")
        }
    }
}

tasks.register("updateSite") {
    group = "documentation"
    description = "Updates the docs/ site for a release: API reference, release notes and version."
    dependsOn(updateApiDocs, updateReleaseNotes, updateSiteVersion)
}
