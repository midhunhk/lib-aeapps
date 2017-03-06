libAeApps
===============
This Android library project contains some reusable code that can be used across projects. Depending on the features you use, make sure to declare the permissions in your manifest.xml file. Whenever I come across any piece of code that seems reusable, it will be moved to this library project.

Naming
======
The library name represents **Artistik Expressionz** since all my apps follow the package structure "com.ae.apps.[appname]". It is short and all my design works come under same name.

The library [codenames](https://github.com/midhunhk/ae-apps-library/wiki/Codenames) are based on fruits. The latest release code name is **Damson**.

Damson is a kind of plum. Originally, the fruit comes from the Mediterranean. The Damson is generally less sweet than other kinds of plum and is used in jams, jellies, and liqueurs.

Contents
========
The following components are available as part of this library as of now.

* Activities
  * Base Activity for Donations (Google Play In-App Billing v3)
  * Base Activity for ToolbarActivity
* Database  
  * DataBaseHelper
  * CopiedDataBaseHelper for using a database file from app's assets folder
* Managers  
  * Contact Manager for accessing Android's Contacts API
  * SMS Manager for accessing Android's SMS API  
* Utility Components
  * SimpleGraphView draws a pie chart
  * RoundedImageView shows an image inside a Circle
  * Mock Contact Data

Implementation
==============
Presently the following android app projects make use of this library.

* <a href="https://github.com/midhunhk/message-counter">Message Counter</a>
* <a href="https://github.com/midhunhk/random-contact">Random Contact</a>
* <a href="https://github.com/midhunhk/trip-o-meter">Trip 'O Meter</a>

Changelog
===============
To see the history of changes, see [Changelog](https://github.com/midhunhk/ae-apps-library/blob/master/VersionHistory.md)

License
=======
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
  
 http://www.apache.org/licenses/LICENSE-2.0
  
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
