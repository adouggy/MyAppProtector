#!/bin/sh


ant clean

ant release

adb uninstall net.synergyinfosys.android.myappprotector

adb install bin/MainActivity-release.apk 

adb shell am start net.synergyinfosys.android.myappprotector/net.synergyinfosys.android.myappprotector.MainActivity
