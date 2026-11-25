package com.ae.apps.lib

object Libs {

    object AndroidX {
        private const val APPCOMPAT_VERSION = "1.7.1"
        const val CORE_KTX = "androidx.core:core-ktx:1.16.0"
        const val APPCOMPAT = "androidx.appcompat:appcompat:$APPCOMPAT_VERSION"
        @Suppress("unused")
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

        @Suppress("unused")
        object Kotlin {
            @Suppress("unused")
            const val IN_APP_REVIEW = "com.google.android.play:review-ktx:2.0.1"
            @Suppress("unused")
            const val IN_APP_UPDATE = "com.google.android.play:app-update-ktx:2.1.0"
        }
    }

    object Material {
        const val DESIGN  = "com.google.android.material:material:1.11.0"
    }

    object Test {
        // JUnit 6 is the continuation of JUnit 5 (Jupiter); same APIs, requires Java 17
        private const val JUNIT_VERSION = "6.1.3"
        private const val MOCKITO_VERSION = "5.24.0"

        const val JUNIT_BOM = "org.junit:junit-bom:$JUNIT_VERSION"
        const val JUNIT_JUPITER = "org.junit.jupiter:junit-jupiter"
        const val JUNIT_PLATFORM_LAUNCHER = "org.junit.platform:junit-platform-launcher"

        object Mockito {
            const val MOCKITO_CORE = "org.mockito:mockito-core:$MOCKITO_VERSION"
            const val MOCKITO_JUNIT = "org.mockito:mockito-junit-jupiter:$MOCKITO_VERSION"
            const val MOCKITO_KOTLIN = "org.mockito.kotlin:mockito-kotlin:6.4.0"
        }
    }
}
