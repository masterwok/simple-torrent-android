package com.masterwok.simpletorrentstream.models

import android.net.Uri
import com.frostwire.jlibtorrent.TorrentHandle
import com.masterwok.simpletorrentstream.TorrentSessionBufferState
import com.masterwok.simpletorrentstream.extensions.*

@Suppress("unused")
class TorrentSessionStatus private constructor(
        val isFinished: Boolean
        , val progress: Float
        , val bytesDownloaded: Long
        , val bytesWanted: Long
        , val videoFileUri: Uri
        , val saveUri: Uri
        , val torrentSessionBufferState: TorrentSessionBufferState
) {
    internal companion object {
        fun createInstance(
                torrentHandle: TorrentHandle
                , torrentSessionBufferState: TorrentSessionBufferState
        ): TorrentSessionStatus = TorrentSessionStatus(
                torrentHandle.isFinished()
                , torrentHandle.getProgress()
                , torrentHandle.getTotalDone()
                , torrentHandle.getTotalWanted()
                // TODO: This can be optimized..
                , torrentHandle.getLargestFileUri()
                , torrentHandle.getSaveLocation()
                , torrentSessionBufferState
        )
    }
}

