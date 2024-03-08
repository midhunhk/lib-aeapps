import com.ae.apps.lib.ConfigurationData
import com.ae.apps.lib.Libs
import com.ae.apps.lib.Publish

plugins {
    id ("com.android.library")
    id ("org.jetbrains.kotlin.android")
    id ("maven-publish")
}

android {
    namespace = "com.ae.apps.lib.core"
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
        release {
            isMinifyEnabled = false
            proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

afterEvaluate {
    publishing {
        publications {
            // creates a maven publication called release
            register<MavenPublication>("release") {
                from (components["release"])

                groupId = Publish.groupId
                artifactId = "core"
                version = Publish.version
            }
        }
    }
}

dependencies {
    implementation(Libs.AndroidX.appcompat)
    implementation(Libs.AndroidX.recyclerview)
    implementation(Libs.GooglePlay.playCore)

    testImplementation(Libs.Test.junit)
    testImplementation(Libs.Test.mockitoCore)
    testImplementation (project(":mocks"))

    androidTestImplementation (Libs.Test.runner)
    androidTestImplementation (Libs.Test.espresso)
    androidTestImplementation (Libs.Test.mockitoCore)
    androidTestImplementation (Libs.Test.extJunit)
}
