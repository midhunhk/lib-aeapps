plugins {
    alias(libs.plugins.aeapps.android.library)
}

android {
    namespace = "com.ae.apps.lib.utilities"
}

dependencies {
    implementation(libs.androidx.appcompat)
}
