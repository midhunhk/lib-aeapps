import com.ae.apps.lib.ArtifactId
import com.ae.apps.lib.ConfigurationData
import com.ae.apps.lib.Libs
import com.ae.apps.lib.Publish

plugins {
    id ("com.android.library")
    id ("maven-publish")
    id ("org.jetbrains.dokka")
}

afterEvaluate {
    publishing {
        publications {
            // creates a maven publication called release
            register<MavenPublication>("release") {
                from (components["release"])

                groupId = Publish.GROUP_ID
                artifactId = ArtifactId.MULTI_CONTACT
                version = Publish.VERSION_STRING
            }
        }
    }
}

android {
    namespace = "com.ae.apps.lib.multi_contact"
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
            withSourcesJar()
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

dependencies {
    // Modules
    // core: models, EmptyRecyclerView and the dimensions and colours used by the layouts
    api (project(":core"))

    // Libraries
    // Exposed in the public API of this module
    api(Libs.AndroidX.APPCOMPAT)
    api(Libs.AndroidX.RECYCLER_VIEW)

    // Test Dependencies
    testImplementation (Libs.Test.JUNIT)

    androidTestImplementation (Libs.Test.TEST_RUNNER)
    androidTestImplementation (Libs.Test.Mockito.MOCKITO_CORE)
}
