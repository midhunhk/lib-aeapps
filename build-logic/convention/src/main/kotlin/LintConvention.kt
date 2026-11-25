import com.android.build.api.dsl.Lint
import org.gradle.api.Project

/**
 * Lint settings shared by the library modules and the sample app.
 *
 * Every warning fails the build, except warnings already recorded in the module's
 * `lint-baseline.xml`. To accept the current warnings after a deliberate change, run
 * `./gradlew updateLintBaseline` and commit the baseline diff.
 */
fun Lint.configureLint(project: Project) {
    warningsAsErrors = true
    abortOnError = true
    baseline = project.file("lint-baseline.xml")
    // Dependency versions are kept current by Dependabot; these checks only report that a
    // newer version exists, which would fail the build every time a library ships a release.
    disable += setOf(
        "GradleDependency",
        "NewerVersionAvailable",
        "AndroidGradlePluginVersion",
        // Lint checks bundled with a library that were built for an older lint version.
        // Nothing in this project can fix them; lint skips them either way.
        "ObsoleteLintCustomCheck",
    )
}
