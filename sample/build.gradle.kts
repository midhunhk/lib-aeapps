
/*
 * Copyright (c) 2019 Midhun Harikumar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.ae.apps.lib.sample"

    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.ae.apps.lib.sample"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 14
        versionName = "1.5.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Feature flag for the developer-only "Generate Contacts" screen. It is off by default and
        // only switched on for debug builds below, so builds published to the Play Store never show
        // it. The screen, its manifest entry and the WRITE_CONTACTS permission live in src/debug,
        // so release builds do not contain them at all.
        buildConfigField("boolean", "FEATURE_CONTACT_GENERATOR", "false")
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        debug {
            buildConfigField("boolean", "FEATURE_CONTACT_GENERATOR", "true")
        }
        release {
            isMinifyEnabled = false
            proguardFiles (
                    getDefaultProguardFile("proguard-android-optimize.txt"), 
                    "proguard-rules.pro")
        }
    }
}
dependencies {

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.material)

    // Use these to test library features while in development
    api (project(":core"))
    api (project(":contacts-api"))
    api (project(":sms-api"))
    api (project(":runtime-permissions"))
    api (project(":utilities"))
    api (project(":multi-contact"))

    // Otherwise use these to build sample features against published API
    /*
    def libAeAppsVersion = "4.1.0-beta.05"
    implementation "com.github.midhunhk.lib-aeapps:core:$libAeAppsVersion"
    implementation "com.github.midhunhk.lib-aeapps:contacts-api:$libAeAppsVersion"
    implementation "com.github.midhunhk.lib-aeapps:sms-api:$libAeAppsVersion"
    implementation "com.github.midhunhk.lib-aeapps:runtime-permissions:$libAeAppsVersion"
    implementation "com.github.midhunhk.lib-aeapps:utilities:$libAeAppsVersion"
    implementation "com.github.midhunhk.lib-aeapps:multi-contact:$libAeAppsVersion"
     */
}
