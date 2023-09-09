plugins {
    id ("com.android.library")
    id ("maven-publish")
}

android {
    namespace = "com.ae.apps.lib.contacts_api"
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
                artifactId = "contacts-api"
                version = Publish.version
            }
        }
    }
}

dependencies {
    implementation(Libs.AndroidX.appcompat)

    api (project(":core"))
    api (project(":utilities"))

    testImplementation (project(":mocks"))
    testImplementation(Libs.Test.junit)
    testImplementation(Libs.Test.mockitoCore)

    androidTestImplementation (Libs.Test.runner)
    androidTestImplementation (Libs.Test.mockitoCore)
}