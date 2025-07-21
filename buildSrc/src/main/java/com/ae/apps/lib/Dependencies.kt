package com.ae.apps.lib

object Libs {

    object AndroidX {
        private const val APPCOMPAT_VERSION = "1.7.1"
        const val CORE_KTX = "androidx.core:core-ktx:1.16.0"
        const val APPCOMPAT = "androidx.appcompat:appcompat:$APPCOMPAT_VERSION"
        const val APPCOMPAT_RESOURCES = "androidx.appcompat:appcompat-resources:$APPCOMPAT_VERSION"
        const val FRAGMENT = "androidx.fragment:fragment:1.8.8"
        const val RECYCLER_VIEW = "androidx.recyclerview:recyclerview:1.4.0"
        const val PREFERENCE = "androidx.preference:preference:1.2.1"
        const val CONSTRAINT_LAYOUT = "androidx.constraintlayout:constraintlayout:2.2.1"
    }

    object Google {
        const val GUAVA = "com.google.guava:guava:31.0.1-android"
    }

    object GooglePlay {
        private const val BILLING_VERSION = "8.0.0"
        const val BILLING_CLIENT = "com.android.billingclient:billing:$BILLING_VERSION"
        const val IN_APP_REVIEW = "com.google.android.play:review:2.0.1"

        object Kotlin {
            const val IN_APP_REVIEW = "com.google.android.play:review-ktx:2.0.1"
            const val IN_APP_UPDATE = "com.google.android.play:app-update-ktx:2.1.0"
        }
    }

    object Material {
        const val DESIGN  = "com.google.android.material:material:1.11.0"
    }

    object Test {
        private const val JUNIT_VERSION = "4.13.2"
        private const val JUNIT5_VERSION = "5.13.4"
        private const val MOCKITO_CORE_VERSION = "2.19.0"

        const val JUNIT ="junit:junit:$JUNIT_VERSION"
        const val MOCKITO_CORE = "org.mockito:mockito-core:$MOCKITO_CORE_VERSION"
        const val TEST_RUNNER = "androidx.test:runner:1.6.2"
        const val ESPRESSO_CORE = "androidx.test.espresso:espresso-core:3.6.1"
        const val JUNIT_EXT = "androidx.test.ext:junit:1.2.1"
        const val RULES = "androidx.test:rules:1.6.1"
        const val ROBO_ELECTRIC = "org.robolectric:robolectric:4.13"
        const val TEST_NG = "org.testng:testng:6.9.6"

        // junit5
        object JUNITJUPITER {
            const val JUNIT5 = "org.junit.jupiter:junit-jupiter:$JUNIT5_VERSION"
            const val API = "org.junit.jupiter:junit-jupiter-api:$JUNIT5_VERSION"
            const val PARAMS = "org.junit.jupiter:junit-jupiter-params:$JUNIT5_VERSION"
            const val MOCKITO_EXT = "org.mockito:mockito-junit-jupiter:5.10.0"
        }
    }
}
