plugins {
    id ("com.android.library")
    id ("maven-publish")
}

afterEvaluate {
    publishing {
        publications {
            // creates a maven publication called release
            register<MavenPublication>("release") {
                from (components["release"])

                groupId = Publish.groupId
                artifactId = "multi-contact"
                version = Publish.version
            }
        }
    }
}

android {
    namespace = "com.ae.apps.lib.multi_contact"
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

dependencies {
    // Modules
    api (project(":core"))
    api (project(":utilities"))

    // Libraries
    implementation(Libs.AndroidX.appcompat)
    implementation(Libs.AndroidX.recyclerview)

    // Test Dependencies
    testImplementation (Libs.Test.junit)

    androidTestImplementation (Libs.Test.runner)
    androidTestImplementation (Libs.Test.mockitoCore)
}
