import com.ae.apps.lib.Libs

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
    id ("com.android.application")
}

android {
    namespace = "com.ae.apps.lib.sample"

    compileSdk = 33

    defaultConfig {
        applicationId = "com.ae.apps.lib.sample"
        minSdk = 21
        targetSdk = 33
        versionCode = 8
        versionName = "1.4.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles (
                    getDefaultProguardFile("proguard-android.txt"), 
                    "proguard-rules.pro")
        }
    }

    android {
    }
}
dependencies {

    implementation(Libs.AndroidX.APPCOMPAT)
    implementation(Libs.AndroidX.RECYCLER_VIEW)
    implementation(Libs.AndroidX.CONSTRAINT_LAYOUT)
    implementation(Libs.Material.DESIGN)

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
