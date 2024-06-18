import com.ae.apps.lib.ArtifactId
import com.ae.apps.lib.ConfigurationData
import com.ae.apps.lib.Libs
import com.ae.apps.lib.Publish

plugins {
    id ("com.android.library")
    id ("maven-publish")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.ae.apps.lib.billing_client"
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

afterEvaluate {
    publishing {
        publications {
            // creates a maven publication called release
            register<MavenPublication>("release") {
                from (components["release"])

                groupId = Publish.GROUP_ID
                artifactId = ArtifactId.BILLING_CLIENT
                version = Publish.VERSION_STRING
            }
        }
    }
}

dependencies {
    implementation (Libs.AndroidX.APPCOMPAT)
    implementation (Libs.GooglePlay.BILLING_CLIENT)
    implementation (Libs.Google.GUAVA)
    implementation (Libs.Kotlin.KTX_CORE)

    testImplementation (Libs.Test.JUNIT)
    androidTestImplementation (Libs.Test.TEST_RUNNER)
    androidTestImplementation (Libs.Test.ESPRESSO_CORE)
}