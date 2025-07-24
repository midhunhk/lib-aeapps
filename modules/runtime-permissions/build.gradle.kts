import com.ae.apps.lib.ArtifactId
import com.ae.apps.lib.ConfigurationData
import com.ae.apps.lib.Libs
import com.ae.apps.lib.Publish

plugins {
    id ("com.android.library")
    id ("maven-publish")
}

android {
    namespace = "com.ae.apps.lib.runtime_permissions"
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
}

afterEvaluate {
    publishing {
        publications {
            // creates a maven publication called release
            register<MavenPublication>("release") {
                from (components["release"])

                groupId = Publish.GROUP_ID
                artifactId = ArtifactId.RUNTIME_PERMISSIONS
                version = Publish.VERSION_STRING
            }
        }
    }
}

dependencies {
    api (project(":core"))
    api (project(":utilities"))

    // Libraries
    implementation(Libs.AndroidX.APPCOMPAT)
    implementation(Libs.AndroidX.FRAGMENT)

    // Test Dependencies
    testImplementation (Libs.Test.JUNIT)
    testImplementation (Libs.Test.Mockito.MOCKITO_CORE)

    androidTestImplementation (Libs.Test.TEST_RUNNER)
    androidTestImplementation (Libs.Test.ESPRESSO_CORE)
}
