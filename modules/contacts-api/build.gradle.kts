plugins {
    alias(libs.plugins.aeapps.android.library)
}

android {
    namespace = "com.ae.apps.lib.contacts_api"
    testOptions {
        // Let android.util.Log and similar framework calls be no-ops in JVM unit tests
        unitTests.isReturnDefaultValues = true
    }
}

dependencies {
    api(project(":core"))

    implementation(libs.androidx.appcompat)

    testImplementation(project(":mocks"))
}
