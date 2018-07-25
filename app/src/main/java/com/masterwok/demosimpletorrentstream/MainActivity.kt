package com.masterwok.demosimpletorrentstream

import android.Manifest
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.masterwok.simpletorrentstream.TorrentStream
import com.masterwok.simpletorrentstream.TorrentStreamOptions
import com.masterwok.simpletorrentstream.contracts.TorrentStreamListener
import com.masterwok.simpletorrentstream.extensions.appCompatRequestPermissions
import com.masterwok.simpletorrentstream.extensions.isPermissionGranted
import com.masterwok.simpletorrentstream.models.TorrentStreamStatus
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

    private val torrentStreamListener = object : TorrentStreamListener {
        override fun onPieceFinished(torrentStreamStatus: TorrentStreamStatus) {
            Log.d(
                    "onPieceFinished",
                    "| Piece Count: ${torrentStreamStatus.totalPieces}"
                            + ", Piece: ${torrentStreamStatus.downloadedPieces.size}/${torrentStreamStatus.totalPieces}"
                            + ", First Missing Piece Index: ${torrentStreamStatus.firstMissingPieceIndex}"
                            + ", Progress: ${torrentStreamStatus.bytesDownloaded}/${torrentStreamStatus.bytesWanted} (${torrentStreamStatus.progress}%)"
                            + ", Is Finished: ${torrentStreamStatus.isFinished}"
            )
        }

    }

    private fun startDownload() {
        val magnetUri = "magnet:?xt=urn:btih:d9d9785105166a3a93da6e1f09bd062142a2e2f4&dn=The+Edge+%281997%29+720p+BrRip+x264+-+600MB+-+YIFY&tr=udp%3A%2F%2Ftracker.leechers-paradise.org%3A6969&tr=udp%3A%2F%2Fzer0day.ch%3A1337&tr=udp%3A%2F%2Fopen.demonii.com%3A1337&tr=udp%3A%2F%2Ftracker.coppersurfer.tk%3A6969&tr=udp%3A%2F%2Fexodus.desync.com%3A6969"

        val options = TorrentStreamOptions
                .Builder()
                .setDownloadLocation(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS))
                .build()

        val torrentStream = TorrentStream(options)

        torrentStream.setListener(torrentStreamListener)

        launch {
            torrentStream.downloadMagnet(magnetUri, 30)
        }
    }
}
