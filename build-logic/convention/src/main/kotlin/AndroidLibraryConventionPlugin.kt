import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType
import org.jetbrains.dokka.gradle.DokkaExtension
import org.jetbrains.dokka.gradle.engine.parameters.VisibilityModifier
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

/**
 * Configuration shared by every published library module (`id("aeapps.android.library")`).
 *
 * A module's own build script only declares its `namespace` and dependencies. Everything else is
 * set here:
 * - Android: SDK levels and Java level from the version catalog, consumer ProGuard rules, a
 *   release build type without minification, and a release variant published with sources.
 * - Kotlin: the JVM toolchain and explicit API mode. Every library module applies Kotlin,
 *   including those still written in Java, so coverage and the API reference treat them all
 *   the same.
 * - Publishing: a `release` Maven publication. The artifact ID is the Gradle project name; the
 *   group and version come from `aeapps.group` and `aeapps.version` in gradle.properties.
 * - Tests: JUnit 6 (Jupiter) and Mockito on the JUnit Platform.
 * - Coverage: Kover.
 * - Public API: the binary-compatibility validator. `apiDump` records the public API in
 *   `api/<module>.api`; `apiCheck` (part of `check`) fails if it changes without a new dump.
 * - API reference: Dokka, documenting public and protected members only, without members
 *   inherited from Android framework classes.
 */
class AndroidLibraryConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) = with(target) {
        val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
        val minSdkLevel = libs.version("minSdk").toInt()
        val javaLevel = libs.version("jvmTarget").toInt()

        with(pluginManager) {
            apply("com.android.library")
            apply("org.jetbrains.kotlin.android")
            apply("maven-publish")
            apply("org.jetbrains.dokka")
            apply("org.jetbrains.kotlinx.kover")
            apply("org.jetbrains.kotlinx.binary-compatibility-validator")
        }

        extensions.configure<LibraryExtension> {
            compileSdk = libs.version("compileSdk").toInt()

            defaultConfig {
                minSdk = minSdkLevel
                aarMetadata {
                    minCompileSdk = minSdkLevel
                }
                consumerProguardFiles("consumer-rules.pro")
            }

            publishing {
                singleVariant("release") {
                    withSourcesJar()
                }
            }

            buildTypes {
                getByName("release") {
                    isMinifyEnabled = false
                    proguardFiles(
                        getDefaultProguardFile("proguard-android-optimize.txt"),
                        "proguard-rules.pro"
                    )
                }
            }

            compileOptions {
                sourceCompatibility = JavaVersion.toVersion(javaLevel)
                targetCompatibility = JavaVersion.toVersion(javaLevel)
            }
        }

        extensions.configure<KotlinAndroidProjectExtension> {
            jvmToolchain(javaLevel)
            // Public Kotlin declarations must state their visibility and return type, so nothing
            // becomes public API by accident
            explicitApi()
        }

        configurePublishing()
        configureTests(libs)
        configureDokka()
    }

    private fun Project.configurePublishing() {
        val publishGroup = providers.gradleProperty("aeapps.group").get()
        val publishVersion = providers.gradleProperty("aeapps.version").get()

        // The Android "release" software component only exists once AGP has finished configuring
        afterEvaluate {
            extensions.configure<PublishingExtension> {
                publications {
                    register<MavenPublication>("release") {
                        from(components.getByName("release"))
                        groupId = publishGroup
                        artifactId = project.name
                        version = publishVersion
                    }
                }
            }
        }
    }

    private fun Project.configureTests(libs: VersionCatalog) {
        dependencies {
            add("testImplementation", platform(libs.library("junit-bom")))
            add("testImplementation", libs.library("junit-jupiter"))
            add("testRuntimeOnly", libs.library("junit-platform-launcher"))
            add("testImplementation", libs.library("mockito-core"))
            add("testImplementation", libs.library("mockito-junit-jupiter"))
        }
        tasks.withType<Test>().configureEach {
            useJUnitPlatform()
        }
    }

    private fun Project.configureDokka() {
        extensions.configure<DokkaExtension> {
            // Without this, every page lists hundreds of members inherited from Activity and View
            dokkaPublications.configureEach {
                suppressInheritedMembers.set(true)
            }
            dokkaSourceSets.configureEach {
                // Protected members are part of the API: subclasses of the base Activities use them
                documentedVisibilities(VisibilityModifier.Public, VisibilityModifier.Protected)
                // Kotlin and Android both register the main sources ("main" and "release");
                // Dokka rejects the duplicate, so the Android copy is skipped
                if (name == "release") {
                    suppress.set(true)
                }
            }
        }
    }

    private fun VersionCatalog.version(alias: String): String =
        findVersion(alias).orElseThrow { IllegalStateException("No version '$alias' in libs.versions.toml") }
            .requiredVersion

    private fun VersionCatalog.library(alias: String) =
        findLibrary(alias).orElseThrow { IllegalStateException("No library '$alias' in libs.versions.toml") }
}
