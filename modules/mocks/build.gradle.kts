import com.ae.apps.lib.ArtifactId
import com.ae.apps.lib.ConfigurationData
import com.ae.apps.lib.Libs
import com.ae.apps.lib.Publish

plugins {
    id ("com.android.library")
    id ("maven-publish")
    id ("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.ae.apps.lib.mocks"
    compileSdk = ConfigurationData.compileSdk

    defaultConfig {
        minSdk = ConfigurationData.minSdk
        aarMetadata {
            minCompileSdk = ConfigurationData.minSdk
        }
        consumerProguardFiles ("consumer-rules.pro")
        // testInstrumentationRunner ("androidx.test.runner.AndroidJUnitRunner")
    }

    publishing {
        singleVariant("release") {
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles (
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
            )
        }
    }
    testOptions {
        unitTests.all {

        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

afterEvaluate {
    publishing {
        publications {
            // creates a maven publication called release
            register<MavenPublication>("release") {
                from (components["release"])

                groupId = Publish.GROUP_ID
                artifactId = ArtifactId.MOCKS
                version = Publish.VERSION_STRING
            }
        }
    }
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(ConfigurationData.javaLangVersion))
    }

    jvmToolchain(ConfigurationData.javaLangVersion)
}

dependencies {
    api (project(":core"))

    implementation(Libs.AndroidX.APPCOMPAT)
    implementation(Libs.AndroidX.CORE_KTX)

    testImplementation(Libs.Test.MOCKITO_CORE)
    testImplementation(Libs.Test.JUNITJUPITER.JUNIT5)
    testImplementation(Libs.Test.JUNITJUPITER.MOCKITO_EXT)

    androidTestImplementation (Libs.Test.TEST_RUNNER)
    androidTestImplementation (Libs.Test.MOCKITO_CORE)
}
