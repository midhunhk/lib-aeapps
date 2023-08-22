# lib-AeApps
[![](https://jitci.com/gh/midhunhk/lib-aeapps/svg)](https://jitci.com/gh/midhunhk/lib-aeapps)
[![Release](https://jitpack.io/v/midhunhk/ae-apps-library.svg)](https://jitpack.io/#midhunhk/lib-aeapps)
[![GitHub commits](https://img.shields.io/github/commits-since/midhunhk/lib-aeapps/v4.1.0.svg)](https://github.com/midhunhk/lib-aeapps) 
[![Issues](https://img.shields.io/github/issues/midhunhk/lib-aeapps.svg)](https://github.com/midhunhk/lib-aeapps/issues) 

lib-AeApps is an Android library project which provides reusable solutions to some common problems.
Some situations require boiler plate code which can become tedious to manage.

This is named after **Artistik Expressionz** since all my apps follow the package structure "com.ae.apps.[appname]".
[Codenames](https://github.com/midhunhk/lib-aeapps/wiki/Codenames) for development versions are based on fruits, mostly berries.  
The latest codename is *Lingonberry*

## Contents
Detailed information about the architecture of the project is available on the project wiki at [this link](https://github.com/midhunhk/lib-aeapps/wiki/Architecture)
Depending on the features you use, make sure to declare the permissions in your manifest.xml file.

## Implementations
Presently the following android app projects make use of this library, and are supported.

* <a href="https://github.com/midhunhk/message-counter">Message Counter</a>
* <a href="https://github.com/midhunhk/random-contact">Random Contact</a>
* <a href="https://github.com/midhunhk/trip-o-meter">Trip 'O Meter</a>

## Changelog
To see the history of changes, see [Changelog](https://github.com/midhunhk/lib-aeapps/blob/master/VersionHistory.md)

## Using the library
* For version 4, the library has been split into multiple modules, each of which could be included separately in an android app.
* For Version 5, we migrated to maven-publish plugin and upgraded to AGP
* Instructions for using this library are mentioned at [Distribution](https://github.com/midhunhk/lib-aeapps/wiki/Distribution)

## Migration Guide

| Version   	| Upgrade to 	| Guide 	|
|-----------	|------------	|-------	|
| Before v4 	| v4.1       	| [Migration Guide](https://github.com/midhunhk/lib-aeapps/wiki/Migration-Guide)    	|
| Before v5 	| v5.0       	| TBD    	|

## License
Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
  
 http://www.apache.org/licenses/LICENSE-2.0
  
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
