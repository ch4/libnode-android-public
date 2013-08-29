About this Repo
===============
This repo contains all bluetooth source for connecting between an Android device and a NODE. It additionally contains
the compiled java framework code. That is used to expose functionality given by NODE.

About the Library Demo
=======================
The library demo application located in this repo, uses the precompiled jar, which can be downloaded in the downloads section. The library demo displays how to stream clima, motion, oxa, and therma. In addition to, capture color from Chroma.

Compatible Devices
==================
This framework is compatible with Android device 2.3+. 
The device must also have bluetooth 2.1 capabilities.

Repository Requirements
===================
* Code Must be Compiled with Android 4.3 API Level 18 or greater.
* JRE / JDK 1.7


License
=============
Copyright (c) 2012, Variable Technologies LLC
All rights reserved. 

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met: 

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer. 

Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. 

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.


Revsion Number
==================
### Version 0.0.5 
Initial Import
Added Classical Bluetooth Capability (LE Not Supported)
Supports NODE Revision 2 Only


Troubleshooting
==================

* An application compiles and will push to the device.However, the application throws an error with message "Unable to Instantiate Activity. . .", you may need to check the version of Java the IDE is using (Needs to be Version 1.7).

* Upon importing code into Android Studio, an IllegalStateException is thrown. Most of the time, recompile the code and this error will go away.