# Version History

## Version 5.0 (Lingonberry)
#### Version 5.0.0 [Jun 2023]
 - breaking changes to API
 - migrate android gradle plugin version
 - migrate to publish library
 - build structure changes
 - billing-client: migrate billing library #35

## Version 4.1 (Kiwiberry)
#### Version 4.1.0 [Feb 2021]
 - runtime-permissions: AbstractPermissionsAwareActivity #32
 - runtime-permissions: Code Cleanup #33
 - billing-client: upgrade android billing library #29
 - core: more color resources
 - utilities: launchWebPage method
 - contacts-api: Option to filter duplicate phone numbers
 - contacts-api: Use an LRU Cache for Default Contact Pictures to improve performance
 - contacts-api: Removed TIMES_CONTACTED and LAST_TIME_CONTACTED since they have been made obsolete by Google
 - core: add an implementation for In-App Reviews

## Version 4.0 (Juneberry)
#### Version 4.0.7 [Apr 2020]
 - core: More resources

#### Version 4.0.6 [Apr 2020]
 - Update proguard for all modules

#### Version 4.0.5 [Sep 2020]
 - Fixes #31

#### Version 4.0.4 [Apr 2020]
 - Complete rewrite of library and change in architecture
 - Add filter for Multi Contact Picker
 - Sample app to test and showcase the features

## Version 3.0 (Indian Plum)
#### Version 3.0.2 [Aug 2018]
 - Remove permission requests from manifest
 
#### Version 3.0.1 [Aug 2018]
 - Bug Fixes
 
#### Version 3.0.0 [Jul 2018]
 - Remove DonationsActivity
 - Add Runtime Permission Checker
 
## Version 2.3 (Huckleberry)
#### Version 2.3.0 [Jul 2018]
 - More ContactUtil methods
 - Added PermissionCheckingFragment

## Version 2.2 (Grapefruit)
#### Version 2.2.2 [Nov 2017]
 - Reset flagEndAsync before new purchase flow
 - Downgrade support library version for Message Counter
 
#### Version 2.2.1 [Nov 2017]
 - Add MultiContactPicker Activity
 - Add ContactManagerProxy class
 - Add animation resources
 - Add reusable properties
 
#### Version 2.2.0 [Oct 2017]
 - Fix flagStartAsync not getting reset after Donation
 - Generate Java docs for library 
 
## Version 2.1 (Fig)
#### Version 2.1.0 [July 2017]
 - Deploy library to jcenter

## Version 2.0 (Elderberry)
#### Version 2.0.1 [June 2017]
- Refactor ContactManager implementation
- Unit test cases

#### Version 2.0.0 [May 2017]
- Updates to ContactManager
- Add contacts without contact numbers
- Integration with Travis CI

## Version 1.5 (Damson)
#### Version 1.5.3 [Feb 2017]
- Added ContactPickerFragment
- Updated ContactVO

#### Version 1.5.2 [Jan 2017]
- Added delete operation in DatabaseHelper  

## Version 1.5 (Cranberry)
#### Version 1.5.1 [Nov 2016]
- Convert project to Android Studio Gradle format  

#### Version 1.5.0 [Dec 2015]
- Remove duplicate phone numbers for contacts  
- Added come common views, colors and dimensions

## Version 1.4 (Blueberry)
#### Version 1.4.0[May 2015]
- Added ToolBarBaseActivity  
- Added DonationsBasectivity  
- Added appcompat as dependent library  

## Version 1.3 (Blueberry)
#### Version 1.3.0 [Apr 2015]
- Added In-App billing aidl and utils  

## Version 1.2 (Blueberry)
#### Version 1.2.0 [May 2013]
- Added MockContactDataUtils  
- Initial project added to Git

## Version 1.1 (Apricot)
#### Version 1.1.0 [Apr 2014]
- Initial project added to VCS
