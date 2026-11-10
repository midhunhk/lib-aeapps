# lib-aeapps — Project Structure and Release Guide

Written 2026-11-10 against `lingonberry` at `b6b14fd`.

This page describes how the repository is laid out, how the build is wired, and the steps that
are specific to this project when cutting a release. It describes the project **as it is today**;
planned changes live in [RELEASE_PLAN.md](RELEASE_PLAN.md). Where the two disagree, this page is
the "now" and the plan is the "next".

---

## 1. Repository layout

```
lib-aeapps/
├── settings.gradle.kts        Module list and directory mapping
├── build.gradle.kts           Plugin versions (AGP, Kotlin) — nothing else
├── gradle.properties          JVM args, AndroidX, non-transitive R, configuration cache
├── gradle/wrapper/            Gradle 8.13 wrapper
├── jitpack.yml                Tells JitPack to build with JDK 17
├── local_release.bat          Publishes all modules to ~/.m2 for local testing
│
├── buildSrc/                  Shared build constants (see section 3)
│   └── src/main/java/com/ae/apps/lib/
│       ├── ConfigurationData.kt   compileSdk, minSdk, Java version
│       ├── Dependencies.kt        Every third-party dependency coordinate
│       └── Publish.kt             Group ID, VERSION_STRING, artifact IDs
│
├── modules/                   The library — one directory per published artifact
│   ├── core/
│   ├── utilities/
│   ├── contacts-api/
│   ├── sms-api/
│   ├── runtime-permissions/
│   ├── multi-contact/
│   ├── database-helpers/
│   ├── billing-client/
│   ├── mocks/
│   └── aeapps/                Umbrella artifact, no code of its own
│
├── sample/                    Demo application (not published)
│
├── docs/                      GitHub Pages site (hand-maintained HTML)
│   ├── index.html             Landing page
│   ├── release_notes.html     HTML changelog — stops at 4.1.0
│   ├── api/                   Committed Javadoc from an older version
│   ├── assets/                CSS/JS for the site
│   └── resources/v4/          Screenshots and architecture notes
│
├── .github/
│   ├── workflows/ci.yml       Unit tests, lint, release build, local publish
│   ├── CONTRIBUTING.md, CODE_OF_CONDUCT.md, ISSUE_TEMPLATE/
│
├── README.md
├── VersionHistory.md          Markdown changelog (the one the README links to)
├── RELEASE_PLAN.md            5.0 analysis and plan
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
| `:database-helpers` | `database-helpers` | `com.ae.apps.lib.database_helpers` | Java + Kotlin | `DataBaseHelper` (deprecated), `CopiedDataBaseHelper` |
| `:billing-client` | `billing-client` | `com.ae.apps.lib.billing_client` | Kotlin | `AeBillingClient` over Play Billing 8 |
| `:mocks` | `mocks` | `com.ae.apps.lib.mocks` | Kotlin | Fake contacts and messages, mock names in en/es/fr |
| `:aeapps` | `aeapps` | `com.ae.apps.lib.aeapps` | — | `api` dependency on all nine modules above |
| `:sample` | not published | `com.ae.apps.lib.sample` | Java | Demo app |

Two naming details worth knowing:

- **Namespace ≠ Java package.** The Gradle `namespace` (which decides where `R` is generated)
  uses underscores, e.g. `com.ae.apps.lib.multi_contact`, while the source package is
  `com.ae.apps.lib.multicontact`. With `android.nonTransitiveRClass=true`, code must import the
  `R` of the module that owns the resource — for example `multi-contact` references
  `com.ae.apps.lib.multi_contact.R.id.list` explicitly.
- **Artifact IDs use hyphens** and are defined once in
  [Publish.kt](buildSrc/src/main/java/com/ae/apps/lib/Publish.kt). An underscore slipped into
  `sms_api` once and needed a follow-up release (beta.02) to correct.

### 2.2 Dependencies between modules

```
core  ◄── mocks
  ▲  ◄── database-helpers
  ├──── sms-api
  ├──── contacts-api ──────► utilities
  ├──── multi-contact ─────► utilities
  └──── runtime-permissions ► utilities

billing-client   (no module dependencies)
utilities        (no module dependencies)

aeapps ──► all nine (api)
sample ──► core, contacts-api, sms-api, runtime-permissions, utilities, multi-contact
```

All module-to-module edges are `api`, so they appear as compile-scope dependencies in the
published POMs and a consumer of `contacts-api` gets `core` and `utilities` automatically.
Third-party libraries are currently `implementation` — see P1 in the release plan.

### 2.3 Inside a module

Every library module has the same shape:

```
modules/<name>/
├── build.gradle.kts       ~60 lines, near-identical across modules
├── consumer-rules.pro     Empty (shipped to consumers; nothing needs keeping)
├── proguard-rules.pro     Generic template; unused because minify is off
└── src/
    ├── main/
    │   ├── AndroidManifest.xml    Empty <manifest/> — no permissions declared
    │   ├── java/…                 Java and Kotlin both live under java/
    │   └── res/
    └── test/
        ├── java/…                 JUnit 4 modules
        └── kotlin/…               JUnit 5 modules (billing-client, mocks)
```

Because the library manifests are empty, **the consuming app must declare permissions itself**
(`READ_CONTACTS`, `READ_SMS`, `CALL_PHONE`, and `<queries>` entries for package checks).

Test frameworks by module:

| Framework | Modules |
|---|---|
| JUnit 4 + Mockito | `contacts-api`, `sms-api`, `runtime-permissions`, `utilities` (plus Robolectric declared) |
| JUnit 5 + Mockito-Kotlin | `billing-client`, `mocks` (these set `useJUnitPlatform()`) |
| None | `core`, `multi-contact`, `database-helpers`, `aeapps`, `sample` |

There are no instrumented tests (`src/androidTest`) anywhere.

---

## 3. Build configuration

### 3.1 Where each setting lives

| To change… | Edit |
|---|---|
| AGP or Kotlin version | [build.gradle.kts](build.gradle.kts) (root `plugins` block) |
| Gradle version | `gradle/wrapper/gradle-wrapper.properties` (or `./gradlew wrapper --gradle-version …`) |
| compileSdk, minSdk, Java level | [ConfigurationData.kt](buildSrc/src/main/java/com/ae/apps/lib/ConfigurationData.kt) |
| A third-party dependency version | [Dependencies.kt](buildSrc/src/main/java/com/ae/apps/lib/Dependencies.kt) |
| Library version, group, artifact IDs | [Publish.kt](buildSrc/src/main/java/com/ae/apps/lib/Publish.kt) |
| JDK used by JitPack | [jitpack.yml](jitpack.yml) |
| Sample app SDK levels and version | [sample/build.gradle.kts](sample/build.gradle.kts) — hard-coded, **not** read from `ConfigurationData` |

Things that are easy to miss:

- `ConfigurationData.JAVA_LANG_VERSION` only feeds the Kotlin toolchain. The Java
  `compileOptions` in each module are hard-coded to `JavaVersion.VERSION_17`, so a Java-level
  change needs both.
- The sample's `compileSdk`/`minSdk`/`targetSdk` must be bumped by hand whenever
  `ConfigurationData` changes.
- Any edit under `buildSrc/` invalidates the whole build, so a version bump triggers a full
  recompile.
- `org.gradle.configuration-cache=true` is on. A new plugin or task that is not
  configuration-cache compatible will fail the build rather than warn.
- JDK 17 is required locally and is what CI and JitPack use.

### 3.2 Current tool versions

| Tool | Version |
|---|---|
| Gradle | 8.13 |
| Android Gradle Plugin | 8.11.1 |
| Kotlin | 2.2.0 |
| compileSdk | 36 |
| minSdk | 21 (decided: moving to 23 before 5.0.0) |
| Java source/target | 17 |

AGP and the Gradle wrapper are coupled — each AGP release has a minimum Gradle version, so
upgrade them together and check the AGP release notes for the pairing.

### 3.3 Everyday commands

```bash
./gradlew testDebugUnitTest --continue
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
publish to Maven Local, and uploads test and lint reports if anything fails. It does not publish
anywhere and does not create tags or releases.

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
component, with coordinates taken from `Publish.kt`:

```
groupId    = com.github.midhunhk.lib-aeapps
artifactId = core | utilities | contacts-api | sms-api | runtime-permissions |
             multi-contact | database-helpers | billing-client | mocks | aeapps
version    = Publish.VERSION_STRING
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

> **Verify before documenting publicly:** tags carry a `v` prefix (`v5.0.0-beta.03`) while
> `VERSION_STRING` does not (`5.0.0-beta.03`). The old commented snippet in the sample used the
> un-prefixed form. Confirm on the project's jitpack.io page which form JitPack actually resolves
> for this repository and write that exact string into the README.

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
| [Publish.kt](buildSrc/src/main/java/com/ae/apps/lib/Publish.kt) `VERSION_STRING` | **Library version — the real one** | Hand |
| Git tag | Must equal `v` + `VERSION_STRING` | Hand |
| [VersionHistory.md](VersionHistory.md) | Changelog entry and month | Hand |
| [README.md](README.md) | Codename line, "commits since" badge tag, migration table | Hand |
| [sample/build.gradle.kts](sample/build.gradle.kts) | Sample `versionCode` / `versionName` (currently 12 / 1.4.4) — independent of the library version | Hand |
| `sample/src/main/res/values/strings.xml` `app_version` | Sample "about" text — currently says 1.4.0, out of step with `versionName` | Hand |
| [docs/release_notes.html](docs/release_notes.html) | HTML changelog on the Pages site — stops at 4.1.0 | Hand |
| GitHub wiki (Distribution, Migration Guide, Codenames) | Usage instructions | Hand, outside the repo |

### 5.3 Release checklist

Steps 1–6 happen on the codename branch.

1. **Confirm the branch is green.** CI passing on the latest commit, or locally:
   `testDebugUnitTest`, `lintDebug`, `assembleRelease`.

2. **Bump `VERSION_STRING`** in `Publish.kt`. The tree is currently two commits past
   `v5.0.0-beta.03` but still says `5.0.0-beta.03`, so the next tag needs a bump first.

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
   codename/badges, `docs/release_notes.html`, and the sample's version if the sample is being
   re-released.

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
14. Start the next line: create the next codename branch and set `VERSION_STRING` to the next
    `-alpha.01`.

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
- **Version constant left behind.** Commits after a tag keep the old `VERSION_STRING`, so a
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

1. Create `modules/new-module/` with `build.gradle.kts`, `consumer-rules.pro` and
   `src/main/AndroidManifest.xml` — copying an existing module of the same language is quickest.
2. Set a unique `namespace` in its build script.
3. Add an artifact ID constant to `ArtifactId` in `Publish.kt` and use it in the publication.
4. Add the `include` and `projectDir` lines to `settings.gradle.kts`.
5. Add `api(project(":new-module"))` to `modules/aeapps/build.gradle.kts` if it belongs in the
   umbrella.
6. Add it to the module tables in the README and in this file.

Removing or renaming a published module is a breaking change for consumers, since the artifact
coordinate disappears — do it only at a major version and call it out in the migration guide.

---

## 7. Known gaps

Tracked in detail in [RELEASE_PLAN.md](RELEASE_PLAN.md); listed here so this guide is not read
as a description of a finished setup.

- Release steps are entirely manual; nothing checks that the tag matches `VERSION_STRING`.
- No sources/Javadoc jars and no POM metadata in the publications.
- Build logic is duplicated across ten module scripts; dependency versions live in `buildSrc`
  rather than a version catalog.
- The Pages site (`docs/`) and the wiki are maintained by hand and are behind the code.
- The CI workflow has been added but had not yet run on GitHub at the time of writing.
