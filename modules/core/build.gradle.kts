import com.ae.apps.lib.ArtifactId
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

                groupId = Publish.GROUP_ID
                artifactId = ArtifactId.CORE
                version = Publish.VERSION_STRING
            }
        }
    }
}

dependencies {
    implementation(Libs.AndroidX.APPCOMPAT)
    implementation(Libs.AndroidX.RECYCLER_VIEW)
    implementation(Libs.GooglePlay.IN_APP_REVIEW)

    testImplementation(Libs.Test.JUNIT)
    testImplementation(Libs.Test.MOCKITO_CORE)
    testImplementation (project(":mocks"))

    androidTestImplementation (Libs.Test.TEST_RUNNER)
    androidTestImplementation (Libs.Test.ESPRESSO_CORE)
    androidTestImplementation (Libs.Test.MOCKITO_CORE)
    androidTestImplementation (Libs.Test.JUNIT_EXT)
}
