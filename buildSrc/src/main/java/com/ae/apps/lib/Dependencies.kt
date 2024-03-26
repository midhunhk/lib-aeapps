package com.ae.apps.lib

object Libs {

    object AndroidX {
        const val APPCOMPAT = "androidx.appcompat:appcompat:1.6.1"
        const val RECYCLER_VIEW = "androidx.recyclerview:recyclerview:1.3.0"
        const val CONSTRAINT_LAYOUT = "androidx.constraintlayout:constraintlayout:2.1.4"
    }

    object Kotlin{
        const val KTX_CORE = "androidx.core:core-ktx:1.10.1"
    }

    object Google {
        const val GUAVA = "com.google.guava:guava:31.0.1-android"
    }

    object GooglePlay {
        private const val  BILLING_VERSION = "6.2.0"
        const val PLAY_CORE  = "com.google.android.play:core:1.10.3"
        const val BILLING_CLIENT = "com.android.billingclient:billing:$BILLING_VERSION"
    }

    object Material {
        const val DESIGN  = "com.google.android.material:material:1.11.0"
    }

    object Test {
        private const val JUNIT_VERSION = "4.13.2"
        private const val MOCKITO_CORE_VERSION = "2.19.0"

        const val JUNIT ="junit:junit:$JUNIT_VERSION"
        const val MOCKITO_CORE = "org.mockito:mockito-core:$MOCKITO_CORE_VERSION"
        const val TEST_RUNNER = "androidx.test:runner:1.5.2"
        const val ESPRESSO_CORE = "androidx.test.espresso:espresso-core:3.3.0"
        const val JUNIT_EXT = "androidx.test.ext:junit:1.1.5"
    }
}
