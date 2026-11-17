# Migrating from lib-aeapps 4.1 to 5.0

This guide is for apps that use **4.1.x (Kiwiberry)** and are moving to **5.0.x (Lingonberry)**.
If you are already on a 5.0 alpha or beta, skip to
[section 6](#6-from-a-50-pre-release).

Version 5.0 is mostly a rebuild of how the library is built and published, after the Bintray
and JCenter shutdown made the 4.x publishing setup unusable. Most modules keep their 4.1 API.
The changes that will need code edits in your app are:

| Area | Impact | Section |
|---|---|---|
| Build requirements (minSdk, JDK, AGP) | Every app | [1](#1-build-requirements) |
| Dependency coordinates | Every app | [2](#2-dependency-coordinates) |
| Resource `R` class packages | Apps that use library resources from code | [3](#3-resource-r-classes) |
| `billing-client` | Rewrite of your handler | [4.1](#41-billing-client--rewrite-required) |
| `mocks`, `database-helpers` | Small call-site changes | [4.2](#42-mocks), [4.3](#43-database-helpers) |
| `multi-contact`, `core` (Play Core) | Only if you relied on old behaviour | [4.4](#44-multi-contact), [4.5](#45-core) |
| Behaviour fixes | Review, usually no code change | [5](#5-behaviour-changes) |

---

## 1. Build requirements

| | 4.1 | 5.0 |
|---|---|---|
| Minimum Android version (`minSdk`) | 16 | **21** |
| Library compiled against | 29 | 37 |
| Java bytecode | Java 8 | **Java 17** |
| Built with | AGP 4.1, Gradle 6.5 | AGP 9.4, Gradle 9.6 |
| Contains Kotlin | No | Yes (`billing-client`, `mocks`, `database-helpers`) |

What this means for your app:

- **`minSdk` must be 21 or higher.**
- **Build with JDK 17** and an Android Gradle Plugin that can dex Java 17 bytecode. AGP 8.0 or
  newer is the safe choice.
- **`compileSdk`:** the library itself does not force a value, but the AndroidX and Play
  libraries it depends on do. If yours is too low, the `check…AarMetadata` task fails the build
  and names the version required. Raising `compileSdk` does not change your app's behaviour;
  only `targetSdk` does.
- **Kotlin standard library** now arrives as a transitive dependency. Java-only apps need no
  change. Kotlin apps should be on Kotlin 2.x so that the compiler can read the library's
  metadata (verify against your Kotlin version if you are on 1.9).

---

## 2. Dependency coordinates

The group and artifact names are the same as 4.1. Only the version changes.

```kotlin
// settings.gradle(.kts)
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }   // was alongside jcenter() in 4.x
    }
}
```

```kotlin
// app/build.gradle(.kts)
val libAeAppsVersion = "v5.0.1"

implementation("com.github.midhunhk.lib-aeapps:core:$libAeAppsVersion")
implementation("com.github.midhunhk.lib-aeapps:contacts-api:$libAeAppsVersion")
// …or everything in one:
implementation("com.github.midhunhk.lib-aeapps:aeapps:$libAeAppsVersion")
```

- **Remove `jcenter()`** from your repositories if it is still there. It is read-only and the
  library is no longer published to it.
- **Use the git tag name, including the `v`**, as the version (`v5.0.1`). This is the form
  JitPack serves the 5.0 artifacts under.
- Artifact IDs are unchanged: `core`, `utilities`, `contacts-api`, `sms-api`,
  `runtime-permissions`, `multi-contact`, `database-helpers`, `billing-client`, `mocks`,
  `aeapps`.

**Transitive dependencies are now declared correctly.** Where a module's public API exposes
AppCompat, RecyclerView, Fragment or Play Billing types, that library is now a compile-time
dependency of the module. If you only declared AppCompat or RecyclerView yourself to make the
4.1 artifacts compile, you can keep or drop those lines; keeping them is harmless.

---

## 3. Resource R classes

Each module now has an explicit namespace, and it differs from the 4.1 manifest package for
most modules. **Fully qualified references to a library `R` class must be updated:**

| Module | 4.1 `R` class | 5.0 `R` class |
|---|---|---|
| `core` | `com.ae.apps.lib.R` | `com.ae.apps.lib.core.R` |
| `contacts-api` | `com.ae.apps.lib.contacts.R` | `com.ae.apps.lib.contacts_api.R` |
| `sms-api` | `com.ae.apps.lib.api.sms.R` | `com.ae.apps.lib.sms_api.R` |
| `runtime-permissions` | `com.ae.apps.lib.runtimepermissions.R` | `com.ae.apps.lib.runtime_permissions.R` |
| `multi-contact` | `com.ae.apps.lib.multicontact.R` | `com.ae.apps.lib.multi_contact.R` |
| `database-helpers` | `com.ae.apps.lib.database.R` | `com.ae.apps.lib.database_helpers.R` |
| `billing-client` | `com.ae.apps.libs.billingclient.R` | `com.ae.apps.lib.billing_client.R` |
| `utilities`, `mocks` | unchanged | unchanged |

The most common case is the default contact pictures:

```java
// 4.1
.defaultContactPicture(com.ae.apps.lib.R.drawable.profile_icon_3)
// 5.0
.defaultContactPicture(com.ae.apps.lib.core.R.drawable.profile_icon_3)
```

Only `R` classes moved. **Java and Kotlin class packages are unchanged** (for example
`com.ae.apps.lib.api.contacts.ContactsApiGateway`), so ordinary imports keep working.

If your app sets `android.nonTransitiveRClass=true`, your own `R` no longer includes library
resources, so references through your app's `R` must also switch to the library `R` above. With
`false`, references through your app's own `R` still work.

---

## 4. API changes by module

### 4.1 billing-client — rewrite required

The module moved from Play Billing Library 3 to **Play Billing Library 8**, and was rewritten in
Kotlin. Play Billing replaced SKUs with *products*, so the handler interface changed and needs
to be re-implemented.

**Handler interface** (`AeBillingClient.BillingClientHandler`):

| 4.1 | 5.0 |
|---|---|
| `void onBillingClientSetup()` | unchanged |
| `List<String> getSkus()` | `List<String> getProductIds()` |
| `void skuDetailsResponse(List<SkuDetails>)` | `void productDetailsResponse(List<ProductDetails>)` |
| `void handlePurchase(Purchase)` | unchanged |
| `void handleUserCancelled(List<Purchase>)` | unchanged |
| `void handlePurchaseError(List<Purchase>, int)` | `void handlePurchaseError(List<Purchase>, int, String debugMessage)` |
| — | `void onPurchaseConsumed(String purchaseToken)` |
| — | `void onConsumePurchaseError(String purchaseToken, int responseCode)` |
| — | `void onPurchaseAcknowledged(String purchaseToken)` |
| — | `void onAcknowledgePurchaseError(String purchaseToken, int responseCode)` |
| — | `void handleItemAlreadyOwned(List<Purchase>)` |
| — | `void onQueryPurchasesResponse(String productType, List<Purchase>)` |
| — | `void onQueryPurchasesError(String productType, int responseCode)` |

From Java you must implement every method, including the three-argument
`handlePurchaseError` (the default value of `debugMessage` only applies to Kotlin callers).

**Client methods** (`AeBillingClient`):

| 4.1 | 5.0 |
|---|---|
| `initialize(AppCompatActivity, BillingClientHandler)` | unchanged |
| `protected int launchBillingFlow(Activity, SkuDetails)` | `public int launchBillingFlow(Activity, ProductDetails)` |
| — | `public int launchBillingFlowForProductId(Activity, String productId)` |
| `protected void consumeAsync(Purchase, ConsumeResponseListener)` | `public void consumePurchase(Purchase)` — result via `onPurchaseConsumed` / `onConsumePurchaseError` |
| — | `public void acknowledgePurchase(Purchase)` — for non-consumables |
| — | `public void queryPurchasesAsync()` — call from `onResume` to restore purchases |
| `public void onDestroy()` | removed; the client observes the activity lifecycle and disconnects itself |

Things to check while migrating:

- **Subclasses can no longer override the client's methods.** `AeBillingClient` is `open`, but
  its methods are final. If you subclassed it to customise `launchBillingFlow` or
  `consumeAsync`, call the public methods instead.
- **Every purchase must be consumed or acknowledged** in `handlePurchase` when its state is
  `PURCHASED`. Google Play refunds purchases that are not acknowledged within three days. Do not
  grant anything while the state is `PENDING`.
- **Only one-time (`INAPP`) products** can be queried and launched. `queryPurchasesAsync` also
  reports subscriptions, but buying subscriptions is not supported yet.

A minimal Java handler:

```java
public class StoreFragment extends Fragment implements AeBillingClient.BillingClientHandler {

    private final AeBillingClient billingClient = new AeBillingClient();

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        billingClient.initialize((AppCompatActivity) requireActivity(), this);
    }

    @Override
    public void onResume() {
        super.onResume();
        billingClient.queryPurchasesAsync();
    }

    @NonNull @Override
    public List<String> getProductIds() {
        return Arrays.asList("donate_small", "donate_large");
    }

    @Override
    public void productDetailsResponse(@NonNull List<ProductDetails> productDetails) {
        // Show the products; on tap:
        // billingClient.launchBillingFlowForProductId(requireActivity(), "donate_small");
    }

    @Override
    public void handlePurchase(@NonNull Purchase purchase) {
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            billingClient.consumePurchase(purchase);      // consumable
            // billingClient.acknowledgePurchase(purchase); // non-consumable
        }
    }

    @Override public void onBillingClientSetup() { }
    @Override public void handleUserCancelled(@Nullable List<Purchase> purchases) { }
    @Override public void handlePurchaseError(@Nullable List<Purchase> purchases, int responseCode,
                                              @Nullable String debugMessage) { }
    @Override public void onPurchaseConsumed(@NonNull String purchaseToken) { }
    @Override public void onConsumePurchaseError(@Nullable String purchaseToken, int responseCode) { }
    @Override public void onPurchaseAcknowledged(@NonNull String purchaseToken) { }
    @Override public void onAcknowledgePurchaseError(@Nullable String purchaseToken, int responseCode) { }
    @Override public void handleItemAlreadyOwned(@Nullable List<Purchase> purchases) { }
    @Override public void onQueryPurchasesResponse(@NonNull String productType,
                                                   @NonNull List<Purchase> purchases) { }
    @Override public void onQueryPurchasesError(@NonNull String productType, int responseCode) { }
}
```

### 4.2 mocks

The module was rewritten in Kotlin, and mock names now come from resources (English, Spanish
and French).

| 4.1 (Java) | 5.0 from Java | 5.0 from Kotlin |
|---|---|---|
| `MockContactDataUtils.getMockContact()` | `MockContactDataUtils.INSTANCE.getMockContact(context)` | `MockContactDataUtils.getMockContact(context)` |
| `MockSmsApiUtils.getMessageInfoList(n)` | unchanged | unchanged |
| `MockSmsApiUtils.getMessageInfo()` | removed — use `getMessageInfoList(1).get(0)` | `getMessageInfoList(1)[0]` |

`getMockContact` now needs a `Context` to read the names. In plain JVM unit tests, stub
`context.getResources().getStringArray(...)`, or use Robolectric.

### 4.3 database-helpers

- **`DataBaseHelper` is deprecated.** It still works and its API is unchanged, but new code
  should use Room. It will be removed in a future major version.
- **`CopiedDataBaseHelper` was rewritten in Kotlin.** The constructor and the public methods
  `createDataBase()`, `openDataBase()` and `close()` are unchanged. Two things changed for
  subclasses:
  - `checkDataBase()` is now private and cannot be called or overridden.
  - `copyDataBase()` and `query(...)` are still protected but can no longer be overridden.

### 4.4 multi-contact

`ContactPickerFragment` now uses the Activity Result API. `pickContact()` and
`onContactPicked(Uri)` are unchanged. If your subclass overrode `onActivityResult` to intercept
the picker result (request code 2020), that override is no longer called; handle the result in
`onContactPicked` instead.

`MultiContactBaseActivity` and its layout are unchanged. Its resource IDs now live in
`com.ae.apps.lib.multi_contact.R` (see [section 3](#3-resource-r-classes)).

### 4.5 core

The in-app review implementation moved from the monolithic **Play Core** library
(`com.google.android.play:core:1.9.1`) to **`com.google.android.play:review:2.0.1`**. The
`AppReview` API is unchanged.

- If your app also depends on `com.google.android.play:core`, migrate it to the split Play
  libraries (`review`, `app-update`, `feature-delivery`, …). Mixing the monolithic library with
  the split ones causes duplicate-class build errors.
- If your app used Play Core features (such as in-app updates) only because 4.1 brought the
  library in transitively, add the specific Play library you need yourself.

### 4.6 contacts-api

Source compatible. One addition:

- `ContactsDataConsumer` has a new method, `onContactsReadFailed(Exception)`, with an empty
  default implementation. Existing implementations compile unchanged; implement it to show an
  error when contacts cannot be read.

### 4.7 sms-api, runtime-permissions, utilities

Source compatible. See [section 5](#5-behaviour-changes) for fixes that change results.

---

## 5. Behaviour changes

These compile without edits but can change what your app sees. Most are bug fixes.

**contacts-api**

- `initialize()` and `initializeAsync()` are now safe to call several times and from several
  threads. A second call made while contacts are still loading **waits** for that load instead
  of returning immediately. This fixes the intermittent
  `IllegalStateException: ContactsApiGateway not initialized`, but it also means **calling
  `initialize()` on the main thread while an async load is running blocks the main thread**
  until the load finishes. Prefer `initializeAsync`.
- `initializeAsync` now calls `onContactsRead()` only after the contacts are loaded. If the read
  fails it calls `onContactsReadFailed()` instead of crashing on a background thread, and the
  gateway can be initialized again later.
- `getContactInfo(id, options)` returns `null` for an id that was not read, instead of throwing
  `NullPointerException`. Check the result.

**sms-api**

- `MessageInfo.getDate()`, `getType()` and `getProtocol()` are now filled in. In 4.1, `date` was
  always `0` and `type` always `null` for real messages.

**utilities**

- `CommonUtils.isPackageInstalled(packageName, context)` now checks the package you pass. In 4.1
  it checked your own app and **always returned `true`**. On Android 11 and later, declare the
  packages you query in a `<queries>` element in your manifest, or the call returns `false`.
- `IntentUtils.getUriIntent(context, url)` now sets `ACTION_VIEW`.

**runtime-permissions**

- `PermissionCheckingFragment` now treats the request as granted only if **every** requested
  permission is granted; 4.1 looked only at the first.
- `AbstractPermissionsAwareActivity.invokeRequestPermissions()` calls `onPermissionsGranted()`
  directly on Android 5.x, where permissions are granted at install time.

**multi-contact**

- Searching in the picker no longer crashes on Android versions below 15, and filtering is
  thread-safe.

**core**

- `SimpleGraphView` draws correctly on every redraw and no longer crashes when inflated from XML.
- `RoundedImageView` no longer crashes when given a non-bitmap drawable such as a vector; it
  draws it unclipped.
- `ToolBarBaseActivity.displayHomeAsUp()` no longer crashes when there is no action bar.

**database-helpers**

- Two `CopiedDataBaseHelper` instances for different database files no longer overwrite each
  other's path.

---

## 6. From a 5.0 pre-release

If you are on a `v5.0.0-alpha.*` or `v5.0.0-beta.*` build, sections 1–3 already apply to you.
Moving to `v5.0.1` brings:

- **Everything in [section 5](#5-behaviour-changes).** All of those fixes landed after
  `v5.0.0-beta.03`, except the `contacts-api` initialization fix, which is also in
  `v5.0.0-beta.04`.
- **`billing-client`**: builds before `v5.0.0-alpha.19` still have the 4.1 Java client on Play
  Billing 3. Follow [section 4.1](#41-billing-client--rewrite-required).
- **`mocks` and `database-helpers`**: the Kotlin rewrites arrived in `v5.0.0-alpha.14`. Earlier
  alphas need [4.2](#42-mocks) and [4.3](#43-database-helpers).
- **`sms-api` on `v5.0.0-beta.01`** was published as `sms_api` by mistake. Use `sms-api`.
- **Dependency scopes**: AppCompat, RecyclerView, Fragment and Play Billing are now compile
  dependencies where the API exposes them. Nothing to change.

---

## 7. Checklist

- [ ] `minSdk` ≥ 21, JDK 17, AGP 8 or newer
- [ ] `jcenter()` removed; JitPack repository present
- [ ] Version set to `v5.0.1`
- [ ] Fully qualified `com.ae.apps.lib.R` (and other library `R`) references updated
- [ ] `billing-client`: handler re-implemented, purchases consumed or acknowledged,
      `queryPurchasesAsync()` called on resume
- [ ] `mocks` / `CopiedDataBaseHelper` call sites updated, if used
- [ ] `com.google.android.play:core` replaced with split Play libraries, if used
- [ ] `isPackageInstalled` callers checked, and `<queries>` added for Android 11+
- [ ] No `initialize()` on the main thread while `initializeAsync()` may be running
