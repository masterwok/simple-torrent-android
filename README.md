[![Release](https://jitpack.io/v/masterwok/simple-torrent-stream.svg)](https://jitpack.io/#masterwok/simple-torrent-stream)

# simple-vlc-player
An Android torrent streaming library powered by [frostwire-jlibtorrent](https://github.com/frostwire/frostwire-jlibtorrent).


## Usage

```kotlin

```

## Configuration

Add this in your root build.gradle at the end of repositories:
```gradle
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}
```
and add the following in the dependent module:

```gradle
dependencies {
    implementation 'com.github.masterwok:simple-torrent-stream:0.0.1'
}
```
unless you're a fan of large APKs, you'll probably want to add the following to the build.gradle of your app so an APK is generated per ABI:

```gradle
android {
    ...
    splits {
        abi {
            enable true
            reset()
            include 'armeabi-v7a', 'arm64-v8a', 'x86', 'x86_64'
            universalApk false
        }
    }
}

// Map for the version code that gives each ABI a value.
ext.abiCodes = [
        'armeabi-v7a': 1,
        'arm64-v8a'  : 2,
        'x86'        : 3,
        'x86_64'     : 4
]

import com.android.build.OutputFile

android.applicationVariants.all { variant ->
    variant.outputs.each { output ->
        def baseAbiVersionCode = project.ext.abiCodes.get(output.getFilter(OutputFile.ABI))

        if (baseAbiVersionCode != null) {
            output.versionCodeOverride = baseAbiVersionCode * 10000000 + variant.versionCode
        }
    }
}
```

## Sample Screenshots

![Local Playback](/sample/screenshots/localPlayback.jpg?raw=true "Local Playback")
![Renderer Item Selection](/sample/screenshots/rendererItemSelection.jpg?raw=true "Renderer Item Selection")
![Casting](/sample/screenshots/casting.jpg?raw=true "Casting")
<img src="/sample/screenshots/lockScreenAndNotification.jpg?raw=true" height="600" title="Lock Screen and Notification">
