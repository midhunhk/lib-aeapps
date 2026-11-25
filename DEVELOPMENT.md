# lib-aeapps — Project Structure and Release Guide

Written 2026-11-10 against `lingonberry` at `b6b14fd`.

This page describes how the repository is laid out, how the build is wired, and the steps that
are specific to this project when cutting a release. It describes the project **as it is today**;
planned changes live in [RELEASE-PLAN-v5.md](guides/RELEASE-PLAN-v5.md). Where the two disagree, this page is
the "now" and the plan is the "next".

---

## 1. Repository layout

```
lib-aeapps/
├── settings.gradle.kts        Module list and directory mapping
├── build.gradle.kts           Plugin versions (AGP, Kotlin) — nothing else
├── gradle.properties          Library version and group (aeapps.*), AndroidX and Gradle flags
├── gradle/
│   ├── libs.versions.toml     Version catalog: every dependency, plugin and SDK level
│   └── wrapper/               Gradle wrapper
├── jitpack.yml                Tells JitPack to build with JDK 17
├── local_release.bat          Publishes all modules to ~/.m2 for local testing
│
├── build-logic/               Included build with the convention plugin (see section 3)
│   └── convention/src/main/kotlin/
│       └── AndroidLibraryConventionPlugin.kt   aeapps.android.library
│
├── modules/                   The library — one directory per published artifact
│   ├── core/
│   ├── utilities/
│   ├── contacts-api/
│   ├── sms-api/
│   ├── runtime-permissions/
│   ├── multi-contact/
│   ├── billing-client/
│   ├── mocks/
│   └── aeapps/                Umbrella artifact, no code of its own
│
├── sample/                    Demo application (not published)
│
├── docs/                      GitHub Pages site (see section 3.5)
│   ├── index.html             Landing page (hand-written)
│   ├── release_notes.html     Generated from VersionHistory.md (not committed)
│   ├── api/                   Generated API reference, Dokka (not committed)
│   ├── assets/                site.css, logo and favicon
│   └── resources/v4/          Screenshots and icons
│
├── scripts/
│   └── release_notes.py       Builds docs/release_notes.html (run by updateSite)
│
├── .github/
│   ├── workflows/ci.yml       Unit tests, lint, release build, local publish
│   ├── workflows/pages.yml    Builds and deploys the docs site on release tags
│   ├── CONTRIBUTING.md, CODE_OF_CONDUCT.md, ISSUE_TEMPLATE/
│
├── README.md
├── VersionHistory.md          Markdown changelog (the one the README links to)
├── guides/                    Versioned planning and migration documents
│   ├── RELEASE-PLAN-v5.md     5.0 analysis and plan
│   ├── MIGRATION-v4.md        Client migration guide, 3.x → 4.1
│   └── MIGRATION-v5.md        Client migration guide, 4.1 → 5.0
└── LICENSE                    Apache 2.0
```

The Gradle project names are flat (`:core`, `:sms-api`, …) even though the directories sit under
`modules/`. The mapping is done by hand at the bottom of
[settings.gradle.kts](settings.gradle.kts), so **adding a module means two edits there**: an
`include(":name")` line and a `project(":name").projectDir = file("modules/name")` line.

---

## 2. Modules

### 2.1 What each module is

| Gradle project | Published artifact | Namespace | Language | Contents |
|---|---|---|---|---|
| `:core` | `core` | `com.ae.apps.lib.core` | Java | Models (`ContactInfo`, `MessageInfo`, `PhoneNumberInfo`), custom views, `ToolBarBaseActivity`, in-app review, shared drawables/colours/dimens |
| `:utilities` | `utilities` | `com.ae.apps.lib.utilities` | Java | Static helpers for intents, dialogs, telephony, package checks |
| `:contacts-api` | `contacts-api` | `com.ae.apps.lib.contacts_api` | Java | `ContactsApiGateway` and implementation |
| `:sms-api` | `sms-api` | `com.ae.apps.lib.sms_api` | Java | `SmsApiGateway` and implementation |
| `:runtime-permissions` | `runtime-permissions` | `com.ae.apps.lib.runtime_permissions` | Java | Permission-aware base Activity/Fragment, `RuntimePermissionChecker` |
| `:multi-contact` | `multi-contact` | `com.ae.apps.lib.multi_contact` | Java | Multi-select contact picker base Activity, layouts |
| `:billing-client` | `billing-client` | `com.ae.apps.lib.billing_client` | Kotlin | `AeBillingClient` over Play Billing 8 |
| `:mocks` | `mocks` | `com.ae.apps.lib.mocks` | Kotlin | Fake contacts and messages, mock names in en/es/fr |
| `:aeapps` | `aeapps` | `com.ae.apps.lib.aeapps` | — | `api` dependency on all eight modules above |
| `:sample` | not published | `com.ae.apps.lib.sample` | Java | Demo app |

Two naming details worth knowing:

- **Namespace ≠ Java package.** The Gradle `namespace` (which decides where `R` is generated)
  uses underscores, e.g. `com.ae.apps.lib.multi_contact`, while the source package is
  `com.ae.apps.lib.multicontact`. With `android.nonTransitiveRClass=true`, code must import the
  `R` of the module that owns the resource — for example `multi-contact` references
  `com.ae.apps.lib.multi_contact.R.id.list` explicitly.
- **Artifact IDs use hyphens** and are the Gradle project names (`:sms-api` publishes
  `sms-api`); the convention plugin sets them. Before that they were typed by hand, and an
  underscore slipped into `sms_api` once and needed a follow-up release (beta.02) to correct.

### 2.2 Dependencies between modules

```
core  ◄── contacts-api
  ▲  ◄── multi-contact
  ├──── sms-api
  └──── mocks

utilities            (no module dependencies)
runtime-permissions  (no module dependencies)
billing-client       (no module dependencies)

aeapps ──► all eight (api)
sample ──► core, contacts-api, sms-api, runtime-permissions, utilities, multi-contact
```

This is the graph from 5.1. In 5.0, `contacts-api`, `multi-contact` and `runtime-permissions`
also depended on `utilities`, and `runtime-permissions` and `database-helpers` on `core`; those
dependencies were unused and were removed. 6.0 removed the `database-helpers` module (#57); use
Room instead.

All module-to-module edges are `api`, so they appear as compile-scope dependencies in the
published POMs and a consumer of `contacts-api` gets `core` automatically. Third-party libraries
that appear in a module's public API are `api` as well; the rest are `implementation`.

**What goes where.** `core` holds what other modules are built on: the shared models, custom
views, `ToolBarBaseActivity`, in-app review and shared resources. `utilities` holds stand-alone
static helpers that apps call directly. A library module should depend on `utilities` only if
it uses one of those helpers, and on `core` only if it uses a model, view or resource from it.

### 2.3 Inside a module

Every library module has the same shape:

```
modules/<name>/
├── build.gradle.kts       Convention plugin, namespace and dependencies only
├── api/<module>.api       Public API dump, checked in CI (section 3.7)
├── consumer-rules.pro     Empty (shipped to consumers; nothing needs keeping)
├── proguard-rules.pro     Generic template; unused because minify is off
└── src/
    ├── main/
    │   ├── AndroidManifest.xml    Empty <manifest/> — no permissions declared
    │   ├── java/…                 Java and Kotlin both live under java/
    │   └── res/
    └── test/
        ├── java/…                 Java tests (JUnit 6 / Jupiter)
        └── kotlin/…               Kotlin tests (billing-client, mocks)
```

A module's `build.gradle.kts` looks like this; everything else comes from the convention plugin:

```kotlin
plugins {
    alias(libs.plugins.aeapps.android.library)
}

android {
    namespace = "com.ae.apps.lib.contacts_api"
}

dependencies {
    api(project(":core"))
    implementation(libs.androidx.appcompat)
    testImplementation(project(":mocks"))
}
```

Because the library manifests are empty, **the consuming app must declare permissions itself**
(`READ_CONTACTS`, `READ_SMS`, `CALL_PHONE`, and `<queries>` entries for package checks).

All unit tests use **JUnit 6 (Jupiter) and Mockito** on the JUnit Platform. The setup is shared
and lives in the convention plugin, so a module's own script only adds what is specific to
it (for example `mockito-kotlin` for Kotlin tests, or `project(":mocks")`). See section 3.6.

| Has unit tests | No unit tests yet |
|---|---|
| `contacts-api`, `sms-api`, `runtime-permissions`, `utilities`, `billing-client`, `mocks` | `core`, `multi-contact`, `aeapps`, `sample` |

There are no instrumented tests (`src/androidTest`) yet; a smoke test for the picker and
permission flows is planned with the 6.0 API rewrite.

---

## 3. Build configuration

### 3.1 Where each setting lives

| To change… | Edit |
|---|---|
| AGP, Kotlin, Dokka or Kover version | [libs.versions.toml](gradle/libs.versions.toml) `[versions]` |
| A dependency or its version | [libs.versions.toml](gradle/libs.versions.toml) `[libraries]`, then `libs.…` in the module |
| compileSdk, minSdk, targetSdk, Java level | [libs.versions.toml](gradle/libs.versions.toml) (`compileSdk`, `minSdk`, `targetSdk`, `jvmTarget`) — used by every library module and the sample |
| Anything shared by all library modules | [AndroidLibraryConventionPlugin.kt](build-logic/convention/src/main/kotlin/AndroidLibraryConventionPlugin.kt) |
| Library version and group | [gradle.properties](gradle.properties) (`aeapps.version`, `aeapps.group`) |
| A module's namespace or dependencies | That module's `build.gradle.kts` |
| Gradle version | `gradle/wrapper/gradle-wrapper.properties` (or `./gradlew wrapper --gradle-version …`) |
| JDK used by JitPack | [jitpack.yml](jitpack.yml) |
| Sample app version | [sample/build.gradle.kts](sample/build.gradle.kts) |

**The convention plugin** (`aeapps.android.library`, in the `build-logic` included build) is
applied by every library module. It applies AGP, Kotlin, `maven-publish`, Dokka and Kover, and
sets: the SDK and Java levels; consumer ProGuard rules; a release build type without
minification; a `release` publication with sources whose artifact ID is the project name; the
test dependencies and JUnit Platform; and the Dokka options. Change it there, not in a module.

Things that are easy to miss:

- Every library module applies the Kotlin plugin, including those still written in Java, so
  their POMs list `kotlin-stdlib`. Kover and Dokka rely on it.
- The `build-logic` build compiles against AGP, Kotlin and Dokka at the catalog's versions, so
  upgrading one of them is still a one-line change in the catalog.
- Lint reads the catalog and reports newer dependency versions as `GradleDependency` and
  `NewerVersionAvailable` warnings (shown once, on `core`).
- `org.gradle.configuration-cache=true` is on. A new plugin or task that is not
  configuration-cache compatible will fail the build rather than warn.
- JDK 17 is required locally and is what CI and JitPack use.

### 3.2 Current tool versions

| Tool | Version |
|---|---|
| Gradle | 9.6.0 |
| Android Gradle Plugin | 9.5.2 |
| Kotlin | 2.2.10 |
| compileSdk | 37 |
| minSdk | 26 (Android 8.0), from 6.0 (#56) |
| Java source/target | 17 |

AGP and the Gradle wrapper are coupled — each AGP release has a minimum Gradle version, so
upgrade them together and check the AGP release notes for the pairing.

### 3.3 Everyday commands

```bash
./gradlew testDebugUnitTest --continue
```

```bash
./gradlew :koverHtmlReport
```

```bash
./gradlew lintDebug --continue
```

```bash
./gradlew assembleRelease
```

```bash
./gradlew publishReleasePublicationToMavenLocal
```

```bash
./gradlew :sample:installDebug
```

On Windows use `gradlew.bat`, or `local_release.bat` for the Maven Local publish. `gradlew` is
committed without the executable bit, so on Linux/macOS run `chmod +x gradlew` once (CI does
this in the workflow).

### 3.4 Continuous integration

[.github/workflows/ci.yml](.github/workflows/ci.yml) runs on every push to any branch, on pull
requests to `main`, and on manual dispatch. It runs unit tests, lint, the release build and a
publish to Maven Local, and uploads test and lint reports if anything fails. It starts with
`apiCheck`, which fails if a module's public API changed without an updated dump (section 3.7). It builds a
coverage report on every run, shows the overall line coverage in the run's summary and uploads
the HTML and XML report as the `coverage` artifact. It also builds the
API reference to catch broken Dokka configuration, but does not commit it. It does not publish
anywhere and does not create tags or releases.

**Pinned actions.** Every action in the workflows is pinned to a full commit SHA, with its
version as a comment (`actions/checkout@fbc6f39… # v5.1.0`). A tag such as `@v5` can be moved
to different code; a SHA cannot. Don't hand-edit the pins; Dependabot updates the SHA and the
comment together.

**Dependency updates.** [.github/dependabot.yml](.github/dependabot.yml) checks every Monday
for newer versions of the libraries and plugins in `gradle/libs.versions.toml` and of the
GitHub Actions, and opens pull requests against the current development branch
(`target-branch`, currently `nannyberry`):

- Minor and patch updates are grouped: build plugins (AGP, Kotlin, Dokka, Kover, the API
  validator), AndroidX and Material, Google Play, test libraries, and Actions. Each group is one
  pull request.
- A major update of anything gets its own pull request, because it may need code changes.
- CI runs on each update branch. Review the changelog of anything that is not a patch release
  before merging, and run `./gradlew apiCheck` if a dependency in a module's public API changed.

Dependabot reads its configuration from the default branch, so changes to `dependabot.yml`
take effect only once they reach `main`.

### 3.5 Documentation site

`docs/` is the source of the GitHub Pages site. Only the hand-written parts are committed:

| Path | Committed | Source |
|---|---|---|
| `docs/index.html` | Yes | Hand-written; styles in `docs/assets/css/site.css` (no framework, light and dark themes) |
| `docs/assets/`, `docs/resources/` | Yes | Stylesheet, logo, favicon, screenshots |
| `docs/api/` | No (git-ignored) | Generated by Dokka from KDoc and Javadoc in the library modules |
| `docs/release_notes.html` | No (git-ignored) | Generated from `VersionHistory.md` |

**Publishing.** [.github/workflows/pages.yml](.github/workflows/pages.yml) builds and deploys the
site. It runs when a release tag (`v*`) is pushed, and on demand from the Actions tab — use that
after editing `docs/index.html` between releases. It runs `./gradlew updateSite`, then uploads
`docs/` and deploys it. Nothing generated is committed back, so releases need no docs commit and
pull requests show only source changes.

One-time GitHub setup (repository settings):

- **Settings → Pages → Build and deployment → Source:** "GitHub Actions".
- **Settings → Environments → `github-pages` → Deployment branches and tags:** allow tags
  matching `v*`. By default only the default branch may deploy, so tag-triggered runs are
  rejected until this rule exists.

**Generating locally.** The same command builds the full site for previewing:

```bash
./gradlew updateSite
```

It runs three tasks, each also usable on its own (all in the `documentation` group):

| Task | What it updates | From |
|---|---|---|
| `updateApiDocs` | `docs/api/` — regenerated with Dokka; pages for removed classes are deleted | KDoc and Javadoc in the library modules |
| `updateReleaseNotes` | `docs/release_notes.html` | `VersionHistory.md`, via `scripts/release_notes.py` |
| `updateSiteVersion` | The version in the `docs/index.html` dependency snippet | `aeapps.version` in `gradle.properties`, with a `v` prefix |

`updateReleaseNotes` needs Python 3 on the `PATH` (`python` on Windows, `python3` elsewhere).
Pass `-PpythonExecutable=/path/to/python` to use a different one.

The generated files are git-ignored, so running `updateSite` locally leaves the working tree
clean, except that `updateSiteVersion` may change the version line in `docs/index.html`. Commit
that change or not, as you like: the published page always gets its version from
`aeapps.version`.

Dokka is applied by the convention plugin and combined in the root
[build.gradle.kts](build.gradle.kts). It documents the nine
library modules (not `sample` or the empty `aeapps` umbrella), Java and Kotlin together, public
and protected members only, and hides members inherited from Android framework classes. The root
script also works around two Dokka limitations with this project: Java-only Android modules get
a source set registered by hand, and Kotlin modules have their duplicate `release` source set
suppressed. If a new library module is added, add it to the root `dokka(project(...))` list.

To preview the site locally, serve `docs/` over HTTP (opening the files directly does not load
the stylesheet in some browsers):

```bash
python -m http.server 8765 --directory docs
```

### 3.6 Tests and coverage

- **Framework:** JUnit 6 (the continuation of JUnit 5's Jupiter API; it needs Java 17) with
  Mockito and `mockito-junit-jupiter`. Use `org.junit.jupiter.api.*` imports and, for mocks,
  `@ExtendWith(MockitoExtension.class)`. Versions are in the catalog (`junit`, `mockito`).
- **Where it is configured:** the convention plugin adds the test dependencies, switches every
  `Test` task to the JUnit Platform and applies Kover to every library module. The root
  `build.gradle.kts` merges the coverage of all modules.
- **Android classes:** unit tests run on the JVM against a stub `android.jar`. Mock framework
  types with Mockito (static ones such as `Uri.parse` with `mockStatic`), or set
  `testOptions.unitTests.isReturnDefaultValues = true` as `contacts-api` does. Robolectric is not
  used yet; add it for tests that need real framework behaviour.
- **Coverage:** Kover instruments the debug unit tests of every library module and merges them at
  the root. `./gradlew :koverHtmlReport` writes `build/reports/kover/html/index.html`,
  `:koverXmlReport` writes `build/reports/kover/report.xml`, and `:koverLog` prints the total.
  Generated classes (`R`, `BuildConfig`) are excluded. Kover only instruments modules that apply
  the Kotlin plugin, which is why every library module applies it, including those that are
  still Java.

### 3.7 Public API

Every library module has a committed dump of its public API in `modules/<module>/api/<module>.api`,
produced by Kotlin's
[binary-compatibility validator](https://github.com/Kotlin/binary-compatibility-validator) and
covering Java and Kotlin classes alike. It lists every public and protected class, method and field
with its JVM signature.

```bash
./gradlew apiCheck
```

```bash
./gradlew apiDump
```

- **`apiCheck`** compares the compiled API with the dump and fails on any difference. CI runs it
  first; it is also part of `./gradlew check`.
- **`apiDump`** rewrites the dumps. Run it when you change the API on purpose and commit the `.api`
  diff with the code: the diff is what reviewers check. Additions are safe in a minor release;
  removals and signature changes break consumers and belong in a major release.
- **Explicit API mode** is on for Kotlin code: every public Kotlin declaration must say `public`
  and declare its return type, so nothing becomes API by accident. Java code is unaffected;
  there, only `public` and `protected` members appear in the dump.
- The validator is applied by the convention plugin; its version is in the catalog.

---

## 4. How publishing works

### 4.1 The mechanism

There is no upload step. Releases are served by **JitPack**, which builds the repository on
demand the first time someone requests a given tag:

1. A consumer's build asks JitPack for `com.github.midhunhk.lib-aeapps:<module>:<version>`.
2. JitPack checks out the matching git tag, reads [jitpack.yml](jitpack.yml) (JDK 17), and runs
   the project's Maven publish tasks into its own local repository.
3. It serves whatever artifacts that build produced.

Each module registers one `MavenPublication` named `release` built from the Android `release`
component, set up by the convention plugin:

```
groupId    = aeapps.group (com.github.midhunhk.lib-aeapps)
artifactId = the project name: core | utilities | contacts-api | sms-api | runtime-permissions |
             multi-contact | billing-client | mocks | aeapps
version    = aeapps.version
```

Consequences:

- **A release exists as soon as the tag is pushed.** There is no staging step and no way to
  "unpublish" other than deleting the tag — and JitPack caches builds, so a deleted and
  re-created tag may keep serving the old artifacts. Treat tags as immutable; if a release is
  wrong, cut the next version.
- **The first consumer pays the build time**, and if the JitPack build fails, consumers see a
  dependency resolution error rather than a build error. Always check the build log on
  jitpack.io after tagging (section 5, step 8).
- Only the `release` variant is published, with no sources or Javadoc jar at present.

### 4.2 Consumer coordinates

```kotlin
// settings.gradle.kts
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
}

// app/build.gradle.kts — pick individual modules…
implementation("com.github.midhunhk.lib-aeapps:core:<version>")
implementation("com.github.midhunhk.lib-aeapps:contacts-api:<version>")

// …or everything
implementation("com.github.midhunhk.lib-aeapps:aeapps:<version>")
```

> **Use the tag name, including the `v`, as `<version>`** — for example `v5.1.0`.
> `aeapps.version` in `gradle.properties` has no prefix, but JitPack serves artifacts under the git tag
> name. Random Contact resolves the library this way (`v5.0.0-beta.02`).

---

## 5. Versioning and release process

### 5.1 Conventions in use

| Thing | Convention | Example |
|---|---|---|
| Version scheme | Semantic versioning with dotted pre-release counters | `5.0.0-alpha.21`, `5.0.0-beta.03`, `4.1.0-rc.01` |
| Git tag | Lightweight tag, `v` + version | `v5.0.0-beta.03` |
| Development branch | One long-lived branch per major/minor line, named after a fruit in alphabetical order | `juneberry` (4.0), `kiwiberry` (4.1), `lingonberry` (5.0) |
| Stable branch | `main` (GitHub default) | Currently still at 4.1 — last updated March 2021 |
| Merging | Codename branch → `main` by pull request when the line goes stable | PR #30 (juneberry), PR #34 (kiwiberry) |
| Pre-release tags | Cut from the codename branch, not from `main` | all `v5.0.0-*` tags are on `lingonberry` |

Pre-release counters are zero-padded to two digits. Keep doing that: `beta.03` and `beta.3` are
different strings to Gradle and to JitPack, and mixing them makes ordering unpredictable.

Counter gaps in the tag list (alpha.02 → .04 → .10) are harmless; a number that failed to build
on JitPack was simply skipped.

### 5.2 Places that carry a version

There is **one** source of truth for the library version, but several other places repeat a
version or codename and are not updated automatically:

| Location | What it holds | Updated by |
|---|---|---|
| [gradle.properties](gradle.properties) `aeapps.version` | **Library version — the real one** | Hand |
| Git tag | Must equal `v` + `aeapps.version` | Hand |
| [VersionHistory.md](VersionHistory.md) | Changelog entry and month | Hand |
| [README.md](README.md) | Codename line, "commits since" badge tag, migration table | Hand |
| [sample/build.gradle.kts](sample/build.gradle.kts) | Sample `versionCode` / `versionName` (currently 12 / 1.4.4) — independent of the library version | Hand |
| `sample/src/main/res/values/strings.xml` `app_version` | Sample "about" text — currently says 1.4.0, out of step with `versionName` | Hand |
| `docs/release_notes.html` | HTML changelog on the Pages site | Generated by the Pages workflow |
| [docs/index.html](docs/index.html) | Version in the dependency snippet | Set by the Pages workflow |
| GitHub wiki (Distribution, Migration Guide, Codenames) | Usage instructions | Hand, outside the repo |

### 5.3 Release checklist

Steps 1–6 happen on the codename branch.

1. **Confirm the branch is green.** CI passing on the latest commit, or locally:
   `testDebugUnitTest`, `lintDebug`, `assembleRelease`.

2. **Bump `aeapps.version`** in `gradle.properties`.

3. **Dry-run the publication locally** and look at what was produced:

   ```bash
   ./gradlew publishReleasePublicationToMavenLocal
   ```

   Check `~/.m2/repository/com/github/midhunhk/lib-aeapps/` for ten artifact directories, each
   containing the new version with an `.aar`, a `.pom` and a `.module` file. Open one POM and
   confirm the dependencies look right. This is the closest local equivalent to what JitPack
   will build.

4. **Smoke-test as a consumer** (recommended for betas, required for RC and stable). Point the
   sample, or one of the real apps, at the Maven Local artifacts instead of project dependencies
   (add `mavenLocal()` to its repositories) and build it. Project dependencies hide packaging
   mistakes that only show up through the published POM.

5. **Update the changelog** in `VersionHistory.md`. For a stable release also update the README
   codename/badges, and the sample's version if the sample is being re-released. The docs site
   needs nothing here: it is rebuilt from the tag in step 7 (section 3.5).

6. **Commit** the version bump and changelog together, and push the branch. Wait for CI.

7. **Tag and push the tag:**

   ```bash
   git tag v<version>
   ```

   ```bash
   git push origin v<version>
   ```

8. **Trigger and check the JitPack build.** Open the repository's page on jitpack.io, find the
   new tag, press "Get it", and read the build log to the end. Confirm all ten artifacts are
   listed. A red log here means nobody can resolve the release.
   Also check that the "Docs site" workflow run for the tag succeeded; it publishes the API
   reference and release notes for the new version.

9. **Create the GitHub release** from the tag with the changelog entry as notes. Mark alphas,
   betas and RCs as pre-release.

Additional steps for a **stable** release (`x.y.0` with no suffix):

10. Open a pull request from the codename branch into `main` and merge it. For 5.0 this is a
    fast-forward: `main` has no commits that `lingonberry` lacks.
11. If the stable tag was cut on the codename branch before merging, make sure the same commit
    is what lands on `main` — do not squash, or the tag will point at a commit that is not on
    `main`.
12. Update the wiki pages (Distribution, Migration Guide, Codenames).
13. Move the dependent apps (Message Counter, Random Contact, Trip 'O Meter) to the new version.
14. Start the next line: create the next codename branch, set `aeapps.version` to the next
    `-alpha.01`, and change both `target-branch` entries in `.github/dependabot.yml` to the new
    branch (this only takes effect once merged into `main`).

### 5.4 Pre-release stages

| Stage | Meaning in this project | API changes allowed |
|---|---|---|
| `alpha.NN` | Build structure or API still moving | Yes |
| `beta.NN` | Feature-complete for the line; fixing bugs and packaging | Agreed for 5.0: yes, until RC |
| `rc.NN` | Believed releasable; only fixes for problems found in the RC | No |
| (none) | Stable | Only in the next major |

### 5.5 Things that have gone wrong before

- **Artifact ID typo** (`sms_api` instead of `sms-api`) shipped in beta.01 and needed beta.02 the
  same day. Step 3 catches this — look at the directory names under `~/.m2`.
- **Version left behind.** Commits after a tag keep the old `aeapps.version`, so a
  locally published build is indistinguishable from the tagged one. Bump to the next counter
  soon after tagging if you publish locally during development.
- **Changelog ahead of reality.** `VersionHistory.md` has listed "5.0.0 [Nov 2024]" since the
  first beta. Write pre-release entries under their own heading and add the stable heading only
  when the stable tag exists.
- **Renamed repository.** The project used to be `ae-apps-library`; some badges and links still
  point there. GitHub redirects, JitPack badges do not.

---

## 6. Adding or removing a module

To add `:new-module`:

1. Create `modules/new-module/` with `consumer-rules.pro`, `proguard-rules.pro` and
   `src/main/AndroidManifest.xml`, and a `build.gradle.kts` that applies
   `alias(libs.plugins.aeapps.android.library)` and sets a unique `namespace` (section 2.3).
   The artifact ID is the project name, so choose it with care.
2. Add the `include` and `projectDir` lines to `settings.gradle.kts`.
3. Add it to the `dokka`/`kover` list in the root `build.gradle.kts`, so it appears in the API
   reference and the coverage report, and run `./gradlew apiDump` to create its API dump.
4. Add `api(project(":new-module"))` to `modules/aeapps/build.gradle.kts` if it belongs in the
   umbrella.
5. Add any new dependency to `gradle/libs.versions.toml` first.
6. Add it to the module tables in the README and in this file.

Removing or renaming a published module is a breaking change for consumers, since the artifact
coordinate disappears — do it only at a major version and call it out in the migration guide.

---

## 7. Known gaps

Tracked in detail in [RELEASE-PLAN-v5.md](guides/RELEASE-PLAN-v5.md); listed here so this guide is not read
as a description of a finished setup.

- Release steps are entirely manual; nothing checks that the tag matches `aeapps.version`.
- No sources/Javadoc jars and no POM metadata in the publications.
- `android.newDsl=false` and `android.builtInKotlin=false` in `gradle.properties` keep AGP's
  old DSL, so every `android { }` block reports a deprecation warning. Moving to the new DSL
  means switching to AGP's built-in Kotlin support (and checking Dokka and Kover with it); the
  convention plugin already uses the new DSL types.
- The published site is rebuilt only for release tags or a manual run, so it shows the latest
  release, not unreleased work on `main`. The wiki is maintained by hand and is behind the code.
- The CI workflow has been added but had not yet run on GitHub at the time of writing.
