# Migrating from lib-aeapps 3.x to 4.x

This guide is for apps that use **3.x (Indian Plum and earlier)** and are moving to
**4.x (Juneberry 4.0, Kiwiberry 4.1)**. The target is 4.1.0, the last 4.x release.

Version 4.0 was a complete rewrite. The single `android-libraries` artifact was split into
separate modules, every package moved from `com.ae.apps.common` to `com.ae.apps.lib`, and the
contacts and SMS "managers" were replaced by API gateways. Expect to touch every import.

After finishing this guide, continue with [MIGRATION-v5](MIGRATION-v5.md) to reach 5.0.

| Area | Impact | Section |
|---|---|---|
| Build requirements (AndroidX) | Every app | [1](#1-build-requirements) |
| Dependency coordinates and modules | Every app | [2](#2-dependency-coordinates-and-modules) |
| Package names | Every import | [3](#3-package-names) |
| Models (`*Vo` → `*Info`) | Every app | [4.1](#41-models) |
| Contacts and SMS | Rewrite of call sites | [4.2](#42-contacts--contactmanager-to-contactsapigateway), [4.3](#43-sms--smsmanager-to-smsapigateway) |
| Runtime permissions | Small interface changes | [4.4](#44-runtime-permissions) |
| Resources | Renamed animations, dimensions and colours | [5](#5-resources) |

---

## 1. Build requirements

| | 3.x | 4.1 |
|---|---|---|
| Support libraries | Android Support Library (`com.android.support:*`) | **AndroidX** (`androidx.*`) |
| Minimum Android version (`minSdk`) | 16 | 16 |
| Library compiled against | 25 | 29 |
| Project format | Android Studio / Gradle | Android Studio / Gradle |

- **Migrate your app to AndroidX** first. In Android Studio use
  *Refactor ▸ Migrate to AndroidX*, then make sure `gradle.properties` contains
  `android.useAndroidX=true`. 4.x will not work alongside the old support libraries.
- **`compileSdk` must be at least 29**, the version the library is compiled against.

---

## 2. Dependency coordinates and modules

### Coordinates

3.x was a single artifact published to JCenter (Bintray) and JitPack:

```groovy
implementation 'com.ae.apps:android-libraries:3.0.2'           // JCenter
implementation 'com.github.midhunhk:ae-apps-library:V3.0.2'     // JitPack
```

4.x is consumed from JitPack, as one artifact per module:

```groovy
// root build.gradle
allprojects {
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}

// app/build.gradle
def libAeAppsVersion = '4.1.0'
implementation "com.github.midhunhk.lib-aeapps:core:$libAeAppsVersion"
implementation "com.github.midhunhk.lib-aeapps:contacts-api:$libAeAppsVersion"
```

Remove the Bintray repository (`dl.bintray.com/midhunhk/android-libraries`); Bintray has been
shut down.

### Modules

Add only the modules you use. A module brings in the modules it depends on (for example
`contacts-api` brings `core` and `utilities`). The dependencies between modules are described on
the [Architecture](https://github.com/midhunhk/lib-aeapps/wiki/Architecture) wiki page.

| Module | Contains |
|---|---|
| `core` | Models, custom views, `ToolBarBaseActivity`, shared resources, in-app review |
| `utilities` | `CommonUtils`, `ContactUtils`, `DialogUtils`, `MobileNetworkUtils`, intents |
| `contacts-api` | `ContactsApiGateway` — replaces `ContactManager` |
| `sms-api` | `SmsApiGateway` — replaces `SMSManager` |
| `runtime-permissions` | `RuntimePermissionChecker`, permission-aware Activity and Fragment |
| `multi-contact` | Multi-select contact picker, `ContactPickerFragment` |
| `database-helpers` | `DataBaseHelper`, `CopiedDataBaseHelper` |
| `billing-client` | `AeBillingClient`, a Google Play Billing wrapper (4.0 shipped `BaseBillingClientFragment` instead) |
| `mocks` | Mock data for samples and unit tests |

`mocks` is meant for tests and demos. Prefer `testImplementation` or `debugImplementation` for it.

---

## 3. Package names

All classes moved from `com.ae.apps.common` to `com.ae.apps.lib`. The change separates library
code clearly from app code, which used the same `com.ae.apps` prefix.

| 3.x class | 4.1 class | Module |
|---|---|---|
| `common.activities.ToolBarBaseActivity` | `lib.activities.ToolBarBaseActivity` | `core` |
| `common.views.EmptyRecyclerView` | `lib.custom.views.EmptyRecyclerView` | `core` |
| `common.views.RoundedImageView` | `lib.custom.views.RoundedImageView` | `core` |
| `common.views.SimpleGraphView` | `lib.custom.views.SimpleGraphView` | `core` |
| `common.views.ColorThemes` | `lib.custom.views.ColorThemes` | `core` |
| `common.vo.ContactVo` | `lib.common.models.ContactInfo` | `core` |
| `common.vo.MessageVo` | `lib.common.models.MessageInfo` | `core` |
| `common.vo.PhoneNumberVo` | `lib.common.models.PhoneNumberInfo` | `core` |
| `common.vo.ContactMessageVo` | removed | — |
| `common.utils.CommonUtils` | `lib.common.utils.CommonUtils` | `utilities` |
| `common.utils.ContactUtils` | `lib.common.utils.ContactUtils` | `utilities` |
| `common.utils.DialogUtils` | `lib.common.utils.DialogUtils` | `utilities` |
| `common.utils.MobileNetworkUtils` | `lib.common.utils.MobileNetworkUtils` | `utilities` |
| `common.utils.ValueComparator` | `lib.common.utils.ValueComparator` | `utilities` |
| `common.utils.IntentUtils` | `lib.common.utils.intents.IntentUtils` | `utilities` |
| — | `lib.common.utils.intents.EmailIntentBuilder` (new) | `utilities` |
| `common.managers.ContactManager` | `lib.api.contacts.ContactsApiGateway` | `contacts-api` |
| `common.managers.contact.ContactDataConsumer` | `lib.api.contacts.types.ContactsDataConsumer` | `contacts-api` |
| `common.managers.contact.AeContactManager`, `AbstractContactManager`, `ContactManagerProxy` | removed — see [4.2](#42-contacts--contactmanager-to-contactsapigateway) | — |
| `common.services.ContactService`, `AeContactService` | removed | — |
| `common.managers.SMSManager` | `lib.api.sms.SmsApiGateway` | `sms-api` |
| `common.permissions.RuntimePermissionChecker` | `lib.permissions.RuntimePermissionChecker` | `runtime-permissions` |
| `common.permissions.PermissionsAwareComponent` | `lib.permissions.PermissionsAwareComponent` | `runtime-permissions` |
| `common.permissions.PermissionCheckingFragment` | `lib.permissions.PermissionCheckingFragment` | `runtime-permissions` |
| `common.activities.multicontact.*` | `lib.multicontact.*` | `multi-contact` |
| `common.fragments.ContactPickerFragment` | `lib.multicontact.ContactPickerFragment` | `multi-contact` |
| `common.db.DataBaseHelper` | `lib.db.DataBaseHelper` | `database-helpers` |
| `common.db.CopiedDataBaseHelper` | `lib.db.CopiedDataBaseHelper` | `database-helpers` |
| `common.mock.MockContactDataUtils` | `lib.mock.MockContactDataUtils` | `mocks` |
| `common.mock.MockContactService` | removed | — |

All names in the table are relative to `com.ae.apps`.

Custom views referenced from layout XML need the new fully qualified name:

```xml
<!-- 3.x -->
<com.ae.apps.common.views.EmptyRecyclerView … />
<!-- 4.x -->
<com.ae.apps.lib.custom.views.EmptyRecyclerView … />
```

---

## 4. API changes

### 4.1 Models

The value objects were renamed to models.

**`ContactVo` → `ContactInfo`**

| 3.x | 4.1 |
|---|---|
| `getHasPhoneNumber()` | `hasPhoneNumber()` |
| `getPhoneNumbersList()` returns `List<PhoneNumberVo>` | returns `List<PhoneNumberInfo>` |
| `getTimesContacted()`, `getLastContactedTime()` | removed — Android no longer provides this data |
| `isMockUser()`, `getMockProfileImageResource()` | removed |
| — | `getPicture()` / `setPicture(Bitmap)` (new) |

**`PhoneNumberVo` → `PhoneNumberInfo`**: same fields, plus `getUnformattedPhoneNumber()`, the
number with all non-digits removed.

**`MessageVo` → `MessageInfo`**: same fields under the new name.

### 4.2 Contacts — `ContactManager` to `ContactsApiGateway`

The old implementation did not manage contacts; it read them. In 4.x it is an *API gateway*, an
abstraction over Android's Contacts API. The gateway reads and caches all contacts once, when
you call one of the initialize methods. Call one before any other method; the others throw
`IllegalStateException` until the gateway is initialized.

```java
// 3.x
ContactManager contactManager = new ContactManager.Builder(getContentResolver(), getResources())
        .addContactsWithPhoneNumbers(true)
        .build();
contactManager.fetchAllContactsAsync(this);

// 4.x
ContactsApiGateway contactsApi = new ContactsApiGatewayImpl.Builder(context).build();
contactsApi.initializeAsync(ContactInfoFilterOptions.of(true), this);
```

| 3.x `ContactManager` | 4.1 `ContactsApiGateway` |
|---|---|
| `new ContactManager.Builder(contentResolver, resources)` | `new ContactsApiGatewayImpl.Builder(context)` |
| `.addContactsWithPhoneNumbers(true)` | `ContactInfoFilterOptions.of(true)`, passed to the initialize call |
| `fetchAllContacts()` | `initialize(ContactInfoFilterOptions)` |
| `fetchAllContactsAsync(ContactDataConsumer)` | `initializeAsync(ContactInfoFilterOptions, ContactsDataConsumer)` |
| `getAllContacts()` | `getAllContacts()` |
| `int getTotalContactCount()` | `long getReadContactsCount()` |
| `getRandomContact()` | `getRandomContact()` — includes phone numbers and picture |
| `getContactInfo(id)` | `getContactInfo(id)` |
| `getContactWithPhoneDetails(id)` | `getContactInfo(id, options)` with `includePhoneDetails(true)` |
| `getContactPhoto(id)`, `getContactPhoto(id, defaultImage)` | `getContactInfo(id, options)` with `includeContactPicture(true)` and optionally `defaultContactPicture(resId)`, then `getPicture()` |
| `long getContactIdFromRawContactId(rawId)` | `String getContactIdFromRawContact(rawId)` |
| `getContactIdFromAddress(address)` | `getContactIdFromAddress(address)` |
| `getContactMessages(id)` | moved to `sms-api`: `SmsApiGateway.getMessagesForContact(uri, id)` |

Requesting optional details:

```java
ContactInfoOptions options = new ContactInfoOptions.Builder()
        .includePhoneDetails(true)
        .includeContactPicture(true)
        .defaultContactPicture(com.ae.apps.lib.R.drawable.profile_icon_3)
        .filterDuplicatePhoneNumbers(true)   // new in 4.1
        .build();
ContactInfo contact = contactsApi.getContactInfo(contactId, options);
```

`ContactsApiGatewayFactory` (in `contacts-api` `types`) can be used to swap in a fake gateway in
tests.

### 4.3 SMS — `SMSManager` to `SmsApiGateway`

The old name clashed with Android's own `android.telephony.SmsManager` and the class did not
manage SMS, so it was renamed and rebuilt as an API gateway. Your app must still declare
`READ_SMS`, which Google Play restricts to default SMS apps and a few approved uses.

```java
// 3.x
SMSManager smsManager = new SMSManager(context);
int count = smsManager.getMessagesCount(SMSManager.SMS_URI_INBOX);

// 4.x
SmsApiGateway smsApi = new SmsApiGatewayImpl(context);
long count = smsApi.getMessageCountForUri(SmsApiConstants.URI_INBOX);
```

| 3.x `SMSManager` | 4.1 `SmsApiGateway` |
|---|---|
| `new SMSManager(context)` | `new SmsApiGatewayImpl(context)` |
| `SMS_URI_ALL`, `SMS_URI_INBOX`, `SMS_URI_SENT`, `SMS_URI_DRAFTS` | `SmsApiConstants.URI_ALL`, `URI_INBOX`, `URI_SENT`, `URI_DRAFTS` |
| `int getMessagesCount(uri)` | `long getMessageCountForUri(uri)` |
| — | `getMessagesForUri(uri)`, `getMessagesForContact(uri, contactId)`, `getMessageCountForContact(uri, contactId)` |
| `Map<String, Integer> getUniqueSenders()` | removed — group the results of `getMessagesForUri(uri)` by `getAddress()` |

Gateways throw `IllegalArgumentException` for a URI that is not one of the `SmsApiConstants`
values.

### 4.4 Runtime permissions

- **`PermissionsAwareComponent` has a new method**, `onPermissionsRequired()`, called when
  permissions are missing so the component can explain why they are needed. Implementations
  must add it.
- **`RuntimePermissionChecker` is created with a factory method from 4.1.** Replace
  `new RuntimePermissionChecker(this)` with `RuntimePermissionChecker.newInstance(this)`. The
  component must be a `Context`, such as an Activity. (4.0 still had the public constructor.)
- `PermissionCheckingFragment` keeps its four abstract methods; only its package changed.
- New base Activities that implement the request and result handling for you:
  `PermissionCheckingActivity` (4.0) and `AbstractPermissionsAwareActivity` (4.1).

### 4.5 Mocks

- `MockContactDataUtils.getMockContact()` returns `ContactInfo` instead of `ContactVo`.
- `getMockContactMessageList(Resources)` and `ContactMessageVo` were removed. Use
  `MockSmsApiUtils.getMessageInfoList(size)` for mock messages.

---

## 5. Resources

Library resources now come from the `core` module, and the multi-contact layouts from
`multi-contact`. Fully qualified references to the library's `R` class change from
`com.ae.apps.aeappslibrary.R` to `com.ae.apps.lib.R` for `core` resources. References through your
app's own `R` keep working.

### Animations

Renamed for consistency:

| 3.x | 4.x |
|---|---|
| `animator/fade_in.xml` | `animator/anim_fade_in.xml` |
| `animator/slide_in_top.xml` | `animator/anim_slide_in_top.xml` |

### Dimensions

3.x mixed purpose-named dimensions with the same values for both margins and padding. 4.x keeps
only a size scale; pick the nearest step for each old value.

| Removed in 4.x | Value | Nearest replacement |
|---|---:|---|
| `margin_small`, `padding_small`, `card_margin` | 6dp | `unit_m` (8dp) or `unit_s` (4dp) |
| `card_padding` | 3dp | `unit_s` (4dp) |
| `padding_horizontal` | 10dp | `unit_m` (8dp) |
| `margin_medium`, `padding_medium` | 12dp | `unit_l` (16dp) or `unit_m` (8dp) |
| `margin_large`, `padding_large` | 18dp | `unit_l` (16dp) |
| `margin_extra_large` | 32dp | `unit_xl` (32dp) |
| `margin_extra_extra_large` | 48dp | `unit_xxl` (48dp) |

The size scale, unchanged from 3.x:

| Dimension | Value |
|---|---:|
| `unit_xxs` | 1dp |
| `unit_xs` | 2dp |
| `unit_s` | 4dp |
| `unit_m` | 8dp |
| `unit_l` | 16dp |
| `unit_xl` | 32dp |
| `unit_xxl` | 48dp |

New in 4.x, for text: `text_hero` (56dp), `text_title1` (32sp), `text_title2` (24sp),
`text_title3` (16sp).

These values suit phone screens. Override them in `values-sw600dp` (or similar) for tablets.

### Colours

Inconsistently named colours were renamed with a `color_` prefix, and two were removed:

| 3.x | 4.x |
|---|---|
| `white` | `color_white` |
| `text_black` | `color_black` |
| `silver` | `color_silver` |
| `grey` (#C2C2C2) | removed — closest is `color_silver` (#C0C0C0) |
| `text_grey` (#666666) | removed — closest is `color_matterhorn_brown` (#535353) |

Unchanged: `color_red`, `color_bright_orange`, `color_grey_dark`, `color_grey_light`,
`color_almost_white`, `color_slate_blue_dark`, `color_lime_green`, `color_blue_dark`.

Added in 4.1:

| Colour | Value |
|---|---|
| `color_charcoal` | #222222 |
| `color_radical_red` | #FF3366 |
| `color_matterhorn_brown` | #535353 |
| `color_alabaster_white` | #F9F9F9 |
| `color_white_50_alpha_010` … `_090` | white at 10–90 % opacity |
| `color_black_900_alpha_010` … `_090` | black at 10–90 % opacity |

### Other resources

- **Removed:** `default_profile_image` (use `profile_icon_1` … `profile_icon_6`),
  `ic_action_search` (use `ic_search`), the `separator_line` layout, and the `AppTheme` style.
  Define a theme in your own app.
- **Added in 4.x:** vector icons `ic_add`, `ic_back`, `ic_bookmark`, `ic_close`, `ic_profile`,
  `ic_search`, `ic_share`, `ic_windows`, and the `months_in_a_year` string array.

---

## 6. Checklist

- [ ] App migrated to AndroidX; `compileSdk` ≥ 29
- [ ] Bintray repository removed; JitPack repository present
- [ ] Single `android-libraries` dependency replaced with the modules you use
- [ ] Imports changed from `com.ae.apps.common.*` to `com.ae.apps.lib.*`, including custom
      views in layout XML
- [ ] `*Vo` models replaced with `*Info` models
- [ ] `ContactManager` replaced with `ContactsApiGateway`; initialize called before use
- [ ] `SMSManager` replaced with `SmsApiGateway`
- [ ] `onPermissionsRequired()` added; `RuntimePermissionChecker.newInstance(…)` used
- [ ] Renamed animations, dimensions and colours updated
- [ ] Continue with [MIGRATION-v5](MIGRATION-v5.md)
