package com.masterwok.demosimpletorrentstream.fragments

import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.AppCompatButton
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.masterwok.demosimpletorrentstream.R
import com.masterwok.demosimpletorrentstream.adapters.TabFragmentPagerAdapter
import com.masterwok.simpletorrentstream.TorrentSession
import com.masterwok.simpletorrentstream.TorrentSessionOptions
import com.masterwok.simpletorrentstream.contracts.TorrentSessionListener
import com.masterwok.simpletorrentstream.models.TorrentSessionStatus
import java.lang.ref.WeakReference

class TorrentFragment : Fragment()
        , TabFragmentPagerAdapter.TabFragment<TorrentSessionStatus>
        , TorrentSessionListener {

    private var torrentPiecesFragment: TorrentPiecesFragment? = null
    private lateinit var buttonPauseResume: AppCompatButton

    private lateinit var torrentSession: TorrentSession
    private var startDownloadTask: DownloadTask? = null

    private var tabIndex: Int = 0

    companion object {
        fun newInstance(
                tabIndex: Int
                , magnetUri: String
                , torrentSessionOptions: TorrentSessionOptions
        ): TorrentFragment = TorrentFragment().apply {
            this.tabIndex = tabIndex

            torrentSession = TorrentSession(magnetUri, torrentSessionOptions)
            torrentSession.setListener(this)

            startDownloadTask = DownloadTask(torrentSession, magnetUri)
            startDownloadTask?.execute()
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater
            , container: ViewGroup?
            , savedInstanceState: Bundle?
    ): View = inflater.inflate(
            R.layout.fragment_torrent
            , container
            , false
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindViewComponents()
        subscribeToViewComponents()
    }

    private fun bindViewComponents() {
        torrentPiecesFragment = childFragmentManager.findFragmentById(R.id.fragment_torrent_pieces) as TorrentPiecesFragment
        buttonPauseResume = view!!.findViewById(R.id.button_pause_resume)
    }

    private fun subscribeToViewComponents() {
        buttonPauseResume.setOnClickListener {
            if (torrentSession.isPaused) {
                torrentSession.resume()
                buttonPauseResume.setText(R.string.button_pause)
            } else {
                torrentSession.pause()
                buttonPauseResume.setText(R.string.button_resume)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        startDownloadTask?.cancel(true)
        torrentSession.setListener(null)
        torrentSession.stop()
    }

    override fun configure(model: TorrentSessionStatus) {
    }

    override fun getTitle(): String = "Torrent: $tabIndex"


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
        torrentPiecesFragment?.configure(torrentSessionStatus)

        Log.d(tag, torrentSessionStatus.torrentSessionBufferState.toString())
    }

    private class DownloadTask : AsyncTask<Void, Void, Unit> {

        private val torrentSession: WeakReference<TorrentSession>
        val magnetUri: String

        @Suppress("ConvertSecondaryConstructorToPrimary")
        constructor(torrentSession: TorrentSession, magnetUri: String) : super() {
            this.torrentSession = WeakReference(torrentSession)
            this.magnetUri = magnetUri
        }

        override fun doInBackground(vararg args: Void) {
            torrentSession.get()?.start(30)
        }
    }

}