About this Repo
===============
This repo contains the [NODE](http://nodesensors.com) Android library, as well as an open source example application.

Documentation
=============
Documentation can be found [here](http://variabletech.bitbucket.org/framework/android/doc)

About the Demo Application
===============
The library demo application located in this repo uses a compiled JAR, which can be 
downloaded separately from the 
[downloads section](https://bitbucket.org/variabletech/libnode-android-public/downloads). 
The demo application shows how to capture and display Motion, Clima, Oxa, Chroma and Therma data from a NODE device. 

Compatible Devices
==================
The library and bluetooth implementation has been tested on the following devices:
	
	* Samsung S3 & S4 
	
	* HTC One X (Developer Edition)
	
	* HTC One X (Consumer Edition)
	
	* Nexus 7 (1st Gen & 2nd Gen)
	
	* Droid X (1st Gen) [This API Example Only]
	
	* Samsung Galaxy S2

However, the NODE Android library is compatible with Bluetooth-enabled Android devices running Android version 2.3 or higher. 

Build Requirements
===================
* Android 4.3 API level 18 or above
* JRE / JDK 1.7

License
=============
Copyright (c) 2012-2013, Variable Technologies LLC
All rights reserved. 

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met: 

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer. 

Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. 

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.


Revision History
==================
### Version 0.0.5 
Initial Import

Added Classic Bluetooth Capability (LE is not supported)

Supports NODE Revision 2 Only

Added [Java Docs](https://variabletech.bitbucket.org/framework/android/doc)


Troubleshooting
==================

* An application compiles and will push to the device.
However, the application throws an error with the message "Unable to Instantiate Activity. . ." 
Fix: You may need to check the version of Java that you are building IDE is using (Needs to be Version 1.7).


* Upon importing code into Android Studio, an IllegalStateException is thrown. 
Most of the time, recompiling will get fix this error.