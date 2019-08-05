[![Release](https://jitpack.io/v/masterwok/simple-torrent-android.svg)](https://jitpack.io/#masterwok/simple-torrent-android)

# [deprecated] simple-torrent-android

I'm not longer maintaining this project due to time constraints.

An Android torrent client library powered by [frostwire-jlibtorrent](https://github.com/frostwire/frostwire-jlibtorrent). It supports sequential and simultaneous downloads.


## Usage

A single torrent download session is represented as the ```TorrentSession``` class. An instance of this class can be used to start, stop, pause, and resume a torrent download. When creating a new session, a ```TorrentSessionOptions``` instance is a required constructor parameter used to configure the session. The ```TorrentSessionListener``` interface can be implemented and set on the session to receive ```TorrentSessionStatus``` updates tied to the lifecycle of the torrent. The ```TorrentSessionBufferState``` is one of the properties of the status and represents the current state of the torrent piece buffer.

For example, the following code snippet sequentially downloads the largest file of the provided torrent to the downloads directory:

```kotlin
// http, https, magnet, file, and content Uri types are all supported.
val torrentUri = Uri.parse("http://www.frostclick.com/torrents/video/animation/Big_Buck_Bunny_1080p_surround_frostclick.com_frostwire.com.torrent")

val torrentSessionOptions = TorrentSessionOptions(
        downloadLocation = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        , onlyDownloadLargestFile = true
        , enableLogging = false
        , shouldStream = true
)

val torrentSession = TorrentSession(torrentSessionOptions)

torrentSession.listener = object : TorrentSessionListener {
    
    // Omitted for brevity
    
    override fun onPieceFinished(
        torrentHandle: TorrentHandle
        , torrentSessionStatus: TorrentSessionStatus
    ) { ... }
}

torrentSession.start(context, torrentUri) // Invoke on background thread.
// OR torrentSession.start(bencodeByteArray)

```

For a more detailed example, please see the demo application alongside this library (screenshot below). Please note, that the demo application does not properly show how to keep sessions alive after the activity is killed. This was a quick and dirty way of showing/testing downloads. A more real-world solution, would be to have the torrent sessions exist within a service that enters the foreground while downloading. By doing this, the sessions will retain network connectivity and will not be murdered by Android the destroyer.

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
    implementation 'com.github.masterwok:simple-torrent-android:0.5.0'
}
```

## Projects using simple-torrent-android
- [Bit Cast](https://play.google.com/store/apps/details?id=com.masterwok.bitcast)

## Demo Screenshots

<img src="/app/screenshots/example.jpg?raw=true" height="600" title="Demo">
