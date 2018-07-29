package com.masterwok.simpletorrentstream.models

import android.net.Uri
import com.frostwire.jlibtorrent.TorrentHandle
import com.frostwire.jlibtorrent.TorrentStatus
import com.masterwok.simpletorrentstream.TorrentSessionBufferState
import com.masterwok.simpletorrentstream.extensions.getProgress
import com.masterwok.simpletorrentstream.extensions.getTotalDone
import com.masterwok.simpletorrentstream.extensions.getTotalWanted

@Suppress("unused")
class TorrentSessionStatus private constructor(
        val state: State
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
                torrentHandle.status().state().toTorrentStreamStatusSate()
                , torrentHandle.getProgress()
                , torrentHandle.getTotalDone()
                , torrentHandle.getTotalWanted()
                , saveLocationUri
                , largestFileUri
                , torrentSessionBufferState
        )

        private fun TorrentStatus.State.toTorrentStreamStatusSate(): State = when (this) {
            TorrentStatus.State.CHECKING_FILES -> State.CHECKING_FILES
            TorrentStatus.State.DOWNLOADING_METADATA -> State.DOWNLOADING_METADATA
            TorrentStatus.State.DOWNLOADING -> State.DOWNLOADING
            TorrentStatus.State.FINISHED -> State.FINISHED
            TorrentStatus.State.SEEDING -> State.SEEDING
            TorrentStatus.State.ALLOCATING -> State.ALLOCATING
            TorrentStatus.State.CHECKING_RESUME_DATA -> State.CHECKING_RESUME_DATA
            TorrentStatus.State.UNKNOWN -> State.UNKNOWN
        }
    }

    enum class State {
        CHECKING_FILES,
        DOWNLOADING_METADATA,
        DOWNLOADING,
        FINISHED,
        SEEDING,
        ALLOCATING,
        CHECKING_RESUME_DATA,
        UNKNOWN
    }
}

