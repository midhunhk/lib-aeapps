plugins {
    alias(libs.plugins.aeapps.android.library)
}

android {
    namespace = "com.ae.apps.lib.aeapps"
}

dependencies {
    implementation(libs.androidx.appcompat)

    // The umbrella artifact: every library module
    api(project(":core"))
    api(project(":mocks"))
    api(project(":contacts-api"))
    api(project(":multi-contact"))
    api(project(":database-helpers"))
    api(project(":runtime-permissions"))
    api(project(":billing-client"))
    api(project(":sms-api"))
    api(project(":utilities"))
}
