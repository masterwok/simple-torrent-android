package com.masterwok.simpletorrentstream.models

import android.net.Uri
import com.frostwire.jlibtorrent.TorrentHandle
import com.masterwok.simpletorrentstream.extensions.*

@Suppress("unused")
class TorrentSessionStatus private constructor(
        val isFinished: Boolean
        , val progress: Float
        , val bytesDownloaded: Long
        , val bytesWanted: Long
        , val videoFileUri: Uri
        , val saveUri: Uri
        , val firstMissingPieceIndex: Int
        , val downloadedPieces: List<Int>
        , val totalPieces: Int
) {
    internal companion object {
        fun createInstance(
                torrentHandle: TorrentHandle
                , downloadedPieceIndexes: List<Int>
        ): TorrentSessionStatus = TorrentSessionStatus(
                torrentHandle.isFinished()
                , torrentHandle.getProgress()
                , torrentHandle.getTotalDone()
                , torrentHandle.getTotalWanted()
                , torrentHandle.getLargestFileUri()
                , torrentHandle.getSaveLocation()
                , torrentHandle.getFirstMissingPieceIndex()
                , downloadedPieceIndexes
                , torrentHandle.getPieceCount()
        )
    }
}

