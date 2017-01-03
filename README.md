Ae-Apps-Lib
===============
This Android library project contains some reusable code that can be used across projects. Depending on the features you use, make sure to declare the permissions in your manifest.xml file. Whenever I come across any piece of code that seems reusable, it will be moved to this library project.

Naming
======
The name ae represents **Artistik Expressionz**. My apps follow the package structure "com.ae.apps.[appname]".

Contents
========
The following contents are available as part of this library.
* Base Activity for Donations (Google Play In-App Billing v3)
* Base Activity for Toolbar
* DataBaseHelper
* CopiedDataBaseHelper for using a database file from app's assets folder
* Contact Manager for accessing Android's Contacts API
* SMS Manager for accessing Android's SMS API
* Utility methods
* SimpleGraphView draws a pie chart
* RoundedImageView shows an image inside a Circle

Implementation
==============
Presently the following android app projects written by me make use of this library.
* <a href="https://github.com/midhunhk/message-counter">Message Counter</a>
* <a href="https://github.com/midhunhk/random-contact">Random Contact</a>

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
