# lib-aeapps 6.0 — Analysis and Release Plan

Status: **draft for review** · Written 2026-11-19 · Baseline: `mulberry` at `2c58cc1` (5.1.0)

This page proposes what should change in the next major version and why. 6.0 is allowed to break
API and artifact contracts, so it is the place for the clean-ups that 5.x deliberately deferred,
for security hardening, and for a small number of new modules that the apps built on this library
currently write for themselves.

Like [RELEASE-PLAN-v5.md](RELEASE-PLAN-v5.md), items are graded **A** (must have for 6.0),
**B** (should have) and **C** (nice to have), and the plan at the end is ordered so each phase
leaves the branch releasable. Nothing in the codebase was changed while producing this page.

---

## 1. Summary

5.x made the library buildable, publishable, tested and documented again. What it did not change
is the *shape* of the API, which still reflects Android practice from 2015–2020:

- **Callback-and-inheritance APIs.** Contacts and SMS use stateful gateways with a callback that
  carries no data; permissions and the multi-contact picker work by extending base Activities and
  Fragments; results come back through string extras.
- **Mostly Java, no nullability.** 45 of 50 source files are Java. Kotlin callers see platform
  types everywhere, and models are mutable beans.
- **A `core` that mixes data and UI.** Data-only modules (`sms-api`, `mocks`) pull in AppCompat,
  RecyclerView, Preference and Play In-App Review.
- **Security defaults that rely on the caller.** URL helpers open any URI scheme, the billing
  client does no purchase verification, and the deprecated `DataBaseHelper` exposes raw SQL.

The apps built on the library work around these gaps the same way: all three hand-roll a
preferences wrapper, and the Kotlin apps use the abandoned Anko `doAsync` for background work.

**6.0 in one sentence:** a Kotlin-only, coroutine-based library with explicit, stable public API,
safe defaults, and three new small modules — `preferences`, `app-update` and `testing` — while
removing what is deprecated, obsolete or no longer used.

Java apps are not a target for 6.0: Trip O Meter stays on 5.x until it moves to Kotlin (D12).

---

## 2. How the analysis was done

| Step | What was looked at |
|---|---|
| 1 | Open items in [RELEASE-PLAN-v5.md](RELEASE-PLAN-v5.md): Phase 3, decisions D1–D7, findings not fixed in 5.x |
| 2 | Current sources on `mulberry`: language split, public surface, deprecated code, build setup |
| 3 | The three client apps: dependencies and the capabilities they implement themselves |
| 4 | A security pass over intents, storage, SQL, billing and supply chain |
| 5 | Platform changes for apps targeting SDK 35–37 |

Claims about third-party or platform behaviour that come from memory rather than a lookup are
marked **verify**. Re-check them against current documentation when the work starts.

---

## 3. Where 5.1 leaves things

### 3.1 Modules

| Module | Java / Kotlin files | Notes |
|---|---|---|
| `core` | 10 / 0 | Models, views, `ToolBarBaseActivity`, in-app review, resources |
| `utilities` | 7 / 0 | Static helper classes |
| `contacts-api` | 11 / 0 | Stateful gateway; callback API |
| `sms-api` | 6 / 0 | Gateway; `READ_SMS` is restricted on Google Play |
| `runtime-permissions` | 6 / 0 | Inheritance-based; deprecated `requestPermissions` API |
| `multi-contact` | 5 / 0 | Base Activity; comma-joined string result |
| `database-helpers` | 1 / 1 | `DataBaseHelper` deprecated since 5.0 (D5: remove in 6.0) |
| `billing-client` | 0 / 1 | Play Billing 8; one-time products only; 13-method handler |
| `mocks` | 0 / 2 | Still bundled in `aeapps` (D3 decided removal; not applied) |
| `aeapps` | — | Umbrella of all nine |

### 3.2 Decisions from 5.x that were never applied

| ID | Decision | State |
|---|---|---|
| D1 | Raise minSdk 21 → 23 | Not applied; `ConfigurationData.MIN_SDK_VERSION` is still 21 |
| D3 | Remove `mocks` from the `aeapps` umbrella | Not applied; release APKs of umbrella users ship mock data |
| D5 | Remove `database-helpers` in 6.0 | Pending — this release |
| D7 | Split `core` into models and UI in 6.0 | Pending — this release |

### 3.3 How the apps use the library

| App | Language | Uses | Builds itself |
|---|---|---|---|
| Message Counter | Kotlin | `aeapps` (alpha.14) | **Not functional on Google Play:** without the `READ_SMS` permission it only shows a notice pointing to an earlier version on an alternate app store. Its code contains a donations screen on an old Play Billing API, but it is not in use. |
| Random Contact | Kotlin | five modules (beta.02/04) | Preferences wrapper, Anko `doAsync`, its own random-contact gateway on top of `contacts-api` |
| Trip O Meter | **Java** | `aeapps` (beta.03) | Preferences via `PreferenceManager`, deprecated `DataBaseHelper` |

Three conclusions shape this plan:

- **Random Contact is the only app that will adopt 6.0 soon.** Trip O Meter stays on 5.x until it
  is converted to Kotlin, and Message Counter is dormant on Google Play.
- **The library should offer coroutine APIs**, so Kotlin apps can drop Anko.
- **Modules with no active user** (`sms`, `billing`) should not drive the 6.0 schedule. `sms` is
  not ported at all (D10); `billing` is kept but sits late in the plan.

---

## 4. Findings

Severity: **A** = must have for 6.0 · **B** = should have · **C** = nice to have.

### 4.1 Security

| # | Sev | Finding | Proposal |
|---|---|---|---|
| S1 | A | **URL helpers open any scheme.** `CommonUtils.launchWebPage`, `MobileNetworkUtils.launchWebPage` and `IntentUtils.getUriIntent` fire `ACTION_VIEW` for whatever string they get, including `intent:`, `file:` and `content:` URIs, and crash with `ActivityNotFoundException` if nothing handles it. | One `openWebPage(context, url)` that accepts only `http`/`https`, returns a result instead of throwing, and optionally uses Custom Tabs. Remove the duplicates. |
| S2 | A | **Billing purchases are not verified.** `AeBillingClient` passes every `PURCHASED` purchase to the app; nothing checks the signature, and there is no hook for server-side verification. | Add an optional `PurchaseVerifier`: a built-in verifier for the app's Base64 RSA public key (local check), and an interface for server-side verification. Only verified purchases reach the "grant entitlement" callback. Support `obfuscatedAccountId`. No app uses `billing` today, so this lands with the billing rework late in the plan. |
| S3 | A | **Raw SQL helpers.** `DataBaseHelper` exposes `rawQuery` and string selections, inviting SQL built by concatenation. It has been deprecated since 5.0. | Remove `DataBaseHelper` and the `database-helpers` module (D5). Point to Room in the migration guide. |
| S4 | B | **Content URI built by concatenation.** `ContactUtils.openWhatsAppContact` appends an unchecked string to `content://com.android.contacts/data/`. | Take a `Long` id and build the URI with `ContentUris.withAppendedId`. |
| S5 | B | **`ACTION_CALL` with silent failure.** `MobileNetworkUtils.callContact` places calls directly (needs `CALL_PHONE`) and swallows every exception. | Default to `ACTION_DIAL` (no permission, user confirms); keep direct calling as an explicit, documented option that reports failure. |
| S6 | B | **Library writes into the app's default `SharedPreferences`.** `AppReviewImpl` stores its counters in `PreferenceManager.getDefaultSharedPreferences`, so its keys mix with the app's own and are cleared or backed up with them. | Use a private, library-named preferences file (via the new `preferences` module), and document its backup behaviour. |
| S7 | B | **Mock data in release builds.** `aeapps` re-exports `mocks`, so every app that uses the umbrella ships mock names and fake data. | Remove `mocks` from `aeapps` (D3); replace it with a `testing` module meant for `testImplementation` only (see 5.4). |
| S8 | B | **Supply chain.** Artifacts are built by JitPack from tags and are unsigned; GitHub Actions are pinned to moving major tags; there is no automated dependency update. | Pin Actions to commit SHAs; add Dependabot or Renovate for Gradle and Actions. Signed artifacts would need Maven Central, which is optional (D4). |
| S9 | C | **`EmailIntentBuilder` is wrong as well as unsafe-by-default.** It sets a type and then data (which clears the type) and passes `EXTRA_EMAIL` as a `String` instead of `String[]`, so some mail apps drop the recipient. | Rebuild on `ShareCompat`/`ACTION_SENDTO` with `mailto:`, and recipients as an array. |
| S10 | C | **Bitmaps and contact data kept in long-lived objects.** `ContactInfo` holds a decoded `Bitmap`; apps cache gateways as process singletons. | Models carry a photo `Uri`, not a `Bitmap`; repositories hold no contact data beyond what the caller collects. |

### 4.2 API design

| # | Sev | Finding | Proposal |
|---|---|---|---|
| A1 | A | **Stateful contacts gateway.** `initialize` before use, `IllegalStateException` otherwise, callback without data or error (the cause of the 5.0 production crash). | `ContactsRepository` with `suspend` functions and a `Flow` that re-emits when contacts change (`ContentObserver`). No initialization step. |
| A2 | C | **SMS gateway mirrors the same pattern** and filters by the unreliable `person` column. | Not ported to 6.0 (D10). If `sms` is revived later: an `SmsRepository` with `suspend` queries by address or thread and an observable message count. |
| A3 | A | **Inheritance-based permissions.** `PermissionCheckingActivity`, `AbstractPermissionsAwareActivity` and `PermissionCheckingFragment` force a base class and use deprecated `requestPermissions`. | A composable `PermissionRequester` built on `ActivityResultContracts.RequestMultiplePermissions`, usable from any `ComponentActivity` or `Fragment`, with rationale and "denied permanently" states. Remove the base classes. |
| A4 | A | **Multi-contact result as a comma-joined string extra.** | A typed `ActivityResultContract<PickContactsRequest, List<ContactId>>`; selections survive rotation. |
| A5 | A | **No explicit public API boundary.** `*Utils`, `*Constants` and cache classes documented as internal are `public`; nothing detects accidental API changes. | Kotlin explicit API mode, `internal` for implementation classes, and binary-compatibility validation in CI. |
| A6 | B | **Mutable Java bean models without nullability.** | Kotlin data classes (`ContactInfo`, `PhoneNumber`, `Message`) with immutable lists; `Parcelable` where they cross screens. |
| A7 | B | **Billing handler with 13 methods.** Every app must implement all of them. | A sealed `PurchaseResult`/`BillingEvent` stream (`Flow`); subscriptions and offer tokens; a fake client for tests. |
| A8 | B | **Static utility classes with duplicates.** Three "is package installed" methods, two `launchWebPage` methods, a pie chart view (`SimpleGraphView`) and a 2015 LG menu-key workaround in `ToolBarBaseActivity`. | Kotlin extension functions; remove duplicates and obsolete workarounds; move `SimpleGraphView` and `ColorThemes` out or delete them (decision D9). |
| A9 | C | **Constants exposed as interfaces** (`ContactsApiConstants`, `SmsApiConstants`, `MultiContactPickerConstants`). | `object`s or `internal` constants. |

### 4.3 Platform

| # | Sev | Finding | Proposal |
|---|---|---|---|
| P1 | A | **Edge-to-edge.** Apps targeting SDK 35+ draw edge-to-edge by default, and the opt-out goes away at 36 (**verify** exact level). `ToolBarBaseActivity` and the multi-contact screen don't apply window insets. | Apply insets in the library's screens; replace `ToolBarBaseActivity` with an inset-aware helper rather than a base class. |
| P2 | A | **Predictive back.** `onKeyDown`/`onKeyUp` overrides and Activity-based navigation predate `OnBackPressedDispatcher`; predictive back becomes the default for apps targeting recent SDKs (**verify** level). | Use `OnBackPressedDispatcher` in library screens; remove the key overrides. |
| P3 | A | **minSdk 21.** AndroidX and Play libraries have been moving to 23 (D1). | Raise to 23 at least; consider 24 or 26 (decision D8). Removes every pre-Marshmallow permission branch. |
| P4 | C | **Notification permission.** Apps targeting 33+ must request `POST_NOTIFICATIONS`. Only Message Counter posts notifications, and it is dormant. | No module in 6.0; `permissions` covers the request if an app needs it (see 5.3). |
| P5 | B | **`READ_SMS` policy.** Google Play allows `READ_SMS` only for default SMS apps and a few approved uses, so `sms-api` cannot be used by most Play apps, including Message Counter. | Do not port `sms-api` to 6.0; it stays available in 5.x (D10). |

### 4.4 Build, release and quality

| # | Sev | Finding | Proposal |
|---|---|---|---|
| Q1 | A | **Duplicated module build scripts** (~60 lines × 9) and `buildSrc` constants. | A `build-logic` convention plugin and a `gradle/libs.versions.toml` catalog. Removes stale entries such as the unused Guava constant. |
| Q2 | A | **No API compatibility check.** | Kotlin binary-compatibility validator (or Metalava) with committed API dumps, run in CI. |
| Q3 | C | **JitPack only.** | Optional. Maven Central would add signing and immutability, at the cost of a new group ID and key management; see D4 and section 6.1. |
| Q4 | B | **Three test frameworks** (JUnit 4, JUnit 5, TestNG) and no coverage reporting; no instrumented tests. | JUnit 5 plus Robolectric for Android classes; Kover coverage in CI; a small instrumented smoke test for the picker and permissions flows. |
| Q5 | B | **Static analysis.** Lint warnings are not enforced; no Kotlin linting. | Lint with `warningsAsErrors` and a baseline; detekt or ktlint. |
| Q6 | C | **Release automation.** Tagging, JitPack warm-up and the GitHub release are manual. | A tag-triggered workflow that verifies `VERSION_STRING` matches the tag, creates the GitHub release from `VersionHistory.md`, and requests the JitPack build. |

---

## 5. Proposed module structure for 6.0

### 5.1 Overview

| 6.0 module | From 5.x | Contents | Depends on |
|---|---|---|---|
| `models` | `core` (models) | `ContactInfo`, `PhoneNumber`, `Message` as Kotlin data classes | — |
| `ui` | `core` (views, resources) | `EmptyRecyclerView`, `RoundedImageView`, inset helpers, shared resources | AppCompat, RecyclerView |
| `review` | `core` (in-app review) | In-app review flow with configurable thresholds | Play Review, `preferences` |
| `utilities` | `utilities` | Kotlin extensions; safe intents (S1, S4, S5, S9) | — |
| `contacts` | `contacts-api` | `ContactsRepository` (A1) | `models`, coroutines |
| `contacts-picker` | `multi-contact` | `ActivityResultContract` picker (A4) | `models`, `ui`, `contacts` |
| `permissions` | `runtime-permissions` | `PermissionRequester` (A3) | Activity, Fragment |
| `billing` | `billing-client` | Billing with verification and sealed results (S2, A7) | Play Billing, coroutines |
| `preferences` | **new** | Typed preferences on DataStore | DataStore |
| `app-update` | **new** | Play in-app updates | Play App Update |
| `testing` | replaces `mocks` | Fakes for every repository, test data builders | `models`, coroutines-test |
| `aeapps` | `aeapps` | Umbrella of the production modules | all of the above except `testing` |
| — | `database-helpers` | **Removed** (D5, S3) | — |
| — | `sms-api` | **Not ported**; stays available in 5.x (D10, P5) | — |

Module names drop the `-api` suffix because the artifacts are already namespaced. Renaming is
optional: if D11 decides to keep the 5.x artifact IDs where the purpose is unchanged, the plan
works the same.

### 5.2 Dependency graph

```
models ◄── contacts ◄── contacts-picker ──► ui
   ▲
   └───── testing (test-only)

preferences ◄── review

utilities, permissions, billing, app-update  (no library dependencies)
```

### 5.3 New modules — why each one

| Module | Evidence | Scope |
|---|---|---|
| `preferences` | All three apps write their own wrapper (`PreferenceRepository`, `AppPreferences`, direct `PreferenceManager`). `review` needs private storage (S6). | Typed keys, `Flow` reads, `suspend` writes on Jetpack DataStore; a migration helper from `SharedPreferences`. |
| `app-update` | `Dependencies.kt` already declares `app-update-ktx` but nothing uses it; it pairs naturally with in-app review. | Flexible and immediate update flows, lifecycle-aware, with a stalled-update reminder policy. |
| `testing` | `mocks` ships in release builds (S7) and only offers static data. | `FakeContactsRepository`, `FakeBillingClient`, model builders. For `testImplementation` only. |

Considered and left out of 6.0:

- **`notifications`** (channels, the Android 13 permission, safe `PendingIntent` defaults). The
  only app that posts notifications is Message Counter, which is dormant. Add it when an active
  app needs it.
- A Jetpack Compose UI module (none of the apps use Compose yet), an analytics or crash-reporting
  wrapper (provider-specific), and a backup module.

---

## 6. Decisions needed

| ID | Question | Recommendation |
|---|---|---|
| **D8** | Which minSdk for 6.0? | **23** at minimum (D1). 24 or 26 only if a dependency requires it or the apps' install bases allow it — check the Play Console. |
| **D9** | Keep `SimpleGraphView`, `ColorThemes` and `RoundedImageView`? | Delete `SimpleGraphView` and `ColorThemes`: none of the three apps use them (checked 2026-11-19). Keep `RoundedImageView` (12 uses) and `EmptyRecyclerView` (18 uses) in `ui`; rewrite `RoundedImageView` without per-frame bitmap allocations. |
| **D10** | Port `sms-api` to 6.0? | **No.** Google Play restricts `READ_SMS`, and its only user, Message Counter, cannot function on Play for that reason. Leave it on 5.x; revive it in a later 6.x if an app needs it. |
| **D11** | Rename artifacts (`contacts-api` → `contacts`, …)? | **Yes**, once, in 6.0 — renaming is cheapest when every app is migrating anyway. |
| **D12** | Kotlin-only, or keep Java callers working? | **Decided 2026-11-19: Kotlin-first.** Sources and public APIs are Kotlin; Java interop is not a goal. Trip O Meter stays on 5.x until it is converted to Kotlin. |
| **D13** | Is a coroutines dependency acceptable in data modules? | **Yes.** Random Contact already uses coroutines, and `suspend`/`Flow` replace the callback APIs. |
| **D14** | How long is 5.x maintained? | **Critical and security fixes only**, as `5.1.x` patch releases from the `mulberry` branch, for as long as Trip O Meter depends on it. No new features. |
| **D4** | Maven Central in 6.0? | **Optional: stay on JitPack unless the library gains users outside your own apps.** See 6.1. |

### 6.1 JitPack and Maven Central compared

| | JitPack (today) | Maven Central |
|---|---|---|
| How a release is made | Push a git tag; JitPack builds it from source the first time someone requests it | Build locally or in CI, sign, upload, and release through the Central Portal |
| Who builds the artifact | JitPack's servers | You (or your CI) |
| Signing | None | GPG signature required for every file |
| Immutability | Builds are cached and can be rebuilt; a re-created tag may serve different files | A released version can never be changed or deleted |
| Consumer setup | Must add `maven { url = uri("https://jitpack.io") }` | Works with `mavenCentral()`, which every Android project already has |
| Group ID | `com.github.midhunhk.lib-aeapps`, derived from the GitHub repository | A verified namespace, for example `io.github.midhunhk` (changes every coordinate once) |
| Availability | Depends on JitPack, a single third-party service; a failed build means nobody can resolve the version | Mirrored by many repositories and proxies |
| Effort | Already working; no secrets | One-time: namespace verification, a GPG key, Central Portal credentials as CI secrets, a publishing plugin |

**When Central is worth it:** if people outside your own apps use the library (they get
`mavenCentral()` resolution, signatures they can verify, and versions that cannot change), or if
JitPack's availability or rebuild behaviour causes problems. **When it isn't:** for a library
consumed only by your own apps, JitPack gives the same result with no keys to manage. If you
move later, 6.0 already changes coordinates (D11), so pairing the move with a major version keeps
the churn to one migration.

---

## 7. Plan

Effort: **S** ≈ under an hour, **M** ≈ half a day, **L** ≈ a day or more. Work happens on the 6.0
codename branch; each phase ends with a green build and a pre-release tag.

### Phase 0 — Foundations (6.0.0-alpha)

| Task | Refs | Effort |
|---|---|---|
| Version catalog and `build-logic` convention plugin; delete per-module duplication | Q1 | L |
| Binary-compatibility validation with API dumps in CI; Kotlin explicit API mode | Q2, A5 | M |
| Pin GitHub Actions to SHAs; add Dependabot/Renovate | S8 | S |
| Unify on JUnit 5 + Robolectric; add Kover coverage to CI | Q4 | M |
| Raise minSdk (D8) and delete pre-23 branches | P3 | S |

### Phase 1 — Removals and restructure (6.0.0-alpha)

| Task | Refs | Effort |
|---|---|---|
| Remove `database-helpers` | D5, S3 | S |
| Split `core` into `models`, `ui` and `review`; rename artifacts if D11 agrees | D7, D11 | M |
| Remove `mocks` from `aeapps`; introduce `testing` with fakes | D3, S7 | M |
| Delete obsolete code: LG key workaround, duplicate helpers, D9 removals | A8, P2 | S |
| Convert `models` and `utilities` to Kotlin | A6, A8, D12 | M |

### Phase 2 — Modern APIs (6.0.0-beta)

| Task | Refs | Effort |
|---|---|---|
| `ContactsRepository` with `suspend`/`Flow` | A1 | L |
| `PermissionRequester` on Activity Result APIs; remove base classes | A3 | M |
| Typed `ActivityResultContract` contact picker; edge-to-edge and predictive back | A4, P1, P2 | L |

### Phase 3 — Security hardening (6.0.0-beta)

| Task | Refs | Effort |
|---|---|---|
| Safe `openWebPage` (http/https only, Custom Tabs, no crash) | S1 | S |
| `ContentUris` for contact data URIs; `ACTION_DIAL` default; `EmailIntentBuilder` fix | S4, S5, S9 | S |
| Review counters in private storage | S6 | S |
| Photo `Uri` instead of `Bitmap` in models | S10 | S |

### Phase 4 — New modules and billing (6.0.0-beta / rc)

| Task | Refs | Effort |
|---|---|---|
| `preferences` on DataStore, with `SharedPreferences` migration | 5.3 | M |
| `app-update` | 5.3 | M |
| Billing: sealed results, subscriptions, fake client, purchase verification (no app uses `billing` today, so this can slip to 6.1) | A7, S2 | L |

### Phase 5 — Release (6.0.0-rc → 6.0.0)

| Task | Refs | Effort |
|---|---|---|
| `guides/MIGRATION-v6.md`: artifact map, API before/after, the 5.x maintenance policy | D14 | L |
| Sample app rewritten against 6.0, including billing and permissions demos | — | L |
| Migrate Random Contact against each pre-release | — | L |
| Release automation workflow | Q6 | M |
| Optional: Maven Central publishing with signing and POM metadata | Q3, D4 | M |

---

## 8. What 6.0 means for the apps

| App | Main work |
|---|---|
| Random Contact | Replace its random-contact gateway with `ContactsRepository`; picker contract; drop Anko and the preferences wrapper |
| Trip O Meter | Stays on 5.x (D12, D14). To move to 6.0 it first needs converting to Kotlin and replacing `DataBaseHelper` with Room (D5). |
| Message Counter | None. It is dormant on Google Play and keeps its current library version. |

Random Contact is the first and, for now, only adopter: it uses the most-changed modules
(contacts, picker, permissions) and is already Kotlin.

---

## 9. Suggested order of the first pull requests

1. **Build foundations:** catalog, convention plugin, API dumps (no behaviour change; makes every later diff smaller).
2. **Removals:** `database-helpers`, `mocks` from `aeapps`, obsolete code.
3. **Module split:** `models` / `ui` / `review`, plus artifact renames.
4. **Contacts and permissions APIs:** the changes the apps feel most.
5. **Security hardening:** small, independent PRs, one per finding.

After those five, tag `6.0.0-alpha.01` and migrate Random Contact against it.
