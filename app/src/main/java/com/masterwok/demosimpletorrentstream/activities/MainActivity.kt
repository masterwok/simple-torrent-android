package com.masterwok.demosimpletorrentstream.activities

import android.Manifest
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.masterwok.demosimpletorrentstream.R
import com.masterwok.demosimpletorrentstream.adapters.TabFragmentPagerAdapter
import com.masterwok.demosimpletorrentstream.fragments.TorrentControlFragment
import com.masterwok.demosimpletorrentstream.fragments.TorrentPiecesFragment
import com.masterwok.simpletorrentstream.TorrentSession
import com.masterwok.simpletorrentstream.TorrentSessionOptions
import com.masterwok.simpletorrentstream.contracts.TorrentSessionListener
import com.masterwok.simpletorrentstream.extensions.appCompatRequestPermissions
import com.masterwok.simpletorrentstream.extensions.isPermissionGranted
import com.masterwok.simpletorrentstream.models.TorrentSessionStatus
import java.lang.ref.WeakReference

class MainActivity : AppCompatActivity() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager

    private val torrentSessionPagerAdapter = TabFragmentPagerAdapter(
            supportFragmentManager
            , TorrentPiecesFragment()
            , TorrentControlFragment()
    )

    private lateinit var torrentSession: TorrentSession

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bindViewComponents()

        viewPager.adapter = torrentSessionPagerAdapter
        tabLayout.setupWithViewPager(viewPager)

        if (!isPermissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            appCompatRequestPermissions(
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    , 0
            )

            return
        }

        startDownload()
    }

    override fun onDestroy() {
        startDownloadTask?.cancel(true)
        torrentSession.setListener(null)
        torrentSession.stop()

        super.onDestroy()
    }

    private fun bindViewComponents() {
        tabLayout = findViewById(R.id.tab_layout)
        viewPager = findViewById(R.id.view_pager)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        startDownload()
    }

    private val torrentStreamListener = object : TorrentSessionListener {

        override fun onAddTorrent(torrentSessionStatus: TorrentSessionStatus) =
                configure("onAddTorrent", torrentSessionStatus)

        override fun onTorrentRemoved(torrentSessionStatus: TorrentSessionStatus) =
                configure("onTorrentRemoved", torrentSessionStatus)

        override fun onTorrentDeleted(torrentSessionStatus: TorrentSessionStatus) =
                configure("onTorrentRemoved", torrentSessionStatus)

        override fun onTorrentDeleteFailed(torrentSessionStatus: TorrentSessionStatus) =
                configure("onTorrentDeleteFailed", torrentSessionStatus)

        override fun onTorrentError(torrentSessionStatus: TorrentSessionStatus) =
                configure("onTorrentError", torrentSessionStatus)

        override fun onTorrentResumed(torrentSessionStatus: TorrentSessionStatus) =
                configure("onTorrentResumed", torrentSessionStatus)

        override fun onTorrentPaused(torrentSessionStatus: TorrentSessionStatus) =
                configure("onTorrentPaused", torrentSessionStatus)

        override fun onTorrentFinished(torrentSessionStatus: TorrentSessionStatus) =
                configure("onTorrentFinished", torrentSessionStatus)

        override fun onPieceFinished(torrentSessionStatus: TorrentSessionStatus) =
                configure("onPieceFinished", torrentSessionStatus)

        override fun onMetadataFailed(torrentSessionStatus: TorrentSessionStatus) =
                configure("onMetadataFailed", torrentSessionStatus)

        override fun onMetadataReceived(torrentSessionStatus: TorrentSessionStatus) =
                configure("onMetadataReceived", torrentSessionStatus)

        private fun configure(
                tag: String
                , torrentSessionStatus: TorrentSessionStatus
        ) {
            try {
                torrentSessionPagerAdapter.configure(torrentSessionStatus)

                val bufferState = torrentSessionStatus.torrentSessionBufferState

                Log.d(tag, bufferState.toString())
            } catch (ex: Exception) {
                Log.d("ERROR", ex.toString())
            }
        }
    }

    private var startDownloadTask: DownloadTask? = null

    private fun startDownload() {
//        val magnetUri = "magnet:?xt=urn:btih:d9d9785105166a3a93da6e1f09bd062142a2e2f4&dn=The+Edge+%281997%29+720p+BrRip+x264+-+600MB+-+YIFY&tr=udp%3A%2F%2Ftracker.leechers-paradise.org%3A6969&tr=udp%3A%2F%2Fzer0day.ch%3A1337&tr=udp%3A%2F%2Fopen.demonii.com%3A1337&tr=udp%3A%2F%2Ftracker.coppersurfer.tk%3A6969&tr=udp%3A%2F%2Fexodus.desync.com%3A6969"
//        val magnetUri = "magnet:?xt=urn:btih:1815a467da2820aea936b622c09966abed626c9c&dn=Alien.1979.Directors.Cut.1080p.BluRay.H264.AAC-RARBG&tr=udp%3A%2F%2Ftracker.leechers-paradise.org%3A6969&tr=udp%3A%2F%2Fzer0day.ch%3A1337&tr=udp%3A%2F%2Fopen.demonii.com%3A1337&tr=udp%3A%2F%2Ftracker.coppersurfer.tk%3A6969&tr=udp%3A%2F%2Fexodus.desync.com%3A6969"
        val magnetUri = "magnet:?xt=urn:btih:2d6354d22bbda47b22ab65066b8736d9851bb493&dn=Grandmas+Boy+UNRATED+2006+720p+WEB-DL+x264+AAC+-+Ozlem&tr=udp%3A%2F%2Ftracker.leechers-paradise.org%3A6969&tr=udp%3A%2F%2Fzer0day.ch%3A1337&tr=udp%3A%2F%2Fopen.demonii.com%3A1337&tr=udp%3A%2F%2Ftracker.coppersurfer.tk%3A6969&tr=udp%3A%2F%2Fexodus.desync.com%3A6969"

        val options = TorrentSessionOptions
                .Builder()
                .setDownloadLocation(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS))
                .build()

        torrentSession = TorrentSession(options)
        torrentSession.setListener(torrentStreamListener)

        startDownloadTask = DownloadTask(torrentSession, magnetUri)
        startDownloadTask?.execute()
    }


    private class DownloadTask : AsyncTask<Void, Void, Unit> {

        private val torrentSession: WeakReference<TorrentSession>
        val magnetUri: String

        constructor(torrentSession: TorrentSession, magnetUri: String) : super() {
            this.torrentSession = WeakReference(torrentSession)
            this.magnetUri = magnetUri
        }


        override fun doInBackground(vararg args: Void) {
            torrentSession.get()?.downloadMagnet(magnetUri, 30)
        }

    }
}
