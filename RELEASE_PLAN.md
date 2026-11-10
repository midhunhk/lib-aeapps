# lib-aeapps 5.0 — Analysis and Release Plan

Status: **draft for review** · Written 2026-11-10 · Branch `lingonberry` at `c2bb953` (one commit after tag `v5.0.0-beta.03`)

This page records how the project was analysed, what was found, and a proposed plan split into
**release blockers**, **should-fix before 5.0.0**, and **nice-to-have** work. Nothing in the
codebase was changed while producing it.

---

## 1. Summary

The library is close to a 5.0.0 release structurally: the build is on current tooling
(AGP 8.11.1, Kotlin 2.2.0, Gradle 8.13, compileSdk 36, JDK 17), every module publishes through
`maven-publish` for JitPack, and Play Billing is already on v8.

What stands between `5.0.0-beta.03` and a stable release is mostly **packaging and correctness**,
not features:

1. **Published POMs hide dependencies consumers need to compile.** Public classes extend or
   expose AppCompat, RecyclerView, Fragment and Play Billing types, but those libraries are
   declared `implementation`, so they are published as runtime-only.
2. **The unit test suite is red.** 1 of 47 tests fails, and 15 of the 46 passing tests are empty
   stubs. There is no CI, so nothing catches this.
3. **A handful of real bugs in public API** — the most visible being
   `CommonUtils.isPackageInstalled`, which ignores its argument and always returns `true`.
4. **Documentation points at the wrong places** — old repository name, `master` branch,
   v4.1.0 badges, and a committed Javadoc site that describes classes which no longer exist.

Everything else (version catalog, convention plugins, Kotlin migration, modern Activity Result
and coroutine APIs, Maven Central) is improvement work that can follow 5.0.0.

---

## 2. How the analysis was done

| Step | What was looked at | Result |
|---|---|---|
| 1 | `settings.gradle.kts`, root build script, `buildSrc`, `gradle.properties`, wrapper, `jitpack.yml` | Module list, tool versions, publish coordinates |
| 2 | All 10 module build scripts + `sample` | Dependency graph, `api` vs `implementation` scopes, duplication |
| 3 | Manifests, ProGuard/consumer rules | All library manifests empty; consumer rules empty |
| 4 | Every main source file in the 9 code modules (~4,900 lines) | Per-module findings in section 5 |
| 5 | Test sources and `./gradlew testDebugUnitTest --continue` | 47 tests, 1 failing — section 4 |
| 6 | README, `VersionHistory.md`, `.github/`, `docs/` site, sample README | Stale links and docs — section 6 |
| 7 | Git history, tags, branches | 5.0 line started Aug 2023; 3 betas; commits since Nov 2024 are almost all AGP bumps |

Not covered: Android Lint and instrumented tests were not run, the sample app was not launched
on a device, and open GitHub issues were not read (the `gh` CLI is not installed on this machine).
"Latest version" claims for third-party libraries below are flagged **verify** where they come
from memory rather than a lookup.

---

## 3. Module structure

### 3.1 Modules

| Module | Artifact | Language | Main LOC | Test LOC | Purpose |
|---|---|---|---:|---:|---|
| `core` | `core` | Java | 960 | 0 | Models (`ContactInfo`, `MessageInfo`, `PhoneNumberInfo`), custom views, `ToolBarBaseActivity`, in-app review, shared resources |
| `utilities` | `utilities` | Java | 747 | 141 | Static helpers: intents, dialogs, phone/network, package checks |
| `contacts-api` | `contacts-api` | Java | 853 | 108 | `ContactsApiGateway` over `ContactsContract` |
| `sms-api` | `sms-api` | Java | 376 | 435 | `SmsApiGateway` over `content://sms` |
| `runtime-permissions` | `runtime-permissions` | Java | 531 | 120 | Permission-aware base Activity/Fragment and checker |
| `multi-contact` | `multi-contact` | Java | 532 | 0 | Multi-select contact picker base Activity |
| `database-helpers` | `database-helpers` | Java + Kotlin | 321 | 0 | `SQLiteOpenHelper` boilerplate (one class already deprecated) |
| `billing-client` | `billing-client` | Kotlin | 441 | 70 | `AeBillingClient` wrapper over Play Billing 8 |
| `mocks` | `mocks` | Kotlin | 120 | 125 | Fake contact / SMS data for demos and tests |
| `aeapps` | `aeapps` | — | 0 | 0 | Umbrella artifact that re-exports all of the above |
| `sample` | (app) | Java | 691 | 0 | Demo app for contacts, multi-contact, SMS |

Group: `com.github.midhunhk.lib-aeapps` · Version: `5.0.0-beta.03`
([Publish.kt](buildSrc/src/main/java/com/ae/apps/lib/Publish.kt))

### 3.2 Dependency graph

```
core  ◄── mocks
  ▲  ◄── database-helpers
  ├──── sms-api
  ├──── contacts-api ──────► utilities
  ├──── multi-contact ─────► utilities
  └──── runtime-permissions ► utilities

billing-client   (standalone)
utilities        (standalone)

aeapps ──► all nine modules (api)
sample ──► core, contacts-api, sms-api, runtime-permissions, utilities, multi-contact
```

Observations on the shape:

- The graph is shallow and acyclic in `main` scope, which is good. `core` declares
  `testImplementation(project(":mocks"))` while `mocks` depends on `core` — legal, but `core` has
  no tests, so the line is dead.
- `core` is a grab-bag: data models, UI widgets, a base Activity and the Play in-app review
  wrapper all ship together. Anyone who wants only `ContactInfo` (for example `sms-api`) also
  gets `play:review`, `preference` and `recyclerview` transitively.
- `aeapps` re-exports `mocks`, so an app using the umbrella artifact ships mock names in three
  locales to production.
- `runtime-permissions` depends on `utilities` for a single six-line method
  (`CommonUtils.createParentLayout`).

### 3.3 Build configuration

| Item | Current | Note |
|---|---|---|
| AGP | 8.11.1 | Current as of the last update (Jul 2025); newer releases exist — **verify** |
| Kotlin | 2.2.0 | Same |
| Gradle wrapper | 8.13 | Minimum for AGP 8.11 |
| compileSdk / minSdk | 36 / 21 | See decision D1 on minSdk |
| Java target | 17 | Fine for AGP 8+ consumers |
| Dependency management | Constants in [buildSrc/.../Dependencies.kt](buildSrc/src/main/java/com/ae/apps/lib/Dependencies.kt) | No version catalog; any edit invalidates the whole build |
| Module build scripts | ~60 near-identical lines × 10 | Publishing, SDK and compile options are copy-pasted |
| CI | None | Travis removed Aug 2023; no `.github/workflows`; README still shows a JitCI badge |
| Static analysis | None configured | No lint baseline, detekt, ktlint, or API-compatibility check |

---

## 4. Test baseline

Command: `./gradlew testDebugUnitTest --continue` → **BUILD FAILED** (3m 14s, cold daemon).

| Module | Tests | Result | Comment |
|---|---:|---|---|
| `billing-client` | 1 | pass | A second test has its `@Test` removed ("ProductDetails cannot be mocked"); setup uses reflection on private fields |
| `contacts-api` | 15 | pass | **All 15 are empty `// TODO implement test` bodies** |
| `mocks` | 5 | **1 fail** | See below |
| `runtime-permissions` | 5 | pass | |
| `sms-api` | 13 | pass | Best-covered module |
| `utilities` | 8 | pass | |
| `core`, `multi-contact`, `database-helpers`, `aeapps`, `sample` | 0 | — | No tests |

**The failure:**
[MockContactDataUtilsTest.kt](modules/mocks/src/test/kotlin/com/ae/apps/lib/mock/MockContactDataUtilsTest.kt)
uses `@Mock lateinit var` fields but the class has no `@ExtendWith(MockitoExtension::class)`, so
the mocks are never created and `setUp` throws `UninitializedPropertyAccessException`. This is a
test bug, not a library bug.

Other test-infrastructure notes:

- Three frameworks are in play: JUnit 4 (most modules), JUnit 5 (`billing-client`, `mocks`),
  and TestNG 6.9.6 (declared in `aeapps`, which has no tests).
- `mockito-kotlin` 3.2.0 is paired with `mockito-core` 5.18.0; these are several major versions
  apart.
- Robolectric is 4.13, which predates SDK 36 support — **verify** the minimum version that
  supports compileSdk 36 before adding Robolectric tests.
- Every module declares `androidTestImplementation` dependencies, but there is no
  `src/androidTest` directory anywhere and the instrumentation runner line is commented out.

---

## 5. Findings by area

Severity: **A** = fix before 5.0.0 · **B** = should fix before 5.0.0 · **C** = nice to have.

### 5.1 Publishing and packaging

| # | Sev | Finding |
|---|---|---|
| P1 | A | **Leaky `implementation` scopes.** Public API exposes types from libraries published as runtime-only: `core` (`ToolBarBaseActivity extends AppCompatActivity`, `EmptyRecyclerView extends RecyclerView`), `runtime-permissions` (AppCompat, Fragment), `multi-contact` (AppCompat, Fragment, RecyclerView), `billing-client` (`ProductDetails`, `Purchase`, `AppCompatActivity`, `DefaultLifecycleObserver` in public signatures). Consumers only compile today because they happen to declare the same libraries themselves. These should be `api`. |
| P2 | A | **No sources jar.** `singleVariant("release") { }` is empty in every module, so consumers get no source attachment or KDoc/Javadoc in the IDE. Add `withSourcesJar()` (and optionally `withJavadocJar()`). |
| P3 | B | **No POM metadata** (name, description, URL, licence, SCM, developer). Not required by JitPack, but required for Maven Central and useful regardless. |
| P4 | B | `aarMetadata { minCompileSdk = MIN_SDK_VERSION }` sets the consumer's minimum *compileSdk* to 21, which is the minSdk value used in the wrong field. Either set it to what the library genuinely needs or remove it. |
| P5 | B | Unused dependency: Guava 31.0.1 in `billing-client` — no source file imports it. It adds a large transitive dependency for nothing. |
| P6 | B | `aeapps` umbrella exposes `mocks` as `api`. Decide whether mocks belong in a production umbrella (decision D3). |
| P7 | C | `proguard-rules.pro` in each module holds a generic "keep everything public" template, but `isMinifyEnabled = false`, so it never runs. `consumer-rules.pro` files are empty, which is correct for this code (no reflection). The unused rule files can be deleted. |
| P8 | C | Stray file [modules/build.gradle](modules/build.gradle) (Groovy, namespace typo `lib-aeappps`) is not part of any project. `AeAppsLib.iml` is tracked despite `*.iml` being ignored. |
| P9 | C | `.gitignore` ignores `gradle/` (the wrapper is tracked only because it was force-added) and lists `.gitignore` itself. **This will silently hide a new `gradle/libs.versions.toml`** — fix before starting the version-catalog work. |

### 5.2 Correctness bugs in library code

| # | Sev | Module | Finding |
|---|---|---|---|
| B1 | A | `utilities` | [CommonUtils.isPackageInstalled](modules/utilities/src/main/java/com/ae/apps/lib/common/utils/CommonUtils.java) looks up `context.getPackageName()` instead of the `packageName` argument, so it **always returns true**. There are also three overlapping variants (`isPackageInstalled`, `checkIfPackageIsInstalled`, `isPackageInstalled2`); on Android 11+ all of them need a `<queries>` declaration in the consumer manifest, which is undocumented. |
| B2 | A | `contacts-api` | `ContactsApiGatewayImpl.getContactInfo(id, options)` dereferences the lookup result without a null check — an unknown ID throws `NullPointerException`. `getContactInfo(id)` also does not check that the gateway is initialised. |
| B3 | A | `contacts-api` | Cursor leaks: `readContacts` never closes its cursor; `getContactIdFromRawContact` only closes it when a row is found. `readContacts` also queries with a `null` projection (all columns) when it needs three. |
| B4 | A | `sms-api` | `SmsApiGatewayImpl` closes its cursor only when `moveToFirst()` succeeds — empty result sets leak. |
| B5 | A | `sms-api` | `SMS_TABLE_PROJECTION` omits `date` and `type`, so `MessageInfo.getDate()` is always `0` and `getType()` always `null` for real data. `protocol` is in the projection but never read. |
| B6 | B | `contacts-api` | `initializeAsync` uses the deprecated no-arg `new Handler()`, which throws if called from a thread without a Looper, and spawns a raw `Thread` per call with no cancellation or error path. |
| B7 | B | `multi-contact` | `MultiContactRecyclerViewAdapter.performFiltering` reassigns `filteredContacts` on the filter worker thread while the main thread reads it in `onBindViewHolder`/`getItemCount` — a race that can produce `IndexOutOfBoundsException`. Also, if the consumer's layout lacks `R.id.list`, `adapter` stays null and the search listener crashes. |
| B8 | B | `runtime-permissions` | `PermissionCheckingFragment.onRequestPermissionsResult` checks only `grantResults[0]`, so with several permissions a partial grant is treated as full. `RuntimePermissionChecker` requires the component to be a `Context`, so despite the Javadoc a Fragment cannot use it. |
| B9 | B | `database-helpers` | `CopiedDataBaseHelper` keeps `databasePath` in a **companion object**, so two helpers for two databases overwrite each other's path. Streams are not closed on failure, and it throws `java.lang.Error`. The "open readable DB then overwrite the file" technique is also known to misbehave with WAL on Android 9+. |
| B10 | B | `core` | `AppReviewImpl` has a private constructor behind a singleton, so its `protected` extension points (`getReviewRequestThreshold`, `conditionsMetForShowingReview`) can never be overridden. The show-condition logic (`timesPrompted < threshold || timesPrompted % threshold == 0`) is hard to reason about and untested. |
| B11 | B | `core` | `ToolBarBaseActivity.displayHomeAsUp()` dereferences `getSupportActionBar()` without a null check, while the neighbouring `setToolbarTitle` does check. |
| B12 | C | `core` | `ContactInfo.equals` just calls `super.equals` while `compareTo` orders by ID — inconsistent with `Comparable`'s contract. `contacts.set(index, sameObject)` in `getRandomContact` is a no-op. |
| B13 | C | `utilities` | `MobileNetworkUtils` swallows every exception silently; `IntentUtils.getUriIntent` builds an Intent with no action; `EmailIntentBuilder` calls `setType` then `setData`, and `setData` clears the type. `ContactUtils` shows hard-coded English toasts. |
| B14 | C | `billing-client` | Setup failure is never reported to the handler (the callback is commented out); there is no reconnect after `onBillingServiceDisconnected`; only `INAPP` products can be queried or launched, although `queryPurchasesAsync` also reports `SUBS`. Products Play could not fetch are dropped silently. |

### 5.3 Platform currency

| # | Sev | Finding |
|---|---|---|
| C1 | B | **Edge-to-edge.** Apps targeting SDK 35+ draw edge-to-edge, and the opt-out is gone at 36. `ToolBarBaseActivity` and `MultiContactBaseActivity` set content views without handling window insets, so consumers targeting 35/36 will get toolbars under the status bar. Needs either inset handling in the base classes or clear documentation. |
| C2 | B | **Deprecated result APIs.** `PermissionCheckingActivity`, `AbstractPermissionsAwareActivity` and `PermissionCheckingFragment` use `requestPermissions` / `onRequestPermissionsResult`; `MultiContactBaseActivity` documents `startActivityForResult` and the sample uses it. `ContactPickerFragment` already uses `registerForActivityResult`, so the pattern exists in the codebase. |
| C3 | B | `SDK_INT >= M` branches and `@TargetApi(M)` are scattered through `runtime-permissions`. They are only meaningful if minSdk stays at 21 (decision D1). |
| C4 | B | Dependency versions in `Dependencies.kt` were last refreshed in July 2025 (Material is older, at 1.11.0). A refresh pass is due before release — **verify** current versions rather than trusting this document. |
| C5 | C | `sms-api` filters by the `person` column, which most modern devices leave unpopulated; matching by `address` is the dependable route. `READ_SMS` is also restricted on Google Play, which the sample README notes but the library docs do not. |
| C6 | C | `core` pulls in `androidx.preference` solely for `PreferenceManager.getDefaultSharedPreferences`; a named `SharedPreferences` file would remove the dependency. |

### 5.4 Build system

| # | Sev | Finding |
|---|---|---|
| G1 | B | Ten module scripts repeat the same `android {}`, `publishing {}` and `afterEvaluate` blocks. One convention plugin would reduce each module to its namespace, artifact ID and dependencies, and make P1–P4 one-place fixes. |
| G2 | C | `buildSrc` constants instead of a `libs.versions.toml` catalog — no Dependabot/Renovate support and full-build invalidation on every version bump. |
| G3 | C | Inconsistencies: `core` applies the Kotlin plugin but has no Kotlin; Kotlin modules call `jvmToolchain` twice; `sample` hard-codes SDK levels instead of using `ConfigurationData`, uses `api` in an application module, and has an empty nested `android { android { } }` block plus a commented Groovy snippet referencing 4.1.0-beta.05. |

### 5.5 API design

| # | Sev | Finding |
|---|---|---|
| A1 | B | No explicit boundary between public API and internals. `ContactsApiUtils`, `SmsApiUtils` and `DefaultPictureCache` are documented as internal but are `public`. Once 5.0.0 ships these are frozen. |
| A2 | C | Constants are exposed through interfaces (`ContactsApiConstants`, `SmsApiConstants`, `MultiContactPickerConstants`). |
| A3 | C | Models are mutable Java beans with no nullability annotations, so Kotlin callers see platform types everywhere. |
| A4 | C | `multi-contact` passes selections as a comma-joined string extra rather than a typed `ActivityResultContract`. |
| A5 | C | Callback-only async (`ContactsDataConsumer.onContactsRead()` carries no data and no error). |

---

## 6. Documentation findings

| # | Sev | Finding |
|---|---|---|
| D-1 | A | [README.md](README.md): JitPack badge points at the old repo name `ae-apps-library`; "commits since" badge is pinned to `v4.1.0`; Changelog link uses the `master` branch (default is `main`); JitCI badge is for a service no longer in use. No Gradle snippet showing how to add the dependency — it defers entirely to the wiki. |
| D-2 | A | [VersionHistory.md](VersionHistory.md) lists "Version 5.0.0 [Nov 2024]" although only betas have shipped, and omits the Billing v8 migration, compileSdk 36 and the July 2025 changes. |
| D-3 | B | No v4 → v5 migration guide (the README table stops at v4.1). Breaking changes include the billing handler interface (SKU → ProductDetails), JDK 17 and coordinate changes. |
| D-4 | B | `docs/api/` is a committed Javadoc dump (~220 files) from an older version: it documents `SMSManager`, `ContactManager`, `AeContactService` and `com.ae.apps.libs.*`, none of which exist now. |
| D-5 | C | [.github/CONTRIBUTING.md](.github/CONTRIBUTING.md) links to `ae-apps-library`. [sample/Readme.md](sample/Readme.md) has an empty "Features Provided" heading. Licence headers vary by year and are missing from some files. |

---

## 7. Decisions needed from you

These change the shape of the plan, so they are called out rather than assumed.

| ID | Question | Recommendation |
|---|---|---|
| **D1** | Keep minSdk 21 or raise to 23? | **Decided 2026-11-10: raise to 23.** Deletes every pre-M permission branch (Phase 2). |
| **D2** | Is 5.0.0 allowed to break API relative to beta.03? | **Decided 2026-11-10: yes.** Fix scopes, hide internals and remove dead variants now rather than carrying them through 5.x. |
| **D3** | Should `mocks` stay in the `aeapps` umbrella? | **Decided 2026-11-10: remove it.** Apps add `mocks` as `debugImplementation`/`testImplementation` (Phase 2). |
| **D4** | JitPack only, or Maven Central as well? | **JitPack for 5.0.0**; treat Central as post-release (needs a verified namespace, signing, and the POM metadata from P3). |
| **D5** | What happens to `database-helpers`? | **Ship as-is but deprecate the whole module** in 5.0 with a pointer to Room, fix only B9, and remove it in 6.0. |
| **D6** | Do Message Counter, Random Contact and Trip 'O Meter all move to 5.0? | Their usage determines which "internal" classes can safely be hidden under A1 — worth a quick grep in each app before Phase 2. |

---

## 8. Plan

Phases are ordered so that each one leaves the branch releasable. Effort is a rough guide for
one person: **S** ≈ under an hour, **M** ≈ half a day, **L** ≈ a day or more.

### Phase 0 — Get to green (do first)

| Task | Refs | Effort |
|---|---|---|
| Fix `MockContactDataUtilsTest` (add `MockitoExtension`, or build the mocks by hand) | §4 | S |
| Replace the 15 stub tests in `ContactsApiUtilsTest` with real ones using `MatrixCursor` under Robolectric, or delete them | §4 | M |
| Add a GitHub Actions workflow: `assembleRelease`, `testDebugUnitTest`, `lint`, and `publishToMavenLocal` on push and PR | §3.3 | S |
| Fix `.gitignore` (`gradle/` entry, self-reference); delete `modules/build.gradle` and `AeAppsLib.iml` | P8, P9 | S |

Exit: CI is green on `lingonberry`.

**Status 2026-11-10: done locally, not yet committed or run on GitHub.**
`testDebugUnitTest`, `lintDebug`, `assembleRelease` and `publishReleasePublicationToMavenLocal`
all pass on this machine (47 tests, 0 failures; lint 0 errors).

- `MockContactDataUtilsTest` fixed with `MockitoExtension`.
- `ContactsApiUtilsTest` rewritten: 15 real tests against a mocked `Cursor`.
- Added `.github/workflows/ci.yml`. It runs `chmod +x ./gradlew` because `gradlew` is committed
  without the executable bit.
- `.gitignore` no longer ignores `gradle/` or itself, and ignores all of `.kotlin/`. This
  exposed a previously hidden, untracked `modules/aeapps/.gitignore` (contains `/build`).
- Deleted `modules/build.gradle` and `AeAppsLib.iml`.
- Lint found an extra crash bug, fixed here because it blocked a green build:
  `MultiContactRecyclerViewAdapter` called `CharSequence.isEmpty()`, which only exists from
  API 35, so typing in the picker's search box would crash on anything older. Replaced with
  `length() == 0`.
- Remaining lint warnings (not errors): `core` 6, `multi-contact` 2, `runtime-permissions` 1,
  `sample` 31.

### Phase 1 — Release blockers

| Task | Refs | Effort |
|---|---|---|
| Change exposed dependencies from `implementation` to `api` in `core`, `runtime-permissions`, `multi-contact`, `billing-client`; drop Guava | P1, P5 | S |
| Add `withSourcesJar()` to every publication | P2 | S |
| Fix `isPackageInstalled`; collapse the three variants into one and deprecate the others; document `<queries>` | B1 | S |
| Null-safety and cursor handling in `contacts-api` (try-with-resources, explicit projection, not-found path) | B2, B3 | M |
| Cursor handling and projection in `sms-api`; add tests asserting `date`/`type` are populated | B4, B5 | S |
| README: correct badges and links, add a dependency snippet and a module table | D-1 | S |
| `VersionHistory.md`: real 5.0.0 entry | D-2 | S |
| Verify the published artifacts by pointing the sample (or one of the three apps) at `mavenLocal` coordinates instead of project dependencies | P1 | M |

Exit: a consumer with *only* the lib-aeapps dependencies declared can compile against each module.

**Status 2026-11-10: code and packaging items done locally (second analysis pass); not yet
committed.** Full build passes: 57 unit tests, lint 0 errors, release build, local publish.
POMs checked in Maven Local — exposed libraries are now `compile` scope and each module has a
`-sources.jar`.

Fixed from the findings tables: P1, P2, P5, B1 (the always-true bug only), B2, B3, B4, B5, B6,
B7, B8 (partial grant only), B9, B11, and the README/CONTRIBUTING links and changelog heading
from D-1/D-2.

New in the second pass, also fixed:

- `SimpleGraphView` never reset its running angle, so the pie was drawn wrongly on every redraw
  after the first, and it crashed when created through its XML constructors.
- `RoundedImageView` cast every drawable to `BitmapDrawable` and crashed on vectors.
- `getContactPicture` never closed its input stream.
- `IntentUtils.getUriIntent` built an Intent with no action.

Found in the second pass, **not** fixed (need a decision or are larger than a bug fix):

- `SimpleGraphView.drawLabels` advances `x` by the line height instead of `y`, so legend labels
  overlap. The intended layout is unclear from the code.
- `RoundedImageView.onDraw` allocates three bitmaps on every draw pass.
- `SmsApiUtilsTest` has a hand-rolled `assertThrows` and an unchecked cast warning.

Still open in Phase 1: collapsing the three `isPackageInstalled` variants and documenting
`<queries>`; a README dependency snippet (blocked on confirming the JitPack version format);
and the consumer smoke test against Maven Local from a separate project.

### Phase 2 — Should-fix before 5.0.0

| Task | Refs | Effort |
|---|---|---|
| Apply decision D1 (minSdk) and remove dead SDK checks | C3 | S |
| Refresh dependency versions; align `mockito-kotlin` with `mockito-core`; drop TestNG and unused `androidTest` dependencies | C4, §4 | M |
| Edge-to-edge handling in `ToolBarBaseActivity` and `MultiContactBaseActivity`; verify in the sample at targetSdk 36 | C1 | M |
| Fix the adapter filter race and null-adapter crash in `multi-contact` | B7 | S |
| Fix partial-grant handling in `PermissionCheckingFragment` | B8 | S |
| `initializeAsync`: main-looper handler, executor, and an error callback | B6 | S |
| `CopiedDataBaseHelper`: instance-level path, `use {}` for streams, proper exception type | B9 | S |
| `AppReviewImpl`: make the threshold configurable, add unit tests for the show condition | B10 | M |
| `displayHomeAsUp` null check | B11 | S |
| Billing: report setup failure to the handler; enable or implement reconnection | B14 | M |
| Mark internals: move helper classes to an `internal` package or reduce visibility; apply D3 and D5 | A1, P6 | M |
| Fix `aarMetadata`; add POM name/description/licence/SCM | P3, P4 | S |
| Write the v4 → v5 migration guide; remove or regenerate `docs/api` | D-3, D-4 | M |
| Add tests for `core` (models, `EmptyRecyclerView`) and `multi-contact` (adapter filtering, result encoding) | §4 | M |

Then: tag `v5.0.0-rc.01`, migrate one real app to it, and release `v5.0.0` after a short soak.

### Phase 3 — Nice to have (5.1 and later)

**Build and tooling**

- Version catalog (`gradle/libs.versions.toml`) replacing `Dependencies.kt`, plus Renovate or Dependabot.
- A convention plugin (`build-logic`) for library modules, replacing `buildSrc` and the ten duplicated scripts (G1). If time allows this is worth pulling into Phase 1, because it turns P1–P4 into single-file changes.
- Binary compatibility validation (Kotlin BCV or Metalava) so API changes show up in review.
- Kover or JaCoCo coverage reporting; detekt/ktlint; a lint baseline with `warningsAsErrors`.
- Dokka-generated API docs published to GitHub Pages from CI, replacing the committed HTML.
- Release automation: a tag-triggered workflow that builds, creates the GitHub release and warms the JitPack build.
- Maven Central publishing (D4).

**API modernisation**

- Permissions: an `ActivityResultContracts.RequestMultiplePermissions`-based helper usable from any `ComponentActivity` or `Fragment`, with rationale support; deprecate the inheritance-based classes.
- Multi-contact: a typed `ActivityResultContract<List<String>, List<String>>`, `ListAdapter` with `DiffUtil`, and saved-state for selections across rotation.
- Contacts and SMS: `suspend`/`Flow` variants alongside the callback API, error reporting, and SMS lookup by address rather than `person`.
- Models: Kotlin data classes (or at least nullability annotations and proper `equals`/`hashCode`), `Parcelable` where useful.
- Billing: subscriptions and offer tokens, a sealed result type in place of the 12-method handler interface, a test fake.
- Split `core` into `models` and `ui` so data-only modules stop pulling UI and Play dependencies.
- Gradual Java → Kotlin migration, starting with `utilities` and the constants interfaces.

**Sample app**

- Demos for billing (with static test products), in-app review, permissions and database helpers.
- Move to Activity Result APIs and Material 3; add a smoke test so CI exercises the library through the app.
- Fill in the sample README.

---

## 9. Suggested order of the first few pull requests

1. **Green build**: failing test fix + CI workflow + repo hygiene (Phase 0).
2. **Packaging**: `api` scopes, sources jar, Guava removal, `mavenLocal` consumer check.
3. **Data-layer fixes**: contacts and SMS cursor/null/projection fixes with tests.
4. **Utilities fixes**: `isPackageInstalled` and friends.
5. **Docs**: README, version history, migration guide.

After those five, the library is a defensible `5.0.0-rc.01`; Phase 2 items can then be taken in
any order.
