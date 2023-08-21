## Sample App for libAeApps
This is a sample Android app that uses features provided by libAeApps

### Features Provided

### Notes
Since Google Play has restrictions on the usage of READ_SMS permission, those changes are not enabled
in this app, but can be enabled if you checkout the project and modify the below files.
 - AndroidManifest.xml
   - Uncomment the READ_SMS permission declaration
 - SmsSampleActivity.java
   - Uncomment the `init()` method invocation in `onCreate()`