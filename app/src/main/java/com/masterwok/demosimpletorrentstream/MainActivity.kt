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

        override fun onTorrentError(torrentStreamStatus: TorrentStreamStatus) =
                log("onTorrentError", torrentStreamStatus)

        // TODO: Fix state when stream has already been downloaded (see logs).
        override fun onTorrentFinished(torrentStreamStatus: TorrentStreamStatus) =
                log("onTorrentFinished", torrentStreamStatus)

        override fun onMetadataFailed(torrentStreamStatus: TorrentStreamStatus) =
                log("onMetadataFailed", torrentStreamStatus)

        override fun onMetadataReceived(torrentStreamStatus: TorrentStreamStatus) =
                log("onMetadataReceived", torrentStreamStatus)

        override fun onAddTorrent(torrentStreamStatus: TorrentStreamStatus) =
                log("onAddTorrent", torrentStreamStatus)

        // TODO: Ensure piece count is corrent on finish (see logs).
        override fun onPieceFinished(torrentStreamStatus: TorrentStreamStatus) =
                log("onPieceFinished", torrentStreamStatus)

        private fun log(
                tag: String
                , torrentStreamStatus: TorrentStreamStatus
        ) {
            Log.d(
                    tag
                    , "| Total Pieces: ${torrentStreamStatus.totalPieces}"
                    + ", Piece: ${torrentStreamStatus.downloadedPieces.size}/${torrentStreamStatus.totalPieces}"
                    + ", First Missing Piece Index: ${torrentStreamStatus.firstMissingPieceIndex}"
                    + ", Progress: ${torrentStreamStatus.bytesDownloaded}/${torrentStreamStatus.bytesWanted} (${torrentStreamStatus.progress * 100}%)"
                    + ", Is Finished: ${torrentStreamStatus.isFinished}"
            )
        }

    }

    private fun startDownload() {
//        val magnetUri = "magnet:?xt=urn:btih:d9d9785105166a3a93da6e1f09bd062142a2e2f4&dn=The+Edge+%281997%29+720p+BrRip+x264+-+600MB+-+YIFY&tr=udp%3A%2F%2Ftracker.leechers-paradise.org%3A6969&tr=udp%3A%2F%2Fzer0day.ch%3A1337&tr=udp%3A%2F%2Fopen.demonii.com%3A1337&tr=udp%3A%2F%2Ftracker.coppersurfer.tk%3A6969&tr=udp%3A%2F%2Fexodus.desync.com%3A6969"
//        val magnetUri = "magnet:?xt=urn:btih:1815a467da2820aea936b622c09966abed626c9c&dn=Alien.1979.Directors.Cut.1080p.BluRay.H264.AAC-RARBG&tr=udp%3A%2F%2Ftracker.leechers-paradise.org%3A6969&tr=udp%3A%2F%2Fzer0day.ch%3A1337&tr=udp%3A%2F%2Fopen.demonii.com%3A1337&tr=udp%3A%2F%2Ftracker.coppersurfer.tk%3A6969&tr=udp%3A%2F%2Fexodus.desync.com%3A6969"
        val magnetUri = "magnet:?xt=urn:btih:2d6354d22bbda47b22ab65066b8736d9851bb493&dn=Grandmas+Boy+UNRATED+2006+720p+WEB-DL+x264+AAC+-+Ozlem&tr=udp%3A%2F%2Ftracker.leechers-paradise.org%3A6969&tr=udp%3A%2F%2Fzer0day.ch%3A1337&tr=udp%3A%2F%2Fopen.demonii.com%3A1337&tr=udp%3A%2F%2Ftracker.coppersurfer.tk%3A6969&tr=udp%3A%2F%2Fexodus.desync.com%3A6969"

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
