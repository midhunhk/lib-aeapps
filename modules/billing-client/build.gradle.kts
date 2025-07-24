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
    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
    }
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(ConfigurationData.JAVA_LANG_VERSION))
    }

    jvmToolchain(ConfigurationData.JAVA_LANG_VERSION)
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

    testImplementation (Libs.Test.JunitJupiter.API)
    testRuntimeOnly(Libs.Test.JunitJupiter.ENGINE)
    testRuntimeOnly(Libs.Test.JunitJupiter.LAUNCHER)
    testImplementation(Libs.Test.Mockito.MOCKITO_JUNIT)
    testImplementation(Libs.Test.Mockito.MOCKITO_KOTLIN)

    androidTestImplementation (Libs.Test.TEST_RUNNER)
    androidTestImplementation (Libs.Test.ESPRESSO_CORE)
}