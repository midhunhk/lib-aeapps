pluginManagement {

    /**
     * The pluginManagement.repositories block configures the
     * repositories Gradle uses to search or download the Gradle plugins and
     * their transitive dependencies. Gradle pre-configures support for remote
     * repositories such as JCenter, Maven Central, and Ivy. You can also use
     * local repositories or define your own remote repositories. The code below
     * defines the Gradle Plugin Portal, Google"s Maven repository,
     * and the Maven Central Repository as the repositories Gradle should use to look for its
     * dependencies.
     */

    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven (url = "https://jitpack.io")
    }
}
dependencyResolutionManagement {

    /**
     * The dependencyResolutionManagement.repositories
     * block is where you configure the repositories and dependencies used by
     * all modules in your project, such as libraries that you are using to
     * create your application. However, you should configure module-specific
     * dependencies in each module-level build.gradle file. For new projects,
     * Android Studio includes Google's Maven repository and the Maven Central
     * Repository by default, but it does not configure any dependencies (unless
     * you select a template that requires some).
     */

    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven ( url = "https://jitpack.io" )
    }
}
rootProject.name = "lib-aeapps"

include (":core")
include (":mocks")
include (":utilities")
include (":contacts-api")
include (":multi-contact")
include (":runtime-permissions")
include (":database-helpers")
include (":billing-client")
include (":sms-api")
include (":aeapps")

include (":sample")

project(":core").projectDir = file("modules/core")
project(":mocks").projectDir = file("modules/mocks")
project(":utilities").projectDir = file("modules/utilities")
project(":contacts-api").projectDir = file("modules/contacts-api")
project(":multi-contact").projectDir = file("modules/multi-contact")
project(":runtime-permissions").projectDir = file("modules/runtime-permissions")
project(":database-helpers").projectDir = file("modules/database-helpers")
project(":billing-client").projectDir = file("modules/billing-client")
project(":sms-api").projectDir = file("modules/sms-api")

project(":aeapps").projectDir = file("modules/aeapps")

