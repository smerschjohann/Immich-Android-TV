# Immich Android TV

## Fork Notice
_THIS IS A FORK_ specifically tailored for use on a Nixplay frame.

It has the following changes:
- Added Android Settings to settings page
- Added motion sensor to turn the display on, set the wakelock in the settings. Default value: 15 minutes.
- Added POWER button on remote to turn of screen (after display turnoff time)
- Added abilitiy to use client certificates (put it in /storage/emulated/0/Android/data/nl.giejay.android.tv.immich/files/immich.p12 with password 'immich')
- Added the app as HOME-launcher app
- Removed all Firebase and Cast functionality
- Removed donation button as it is not working on the frame. Feel free to support the original author: https://github.com/giejay/Immich-Android-TV

## How to install

Disclaimer: This guide is based on the W10E model. Please note that improper handling or software errors may damage your device. You are the only one responsible for any damages caused by your modification to either hardware or software.

To open the device, you can use a credit card or the IFixit opener, which worked well and left no visible marks after reassembly.

Behind the display, there is a USB port on a circuit board. The display is lightly taped to the metal backing, which can be carefully removed for better access to the controller.

You may need to unscrew the board to connect a USB cable to the port. The frame will automatically power on when connected to a PC.

As soon as you have access to ADB over USB you can start replacing the software.

1. Disable the original software on the frame.
```bash
adb shell
$ su
$ pm disable com.kitesystems.nix.prod & pm disable com.kitesystems.nix.frame
```
2. Enable ADB over network to ensure that you can access the device later on, even without opening the frame again.
```bash
adb shell
$ su
$ setprop persist.adb.tcp.port 6666
```
3. install the app. Note: It must be a system app to access the motion sensor.
```bash
adb root && adb remount && adb push app-debug.apk /system/app/immich.apk && adb reboot
```
4. (optional) Copy client certificate to the frame (p12 password must be 'immich')
```bash
adb push immich-eltern.p12 /storage/emulated/0/Android/data/nl.giejay.android.tv.immich/files/immich.p12
```
5. Setup your credentials. You can use [scrcpy](https://github.com/Genymobile/scrcpy) to screen share the Nixplay screen to your PC. You can also send the Immich host information using adb (`adb shell input text https://demo.immich.app`)
6. Go to Android Settings (using settings menu or by `adb shell am start -a android.settings.SETTINGS`). Set the display to turn off after shortest time, disable screensaver if any.

*Enjoy!*


## original documentation

Immich is a self hosted backup solution for photos and videos. Current features include:

- Upload and view videos and photos
- Auto backup when the app is opened
- Selective album(s) for backup
- Multi-user support
- Album and Shared albums

More info here: https://github.com/immich-app/immich

This Android TV app will allow you to view those uploaded photos and videos. Current features
include:

| Features                                                                       | Status |
|:-------------------------------------------------------------------------------|--------|
| Sign in by phone (https://github.com/giejay/Immich-Android-TV-Authentication)  | Done   |
| Sign in by entering API key                                                    | Done   |
| Demo environment                                                               | Done   |
| Album fetching + Lazy loading                                                  | Done   |
| Showing the photos inside an album                                             | Done   |
| Showing people, random, recent or seasonal photos                              | Done   |
| Slideshow of the photos and videos with a configured interval                  | Done   |
| Setting the app as the screensaver                                             | Done   |
| Setting the albums to show in the screensaver                                  | Done   |
| Configure the interval of the screensaver                                      | Done   |
| Add generic sorting of albums and photos                                       | Done   |
| Add sorting for specific album (select last item in row and press right again) | Done   |
| Showing the 4K thumbnail instead of the full image to speed up loading         | Done   |
| Showing the EXIF data and improving the slideshow view                         | Done   |
| Configure whether to play sound with videos                                    | Done   |
| Smarter merging of portrait photos (same people, same date, same city)         | Todo   |
| Add transitions to slideshow                                                   | Todo   |
| Add places/tags view                                                           | Todo   |
| Add background media playing info to screensaver                               | Todo   |
| Casting capabilities                                                           | Todo   |
| Searching in and for albums                                                    | Todo   |
| Dependency injection with Hilt/Dagger                                          | Todo   |

## Screenshots

|                                                                                    |                                                                      |                                                                                    |
|:----------------------------------------------------------------------------------:|:--------------------------------------------------------------------:|:----------------------------------------------------------------------------------:|
|        ![Alt text](/screenshots/homescreen-1.png?raw=true "Album overview")        |      ![Alt text](/screenshots/photos.png?raw=true "All photos")      |      ![Alt text](/screenshots/sorting-options.png?raw=true "Sorting options")      |
|         ![Alt text](/screenshots/home-edit.png?raw=true "Edit homescreen")         | ![Alt text](/screenshots/settings-view.png?raw=true "View settings") | ![Alt text](/screenshots/settings-screensaver.png?raw=true "Screensaver settings") |
| ![Alt text](/screenshots/screensaver-portrait.png?raw=true "Screensaver portrait") |        ![Alt text](/screenshots/people.png?raw=true "People")        |             ![Alt text](/screenshots/seasonl.png?raw=true "Seasonal")              |

## Build steps

1. Clone project with `git clone --recurse git@github.com:giejay/Immich-Android-TV.git`
2. Create an account at firebase and create a google-services.json file, or
   `cp apps/google-services.example apps/google-services.json`
3. copy app/src/strings_other.xml.example to app/src/main/res/values/strings_other.xml and modify
   the address and API keys for your demo server.
4. Build apk with `./gradlew assembleRelease`

## Support the project

You can support the project in several ways. The first one is by creating nice descriptive bug
reports if you find any: https://github.com/giejay/Immich-Android-TV/issues/new/choose.
<br><br>Even better is creating a PR: https://github.com/giejay/Immich-Android-TV/pulls.
<br><br>
Lastly, if you feel this Android TV app is a useful addition to the already great Immich app, you
might consider buying me a coffee or a beer:

[!["Buy Me A Coffee"](https://www.buymeacoffee.com/assets/img/custom_images/orange_img.png)](https://www.buymeacoffee.com/giejay)

## FAQ

#### I'n not able to set the app as a screensaver

1. Enable development mode on the device (click the build number or "Android TV OS Build" 7 times in
   the System->About settings).
2. Go to System -> Developer Options and enable USB Debugging.
3. If you don't have ADB installed on your PC, follow these
   instructions: https://www.xda-developers.com/install-adb-windows-macos-linux/
4. After downloading/installing ADB on the PC, connect to the device using it's IP: adb connect
   192.168.xx.xx.
5. Once you are connected, execute the following command: 'adb shell settings put secure
   screensaver_components nl.giejay.android.tv.immich/.screensaver.ScreenSaverService'
6. Done!
