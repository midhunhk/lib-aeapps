plugins {
    alias(libs.plugins.aeapps.android.library)
}

android {
    namespace = "com.ae.apps.lib.runtime_permissions"
}

dependencies {
    // Exposed in the public API of this module
    api(libs.androidx.appcompat)
    api(libs.androidx.fragment)
}
