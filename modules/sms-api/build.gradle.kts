plugins {
    alias(libs.plugins.aeapps.android.library)
}

android {
    namespace = "com.ae.apps.lib.sms_api"
}

dependencies {
    api(project(":core"))

    implementation(libs.androidx.appcompat)

    testImplementation(project(":mocks"))
}
