plugins {
    alias(libs.plugins.aeapps.android.library)
}

android {
    namespace = "com.ae.apps.lib.multi_contact"
}

dependencies {
    // core: models, EmptyRecyclerView and the dimensions and colours used by the layouts
    api(project(":core"))

    // Exposed in the public API of this module
    api(libs.androidx.appcompat)
    api(libs.androidx.recyclerview)
}
