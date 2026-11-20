# Draft GitHub issues for the 6.0 release plan

Not created yet. Review, edit freely, then they can be created in one go.

Format, so the file can be turned into issues automatically: each issue starts with a `## N.`
heading (the issue title follows the number), then a `Labels:` line, then the body until the next
`## ` heading. References like S1, A3, D8 point to [RELEASE-PLAN-v6.md](guides/RELEASE-PLAN-v6.md).

Setup to create first:

- **Milestone:** `6.0.0` (rename to the 6.0 codename once chosen)
- **New labels:** `security` (red), `breaking change` (orange), `build` (grey)
- **Existing label used:** `enhancement`

The tracking issue (27) is created last, so it can link to the real issue numbers.

---

## 1. Build: version catalog and build-logic convention plugin

Labels: enhancement, build

**Phase 0 — Foundations** · Effort: L · Refs: Q1

### Background
The nine library modules repeat about 60 lines of identical build configuration each (Android
defaults, publishing, compile options), and dependency coordinates live as constants in
`buildSrc`. Every cross-cutting change touches nine files, and stale entries such as the unused
Guava constant survive unnoticed.

### Change
- Add `gradle/libs.versions.toml` and move every dependency and plugin version into it.
- Add a `build-logic` included build with a convention plugin for library modules (SDK levels,
  Java/Kotlin toolchain, publishing, Dokka, test setup).
- Reduce each module script to its namespace, artifact ID and dependencies.
- Remove `buildSrc` once nothing uses it.

### Done when
- [ ] No dependency coordinate or version is declared outside the catalog
- [ ] Module build scripts contain no duplicated `android {}` or publishing blocks
- [ ] Published POMs are unchanged apart from intended differences (compare against 5.1.0 in Maven Local)
- [ ] CI is green

---

## 2. Build: binary-compatibility validation and Kotlin explicit API mode

Labels: enhancement, build

**Phase 0 — Foundations** · Effort: M · Refs: Q2, A5

### Background
Nothing detects accidental changes to the public API, and helper classes documented as internal
(`*Utils`, `*Constants`, picture caches) are public. Once 6.0 is released, whatever is public is a
contract.

### Change
- Add the Kotlin binary-compatibility validator (or Metalava) and commit API dumps per module.
- Run the API check in CI so a pull request fails if it changes the public API without updating
  the dump.
- Enable Kotlin explicit API mode in library modules as they move to Kotlin.

### Done when
- [ ] Every library module has a committed API dump
- [ ] CI fails on an unintended API change
- [ ] Explicit API mode is on for all Kotlin library modules

---

## 3. CI: pin GitHub Actions to commit SHAs and automate dependency updates

Labels: enhancement, build, security

**Phase 0 — Foundations** · Effort: S · Refs: S8

### Background
Workflows reference actions by moving major tags (`@v5`), so a compromised or changed tag would run
in CI with the repository's token. Dependency and action updates are done by hand.

### Change
- Pin every action in `ci.yml` and `pages.yml` to a full commit SHA, with the version as a comment.
- Add Dependabot (or Renovate) for Gradle dependencies and GitHub Actions, grouped to keep pull
  requests few.

### Done when
- [ ] No workflow uses a tag reference for an action
- [ ] Update pull requests are opened automatically for Gradle and Actions

---

## 4. Tests: unify on JUnit 5 and Robolectric, add coverage reporting

Labels: enhancement, build

**Phase 0 — Foundations** · Effort: M · Refs: Q4

### Background
Tests use three frameworks (JUnit 4, JUnit 5 and a declared but unused TestNG), there is no
coverage reporting, and no instrumented tests exist.

### Change
- Move all unit tests to JUnit 5; use Robolectric where Android classes are needed.
- Remove TestNG and unused `androidTest` dependencies.
- Add Kover and publish a coverage report from CI.
- Add a small instrumented smoke test for the contact picker and permission flows (can follow in
  Phase 2 once those APIs exist).

### Done when
- [ ] One test framework across all modules
- [ ] Coverage report produced by CI
- [ ] TestNG removed from the build

---

## 5. Raise minSdk for 6.0 and remove pre-Marshmallow code

Labels: enhancement, breaking change

**Phase 0 — Foundations** · Effort: S · Refs: P3, D1, D8

### Background
`MIN_SDK_VERSION` is still 21, although raising it to 23 was decided for 5.0 and never applied.
AndroidX and Play libraries have been moving their minimum to 23.

### Change
- Set minSdk according to decision D8 (23 at minimum).
- Remove `SDK_INT >= M` branches, `@TargetApi`/`@RequiresApi(M)` annotations and the pre-M
  "granted at install time" paths in `runtime-permissions`.

### Done when
- [ ] D8 decided and applied
- [ ] No API-level checks below the new minSdk remain
- [ ] Lint reports no obsolete SDK-check warnings

---

## 6. Remove the database-helpers module

Labels: enhancement, breaking change, security

**Phase 1 — Removals and restructure** · Effort: S · Refs: D5, S3

### Background
`DataBaseHelper` has been deprecated since 5.0 and exposes `rawQuery` and string selections, which
invite SQL built by concatenation. Room covers the same needs. Trip O Meter uses it, but stays on
5.x (D12, D14).

### Change
- Delete the `database-helpers` module, its artifact and its entry in the `aeapps` umbrella.
- Explain the replacement (Room) in the 6.0 migration guide.

### Done when
- [ ] Module and artifact removed
- [ ] Migration guide covers the replacement

---

## 7. Split core into models, ui and review modules

Labels: enhancement, breaking change

**Phase 1 — Removals and restructure** · Effort: M · Refs: D7, D11

### Background
`core` mixes data models, UI widgets, a base Activity and the Play in-app review wrapper, so a
module that needs only `ContactInfo` also pulls in AppCompat, RecyclerView, Preference and Play
In-App Review.

### Change
- `models`: the data classes, no Android UI or Play dependencies.
- `ui`: `EmptyRecyclerView`, `RoundedImageView`, inset helpers, shared resources.
- `review`: the in-app review flow.
- Rename artifacts if decision D11 agrees (for example `contacts-api` → `contacts`), and update
  the umbrella, docs site, `DEVELOPMENT.md` and dependency graph.

### Done when
- [ ] D11 decided
- [ ] `models` has no AndroidX UI or Play dependencies
- [ ] Every module depends only on what it uses
- [ ] Migration guide has an old → new artifact table

---

## 8. Replace the mocks module with a test-only testing module

Labels: enhancement, breaking change, security

**Phase 1 — Removals and restructure** · Effort: M · Refs: D3, S7

### Background
The `aeapps` umbrella re-exports `mocks`, so every app using the umbrella ships mock names and fake
data in release builds. Removing it from the umbrella was decided for 5.0 and never applied, and
the module only offers static data.

### Change
- Remove `mocks` from `aeapps`.
- Add a `testing` module intended for `testImplementation`: fakes for each repository
  (`FakeContactsRepository`, later `FakeBillingClient`) and builders for models.
- Delete `mocks`.

### Done when
- [ ] `aeapps` contains no test or mock code
- [ ] `testing` provides fakes for every public repository
- [ ] Library tests use `testing` instead of `mocks`

---

## 9. Remove obsolete code and duplicate helpers

Labels: enhancement, breaking change

**Phase 1 — Removals and restructure** · Effort: S · Refs: A8, P2, D9

### Background
The library carries code that is no longer needed: a 2015 LG hardware menu-key workaround in
`ToolBarBaseActivity` (`onKeyDown`/`onKeyUp`), three "is package installed" methods, two
`launchWebPage` methods, and `SimpleGraphView` and `ColorThemes`, which none of the apps use.

### Change
- Remove the LG key overrides.
- Keep one implementation of each duplicated helper.
- Delete `SimpleGraphView` and `ColorThemes` (D9).

### Done when
- [ ] D9 decided
- [ ] No duplicated helpers remain
- [ ] No `onKeyDown`/`onKeyUp` overrides remain in library code

---

## 10. Convert models and utilities to Kotlin

Labels: enhancement, breaking change

**Phase 1 — Removals and restructure** · Effort: M · Refs: A6, A8, D12

### Background
45 of 50 source files are Java. Models are mutable beans without nullability, and helpers are
static classes. 6.0 is Kotlin-first (D12).

### Change
- Models become immutable Kotlin data classes (`ContactInfo`, `PhoneNumber`, `Message`) with
  immutable lists; `Parcelable` where they cross screens.
- Utilities become Kotlin extension functions grouped by topic.

### Done when
- [ ] `models` and `utilities` contain no Java sources
- [ ] Public types have explicit nullability
- [ ] Unit tests cover the converted helpers

---

## 11. Contacts: replace the stateful gateway with a coroutine-based repository

Labels: enhancement, breaking change

**Phase 2 — Modern APIs** · Effort: L · Refs: A1, A9

### Background
`ContactsApiGateway` must be initialized before use, throws `IllegalStateException` otherwise, and
reports completion through a callback that carries neither data nor errors. This design caused the
5.0 production crash (#43). Its constants are exposed as an interface.

### Change
- `ContactsRepository` with `suspend` functions for queries and a `Flow` that re-emits when
  contacts change (`ContentObserver`).
- No initialization step; errors are returned or thrown as typed exceptions, not swallowed.
- Constants become `internal` or an `object`.
- Remove `ContactsApiGateway` and `ContactsDataConsumer`.

### Done when
- [ ] Random Contact's use cases (all contacts, random contact, contact by id, phone numbers, photo) are covered
- [ ] Changes in the Contacts app are emitted without restarting the process
- [ ] Fake in `testing`; unit tests with Robolectric

---

## 12. Permissions: PermissionRequester on the Activity Result API

Labels: enhancement, breaking change

**Phase 2 — Modern APIs** · Effort: M · Refs: A3

### Background
`PermissionCheckingActivity`, `AbstractPermissionsAwareActivity` and `PermissionCheckingFragment`
force apps to extend a base class and use the deprecated `requestPermissions` API.

### Change
- A composable `PermissionRequester` built on
  `ActivityResultContracts.RequestMultiplePermissions`, usable from any `ComponentActivity` or
  `Fragment`.
- Distinguish granted, denied and permanently denied, with a hook for showing a rationale.
- Remove the base classes and `RuntimePermissionChecker`.

### Done when
- [ ] Works from an Activity and a Fragment without inheritance
- [ ] Permanently denied state is reported
- [ ] Sample app demonstrates it

---

## 13. Contact picker: typed result contract, edge-to-edge and predictive back

Labels: enhancement, breaking change

**Phase 2 — Modern APIs** · Effort: L · Refs: A4, A9, P1, P2

### Background
The multi-contact picker is a base Activity that returns selections as a comma-joined string extra,
loses selections on rotation, does not apply window insets (apps targeting SDK 35+ draw
edge-to-edge) and predates `OnBackPressedDispatcher`.

### Change
- A typed `ActivityResultContract` (request → list of contact ids).
- Selections survive configuration changes.
- Apply window insets; replace `ToolBarBaseActivity` with an inset-aware helper.
- Use `OnBackPressedDispatcher`.
- Constants become `internal`.

### Done when
- [ ] Apps launch the picker with `registerForActivityResult` and get a typed list back
- [ ] Rotation keeps selections
- [ ] Content is not drawn under the system bars at targetSdk 36
- [ ] Back gesture behaves correctly with predictive back enabled

---

## 14. Utilities: safe openWebPage that accepts only http and https

Labels: enhancement, breaking change, security

**Phase 3 — Security hardening** · Effort: S · Refs: S1

### Background
`CommonUtils.launchWebPage`, `MobileNetworkUtils.launchWebPage` and `IntentUtils.getUriIntent` fire
`ACTION_VIEW` for any string, including `intent:`, `file:` and `content:` URIs, and crash with
`ActivityNotFoundException` when nothing can open the link.

### Change
- One `openWebPage(context, url)` that accepts only `http` and `https`, returns a result instead of
  throwing, and can optionally use Custom Tabs.
- Remove the other variants.

### Done when
- [ ] Non-web schemes are rejected
- [ ] No crash when no browser is installed
- [ ] Unit tests for accepted and rejected URLs

---

## 15. Utilities: safe contact, dial and email intents

Labels: enhancement, breaking change, security

**Phase 3 — Security hardening** · Effort: S · Refs: S4, S5, S9

### Background
- `ContactUtils.openWhatsAppContact` builds a content URI by appending an unchecked string.
- `MobileNetworkUtils.callContact` places calls directly with `ACTION_CALL` (needs `CALL_PHONE`)
  and swallows every exception.
- `EmailIntentBuilder` sets a type and then data (which clears the type) and passes `EXTRA_EMAIL`
  as a `String` instead of `String[]`, so some mail apps drop the recipient.

### Change
- Build contact data URIs from a `Long` id with `ContentUris.withAppendedId`.
- Default to `ACTION_DIAL` (no permission, user confirms); keep direct calling as an explicit
  option that reports failure.
- Rebuild email on `ACTION_SENDTO` with `mailto:` and recipients as an array.

### Done when
- [ ] No URI is built by string concatenation
- [ ] Dialling needs no permission by default
- [ ] Email recipient appears in Gmail and at least one other mail app

---

## 16. Review: store counters in private storage

Labels: enhancement, security

**Phase 3 — Security hardening** · Effort: S · Refs: S6

### Background
`AppReviewImpl` stores its counters in the app's default `SharedPreferences`, so its keys mix with
the app's own and are cleared or backed up with them. Its singleton also prevents apps from
customizing the threshold.

### Change
- Store counters in a private, library-named store (via the new `preferences` module).
- Make the review thresholds configurable without subclassing.
- Document backup behaviour.

### Done when
- [ ] No library keys in the app's default preferences
- [ ] Thresholds configurable
- [ ] Unit tests for the show/no-show decision

---

## 17. Models: photo URI instead of a decoded Bitmap

Labels: enhancement, breaking change, security

**Phase 3 — Security hardening** · Effort: S · Refs: S10

### Background
`ContactInfo` holds a decoded `Bitmap`, and apps cache gateways as process singletons, so contact
photos stay in memory for the life of the process.

### Change
- Models carry the contact photo as a `Uri`; apps load it with their image loader.
- Repositories hold no contact data beyond what the caller collects.

### Done when
- [ ] No `Bitmap` in public models
- [ ] Random Contact displays photos from the `Uri`

---

## 18. New module: preferences on DataStore

Labels: enhancement

**Phase 4 — New modules and billing** · Effort: M · Refs: plan section 5.3

### Background
All three apps hand-roll a preferences wrapper (`PreferenceRepository`, `AppPreferences`, direct
`PreferenceManager`), and the review counters need private storage (see the review counters issue).

### Change
- Typed keys, `Flow` reads and `suspend` writes on Jetpack DataStore.
- A migration helper from existing `SharedPreferences`.

### Done when
- [ ] Random Contact's `AppPreferences` can be replaced by it
- [ ] Migration from `SharedPreferences` keeps existing values
- [ ] Unit tests

---

## 19. New module: Play in-app updates

Labels: enhancement

**Phase 4 — New modules and billing** · Effort: M · Refs: plan section 5.3

### Background
`Dependencies.kt` already declares the Play `app-update-ktx` library, but nothing uses it, and it
pairs naturally with in-app review.

### Change
- Lifecycle-aware wrapper for flexible and immediate update flows.
- A reminder policy for updates the user postponed.

### Done when
- [ ] Both update types work in an internal test track
- [ ] Sample app demonstrates it

---

## 20. Billing: sealed results, subscriptions and purchase verification

Labels: enhancement, breaking change, security

**Phase 4 — New modules and billing** · Effort: L · Refs: A7, S2

### Background
`AeBillingClient` requires implementing a 13-method handler, supports only one-time products, and
passes every `PURCHASED` purchase to the app without verifying it. No app uses `billing` today, so
this can move to 6.1.

### Change
- A sealed `PurchaseResult`/`BillingEvent` stream (`Flow`) instead of the handler.
- Subscriptions and offer tokens.
- An optional `PurchaseVerifier`: a built-in local signature check with the app's Base64 public
  key, and an interface for server-side verification; only verified purchases reach the
  "grant entitlement" event. Support `obfuscatedAccountId`.
- `FakeBillingClient` in `testing`.

### Done when
- [ ] Unverified purchases never reach the grant event when a verifier is set
- [ ] Subscriptions can be queried and purchased
- [ ] Fake client used by unit tests

---

## 21. Docs: 6.0 migration guide

Labels: enhancement

**Phase 5 — Release** · Effort: L · Refs: D14

### Change
Write `guides/MIGRATION-v6.md`, in the same format as the v4 and v5 guides:
- Old → new artifact map, including removed (`database-helpers`, `mocks`) and not-ported (`sms-api`) modules.
- Before/after code for contacts, permissions, the picker, utilities and billing.
- Build requirements (minSdk, Kotlin).
- The 5.x maintenance policy (critical and security fixes only).

### Done when
- [ ] Linked from the README migration table
- [ ] Every breaking change in the 6.0 changelog has a section

---

## 22. Sample app: rewrite against 6.0

Labels: enhancement

**Phase 5 — Release** · Effort: L

### Change
- Rewrite the sample in Kotlin against the 6.0 APIs.
- Demos for contacts, the picker, permissions, preferences, in-app review and updates, and billing
  with static test products.
- Add a smoke test so CI exercises the library through the app.

### Done when
- [ ] Sample builds against 6.0 only
- [ ] Every module has a demo screen

---

## 23. Migrate Random Contact to 6.0 pre-releases

Labels: enhancement

**Phase 5 — Release** · Effort: L

### Background
Random Contact is the first adopter: it uses the most-changed modules and is already Kotlin. Trip
O Meter stays on 5.x and Message Counter is dormant.

### Change
For each 6.0 pre-release, move Random Contact to it and report problems back as issues:
`ContactsRepository` instead of its own gateway, the picker contract, `PermissionRequester`,
`preferences` instead of `AppPreferences`, and coroutines instead of Anko.

### Done when
- [ ] Random Contact runs on `6.0.0-rc` with no lib-aeapps 5.x dependency
- [ ] Anko removed from Random Contact

---

## 24. Release automation workflow

Labels: enhancement, build

**Phase 5 — Release** · Effort: M · Refs: Q6

### Background
Tagging, warming the JitPack build and creating the GitHub release are manual, and nothing checks
that the tag matches `VERSION_STRING`.

### Change
A tag-triggered workflow that:
- fails if the tag is not `v` + `VERSION_STRING`;
- creates the GitHub release with the matching `VersionHistory.md` entry, marked pre-release for
  alpha/beta/rc;
- requests the JitPack build and reports its result.

### Done when
- [ ] A mismatched tag fails the workflow
- [ ] The GitHub release is created automatically

---

## 25. Optional: publish to Maven Central

Labels: enhancement, build

**Phase 5 — Release** · Effort: M · Refs: Q3, D4

### Background
JitPack builds artifacts on demand and does not sign them. Maven Central gives signed, immutable
releases that resolve with `mavenCentral()`, at the cost of a new group ID and key management. The
plan recommends staying on JitPack unless the library gains users outside your own apps (section 6.1).

### Change
Only if D4 changes: verify a namespace (for example `io.github.midhunhk`), add signing and
Central Portal publishing in CI, and complete the POM metadata (licence, SCM, developers).

### Done when
- [ ] D4 revisited
- [ ] If adopted: a signed release resolves from `mavenCentral()`

---

## 26. Lint and Kotlin static analysis in CI

Labels: enhancement, build

**Phase 0 — Foundations** · Effort: M · Refs: Q5

### Background
Finding Q5 has no task in the plan's section 7, so it is added here. Lint warnings are not
enforced, and there is no Kotlin linting.

### Change
- Lint with `warningsAsErrors` and a committed baseline for existing warnings.
- detekt or ktlint for Kotlin sources, run in CI.

### Done when
- [ ] New lint warnings fail CI
- [ ] Kotlin linter runs in CI

---

## 27. 6.0 release plan

Labels: enhancement

Tracking issue for [RELEASE-PLAN-v6.md](https://github.com/midhunhk/lib-aeapps/blob/main/guides/RELEASE-PLAN-v6.md).
Each phase ends with a green build and a pre-release tag.

### Decisions

- [ ] D4 — Maven Central: optional, stay on JitPack for now
- [ ] D8 — minSdk for 6.0 (23 at minimum)
- [ ] D9 — delete `SimpleGraphView` and `ColorThemes`
- [ ] D10 — do not port `sms-api` to 6.0
- [ ] D11 — rename artifacts once in 6.0
- [x] D12 — Kotlin-first; Trip O Meter stays on 5.x
- [ ] D13 — coroutines in data modules
- [ ] D14 — 5.x gets critical and security fixes only

### Phase 0 — Foundations (6.0.0-alpha)
- [ ] #1 Version catalog and build-logic convention plugin
- [ ] #2 Binary-compatibility validation and explicit API mode
- [ ] #3 Pin GitHub Actions to SHAs, automate dependency updates
- [ ] #4 JUnit 5, Robolectric and coverage
- [ ] #5 Raise minSdk
- [ ] #26 Lint and Kotlin static analysis

### Phase 1 — Removals and restructure (6.0.0-alpha)
- [ ] #6 Remove database-helpers
- [ ] #7 Split core into models, ui and review
- [ ] #8 Replace mocks with testing
- [ ] #9 Remove obsolete code and duplicate helpers
- [ ] #10 Convert models and utilities to Kotlin

### Phase 2 — Modern APIs (6.0.0-beta)
- [ ] #11 ContactsRepository
- [ ] #12 PermissionRequester
- [ ] #13 Contact picker contract, edge-to-edge, predictive back

### Phase 3 — Security hardening (6.0.0-beta)
- [ ] #14 Safe openWebPage
- [ ] #15 Safe contact, dial and email intents
- [ ] #16 Review counters in private storage
- [ ] #17 Photo URI instead of Bitmap

### Phase 4 — New modules and billing (6.0.0-beta / rc)
- [ ] #18 preferences module
- [ ] #19 app-update module
- [ ] #20 Billing rework and purchase verification (may move to 6.1)

### Phase 5 — Release (6.0.0-rc → 6.0.0)
- [ ] #21 Migration guide
- [ ] #22 Sample app
- [ ] #23 Migrate Random Contact
- [ ] #24 Release automation
- [ ] #25 Optional: Maven Central

### Not scheduled
- A9 (constants as interfaces) is folded into #11 and #13.
- A2 (SMS repository) and P4 (notifications) are out of 6.0 by decision D10 and plan section 5.3.

The `#N` numbers above are draft numbers; they are replaced with the real issue numbers when the
issues are created.
