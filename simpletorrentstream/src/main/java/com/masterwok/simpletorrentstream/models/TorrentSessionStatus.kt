package com.masterwok.simpletorrentstream.models

import android.net.Uri
import com.frostwire.jlibtorrent.TorrentHandle
import com.masterwok.simpletorrentstream.TorrentSessionBufferState
import com.masterwok.simpletorrentstream.extensions.getProgress
import com.masterwok.simpletorrentstream.extensions.getTotalDone
import com.masterwok.simpletorrentstream.extensions.getTotalWanted
import com.masterwok.simpletorrentstream.extensions.isFinished

@Suppress("unused")
class TorrentSessionStatus private constructor(
        val isFinished: Boolean
        , val progress: Float
        , val bytesDownloaded: Long
        , val bytesWanted: Long
        , val saveLocationUri: Uri
        , val videoFileUri: Uri
        , val torrentSessionBufferState: TorrentSessionBufferState
) {
    internal companion object {
        fun createInstance(
                torrentHandle: TorrentHandle
                , torrentSessionBufferState: TorrentSessionBufferState
                , saveLocationUri: Uri
                , largestFileUri: Uri
        ): TorrentSessionStatus = TorrentSessionStatus(
                torrentHandle.isFinished()
                , torrentHandle.getProgress()
                , torrentHandle.getTotalDone()
                , torrentHandle.getTotalWanted()
                , saveLocationUri
                , largestFileUri
                , torrentSessionBufferState
        )
    }
}

