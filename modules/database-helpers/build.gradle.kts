import com.ae.apps.lib.ArtifactId
import com.ae.apps.lib.ConfigurationData
import com.ae.apps.lib.Publish

plugins {
    id ("com.android.library")
    id ("maven-publish")
    id ("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.ae.apps.lib.database_helpers"
    compileSdk = ConfigurationData.SDK_COMPILE_VERSION

    defaultConfig {
        minSdk = ConfigurationData.MIN_SDK_VERSION
        aarMetadata {
            minCompileSdk = ConfigurationData.MIN_SDK_VERSION
        }
        consumerProguardFiles ("consumer-rules.pro")
        // testInstrumentationRunner ("androidx.test.runner.AndroidJUnitRunner")
    }

    publishing {
        singleVariant("release") {
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
            )
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
                artifactId = ArtifactId.DATABASE_HELPERS
                version = Publish.VERSION_STRING
            }
        }
    }
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(ConfigurationData.JAVA_LANG_VERSION))
    }

    jvmToolchain(ConfigurationData.JAVA_LANG_VERSION)
}

dependencies {
    api (project(":core"))
}
