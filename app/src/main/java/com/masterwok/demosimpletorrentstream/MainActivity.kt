package com.masterwok.demosimpletorrentstream

import android.Manifest
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import com.masterwok.simpletorrentstream.TorrentStream
import com.masterwok.simpletorrentstream.extensions.appCompatRequestPermissions
import com.masterwok.simpletorrentstream.extensions.isPermissionGranted
import kotlinx.coroutines.experimental.launch

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        if (!isPermissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            appCompatRequestPermissions(
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    , 0
            )

            return
        }

        startDownload()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        startDownload()
    }

    private fun startDownload() {
        val options = TorrentStream
                .Builder()
                .setDownloadLocation(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS))
                .build()

        val torrentStream = TorrentStream(options)
        val magnetUri = "magnet:?xt=urn:btih:d9d9785105166a3a93da6e1f09bd062142a2e2f4&dn=The+Edge+%281997%29+720p+BrRip+x264+-+600MB+-+YIFY&tr=udp%3A%2F%2Ftracker.leechers-paradise.org%3A6969&tr=udp%3A%2F%2Fzer0day.ch%3A1337&tr=udp%3A%2F%2Fopen.demonii.com%3A1337&tr=udp%3A%2F%2Ftracker.coppersurfer.tk%3A6969&tr=udp%3A%2F%2Fexodus.desync.com%3A6969"

        launch {
            val result = torrentStream.downloadMagnet(magnetUri)

            val x = 1
        }
    }
}
