ae apps library
===============
This Android library project contains some reusable code that can be used across projects. Depending on the features you use, make sure to declare the permissions in your manifest.xml file. Whenever I come across any piece of code that seems reusable, it will be moved to this library project.

Name of Library
===============
This library is named "ae apps" library mainly because I develop apps with the package structure "com.ae.apps". Here ae represents **Artistik Expressionz**.

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
