package com.masterwok.simpletorrentstream.models

import android.net.Uri
import com.frostwire.jlibtorrent.TorrentHandle
import com.masterwok.simpletorrentstream.extensions.getFirstMissingPieceIndex
import com.masterwok.simpletorrentstream.extensions.getLargestFileUri
import com.masterwok.simpletorrentstream.extensions.getSaveLocation

@Suppress("unused")
class TorrentStreamStatus private constructor(
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
        ): TorrentStreamStatus {
            val torrentInfo = torrentHandle.torrentFile()
            val status = torrentHandle.status()

            return TorrentStreamStatus(
                    status.isFinished
                    , status.progress()
                    , status.totalDone()
                    , status.totalWanted()
                    , torrentHandle.getLargestFileUri()
                    , torrentHandle.getSaveLocation()
                    , torrentHandle.getFirstMissingPieceIndex()
                    , downloadedPieceIndexes
                    , torrentInfo.numPieces()
            )
        }
    }
}

