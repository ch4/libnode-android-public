Terms of Use
---
Use of this API is subject to the [Variable Terms of Use](http://variableinc.com/terms-use-license/).

About this Repo
---
This repo contains the [NODE](http://nodesensors.com) Android library, as well as an open source example application.
This jar can be consumed via gradle, maven, or just downloaded. 




NODE+ Application Requirements
---
The library and Bluetooth implementation has been tested on the following devices:
	
* Samsung S3 & S4  & S5
* HTC One X (Developer Edition)
* HTC One X (Consumer Edition)
* Nexus 7 (1st Gen & 2nd Gen)
* Droid X (1st Gen) [This API Example Only]
* Samsung Galaxy S2
* Galaxy Note 3


NODE.Framework Requirements
---    
* Android API 2.3+
* Support for Bluetooth SPP Profile and RfComm
    


Build Requirements
---
* Android 4.4.2 API level 19 or above
* Android buildTools 19.1+
* JRE / JDK 1.7  



Troubleshooting
----

* An application compiles and will push to the device.
However, the application throws an error with the message "Unable to Instantiate Activity. . ." 
Fix: You may need to check the version of Java that you are building IDE is using (Needs to be Version 1.7).


* Upon importing code into Android Studio, an IllegalStateException is thrown. 
Most of the time, recompiling will get fix this error.  


Other Documentation
---  
[Importing Project (Android Studio)](https://bitbucket.org/variabletech/libnode-android-public/wiki/Project%20Setup%20%28Android%20Studio%29)

[Project Configurations](https://bitbucket.org/variabletech/libnode-android-public/wiki/Project%20Configuration)

[JavaDocs](http://variabletech.bitbucket.org/framework/android/doc)

[Framework Release Notes & Changes](https://bitbucket.org/variabletech/libnode-android-public/src/8136fca96066e1296f1aeb3bbe69fb44e21e9366/api%20example/?at=master)