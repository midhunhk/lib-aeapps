plugins {
    alias(libs.plugins.aeapps.android.library)
}

android {
    namespace = "com.ae.apps.lib.core"
}

dependencies {
    // Exposed in the public API of this module
    api(libs.androidx.appcompat)
    api(libs.androidx.recyclerview)

    implementation(libs.androidx.preference)
    implementation(libs.play.review)
}
