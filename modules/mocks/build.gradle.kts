plugins {
    alias(libs.plugins.aeapps.android.library)
}

android {
    namespace = "com.ae.apps.lib.mocks"
}

dependencies {
    api(project(":core"))

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)

    testImplementation(libs.mockito.kotlin)
}
