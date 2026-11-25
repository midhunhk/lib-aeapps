plugins {
    alias(libs.plugins.aeapps.android.library)
}

android {
    namespace = "com.ae.apps.lib.billing_client"
}

dependencies {
    // Exposed in the public API of this module
    api(libs.androidx.appcompat)
    api(libs.play.billing)

    testImplementation(libs.mockito.kotlin)
}
