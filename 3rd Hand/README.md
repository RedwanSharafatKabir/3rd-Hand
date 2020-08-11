# 3rd-Hand
### This is an android application for any people who are in trouble like accidently left their important files at home or anywhere and forget to bring those to their workplace or another destination and they are immediately in need of that package, immediate shopping or medical service. To get any of these instant services this software will help to reduce the customer's waste of time and they will have faithful agents.<br><br>

#### App Functionalities -

•	Sign in<br>•	Sign up<br>•	Enter the forgotten equipment<br>•	Enter place and location where customer left his/her product package<br>•	Call nearby agent of the product’s location <br>•	Agent receives call and confirms service<br>•	Bring and handover the product to it’s owner<br>•	Automatically estimate cost according to service route from left product to current owner’s location.<br>•	Giving feedback or review the agent’s work and behavior.<br><br>

#### Hardware Interface -

•	Android version must be minimum Jelly Bean (API 16) and recommended Pie (API 28) and higher.<br><br>

#### Coding Interfaces -

•	Java<br>•	XML<br>•	JSON<br><br>

#### User-permission and Meta-Data in AndroidManifest.xml -

*  uses-permission android:name="android.permission.INTERNET"
*  uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"
*  uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"
*  uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"<br>

*  meta-data<br>&emsp;&emsp;android:name="com.google.android.gms.version"<br>&emsp;&emsp;android:value="@integer/google_play_services_version"<br><br>

#### Applied Plugins -

* apply plugin: 'com.google.gms.google-services'<br><br>

#### Defined classpath -

* classpath 'com.google.gms:google-services:4.3.3'<br><br>

#### Google Map and Places API dependencies -

* implementation 'com.google.android.gms:play-services-maps:17.0.0'
* implementation 'com.google.android.gms:play-services-location:17.0.0'
* implementation 'com.google.android.gms:play-services-places:17.0.0'<br><br>

#### Firebase Database dependencies -

* implementation 'com.google.firebase:firebase-auth:19.3.1'
* implementation 'com.google.firebase:firebase-database:19.2.1'
* implementation 'com.google.firebase:firebase-storage:19.1.1'
* implementation 'com.google.firebase:firebase-analytics:17.2.3'
* implementation 'com.firebaseui:firebase-ui-database:6.2.0'
* implementation 'com.firebase:geofire-android:3.0.0'<br><br>

#### Design and Support dependencies -

* implementation 'androidx.legacy:legacy-support-v4:1.0.0'
* implementation 'com.android.support:design:29.1.1'
* implementation 'com.android.support:cardview-v7:29.1.1'
* implementation 'com.android.support:appcompat-v7:29.1.1'
* implementation 'com.google.android.material:material:1.0.0'
* implementation 'de.hdodenhof:circleimageview:3.0.1'
* implementation 'cc.cloudist.acplibrary:library:1.2.1'
* implementation 'com.daimajia.easing:library:2.0@aar'
* implementation 'com.daimajia.androidanimations:library:2.3@aar'
* implementation 'com.github.d-max:spots-dialog:1.1@aar'
* implementation 'com.github.glomadrian:MaterialAnimatedSwitch:1.1@aar'
* implementation 'com.android.support:multidex:1.0.3'<br><br>

#### Phone number verification dependency -

* implementation 'com.googlecode.libphonenumber:libphonenumber:8.9.7'
