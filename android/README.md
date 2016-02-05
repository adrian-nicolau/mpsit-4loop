4loop Android Client
=====================

Demonstrates use of the Google Play services Location API to retrieve the last
known location for a device.
Demonstrates use of [Volley](http://developer.android.com/training/volley/index.html) for
transmitting network data.

Introduction
============

This sample shows a simple way of getting a device's last known location, which
is usually equivalent to the device's current location.
The accuracy of the location returned is based on the location
permissions you've requested and the location sensors that are currently active
for the device.

This sample uses
[Google Play services (GoogleApiClient)](ihttps://developer.android.com/reference/com/google/android/gms/common/api/GoogleApiClient.html)
and the
[FusedLocationApi] (https://developer.android.com/reference/com/google/android/gms/location/LocationServices.html).

To run this sample, **location must be enabled**.

Prerequisites
--------------

- Android API Level >v11
- Android Build Tools >v21
- Google Support Repository
- Actually tested only on **Nexus 4 and 7 [2013] with Lollipop**

Getting Started
---------------

This sample uses the Gradle build system. To build this project, use the
"gradlew build" command or use "Import Project" in Android Studio. Never tried it though.

Support
-------

Available upon beer.

License
-------

DIT (Dâmbovița Institute of Technology)
