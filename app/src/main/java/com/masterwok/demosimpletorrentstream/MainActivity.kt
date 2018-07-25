package com.masterwok.demosimpletorrentstream

import android.Manifest
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.masterwok.simpletorrentstream.TorrentSession
import com.masterwok.simpletorrentstream.TorrentSessionOptions
import com.masterwok.simpletorrentstream.contracts.TorrentSessionListener
import com.masterwok.simpletorrentstream.extensions.appCompatRequestPermissions
import com.masterwok.simpletorrentstream.extensions.isPermissionGranted
import com.masterwok.simpletorrentstream.models.TorrentSessionStatus
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

    private val torrentStreamListener = object : TorrentSessionListener {

        override fun onAddTorrent(torrentSessionStatus: TorrentSessionStatus) =
                log("onAddTorrent", torrentSessionStatus)

        override fun onTorrentRemoved(torrentSessionStatus: TorrentSessionStatus) =
                log("onTorrentRemoved", torrentSessionStatus)

        override fun onTorrentDeleted(torrentSessionStatus: TorrentSessionStatus) =
                log("onTorrentRemoved", torrentSessionStatus)

        override fun onTorrentDeleteFailed(torrentSessionStatus: TorrentSessionStatus) =
                log("onTorrentDeleteFailed", torrentSessionStatus)

        override fun onTorrentError(torrentSessionStatus: TorrentSessionStatus) =
                log("onTorrentError", torrentSessionStatus)

        override fun onTorrentResumed(torrentSessionStatus: TorrentSessionStatus) =
                log("onTorrentResumed", torrentSessionStatus)

        override fun onTorrentPaused(torrentSessionStatus: TorrentSessionStatus) =
                log("onTorrentPaused", torrentSessionStatus)

        // TODO: Fix state when stream has already been downloaded (see logs).
        override fun onTorrentFinished(torrentSessionStatus: TorrentSessionStatus) =
                log("onTorrentFinished", torrentSessionStatus)

        // TODO: Ensure piece count is correct on finish (see logs).
        override fun onPieceFinished(torrentSessionStatus: TorrentSessionStatus) =
                log("onPieceFinished", torrentSessionStatus)

        override fun onMetadataFailed(torrentSessionStatus: TorrentSessionStatus) =
                log("onMetadataFailed", torrentSessionStatus)

        override fun onMetadataReceived(torrentSessionStatus: TorrentSessionStatus) =
                log("onMetadataReceived", torrentSessionStatus)

        private fun log(
                tag: String
                , torrentSessionStatus: TorrentSessionStatus
        ) {
            Log.d(
                    tag
                    , "| Total Pieces: ${torrentSessionStatus.totalPieces}"
                    + ", Piece: ${torrentSessionStatus.downloadedPieces.size}/${torrentSessionStatus.totalPieces}"
                    + ", First Missing Piece Index: ${torrentSessionStatus.firstMissingPieceIndex}"
                    + ", Progress: ${torrentSessionStatus.bytesDownloaded}/${torrentSessionStatus.bytesWanted} (${torrentSessionStatus.progress * 100}%)"
                    + ", Is Finished: ${torrentSessionStatus.isFinished}"
            )
        }

    }

    private fun startDownload() {
//        val magnetUri = "magnet:?xt=urn:btih:d9d9785105166a3a93da6e1f09bd062142a2e2f4&dn=The+Edge+%281997%29+720p+BrRip+x264+-+600MB+-+YIFY&tr=udp%3A%2F%2Ftracker.leechers-paradise.org%3A6969&tr=udp%3A%2F%2Fzer0day.ch%3A1337&tr=udp%3A%2F%2Fopen.demonii.com%3A1337&tr=udp%3A%2F%2Ftracker.coppersurfer.tk%3A6969&tr=udp%3A%2F%2Fexodus.desync.com%3A6969"
//        val magnetUri = "magnet:?xt=urn:btih:1815a467da2820aea936b622c09966abed626c9c&dn=Alien.1979.Directors.Cut.1080p.BluRay.H264.AAC-RARBG&tr=udp%3A%2F%2Ftracker.leechers-paradise.org%3A6969&tr=udp%3A%2F%2Fzer0day.ch%3A1337&tr=udp%3A%2F%2Fopen.demonii.com%3A1337&tr=udp%3A%2F%2Ftracker.coppersurfer.tk%3A6969&tr=udp%3A%2F%2Fexodus.desync.com%3A6969"
        val magnetUri = "magnet:?xt=urn:btih:2d6354d22bbda47b22ab65066b8736d9851bb493&dn=Grandmas+Boy+UNRATED+2006+720p+WEB-DL+x264+AAC+-+Ozlem&tr=udp%3A%2F%2Ftracker.leechers-paradise.org%3A6969&tr=udp%3A%2F%2Fzer0day.ch%3A1337&tr=udp%3A%2F%2Fopen.demonii.com%3A1337&tr=udp%3A%2F%2Ftracker.coppersurfer.tk%3A6969&tr=udp%3A%2F%2Fexodus.desync.com%3A6969"

        val options = TorrentSessionOptions
                .Builder()
                .setDownloadLocation(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS))
                .build()

        val torrentStream = TorrentSession(options)

        torrentStream.setListener(torrentStreamListener)

        launch {
            torrentStream.downloadMagnet(magnetUri, 30)
        }
    }
}
