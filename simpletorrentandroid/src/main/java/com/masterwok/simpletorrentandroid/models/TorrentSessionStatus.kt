package com.masterwok.simpletorrentandroid.models

import android.net.Uri
import com.frostwire.jlibtorrent.TorrentHandle
import com.frostwire.jlibtorrent.TorrentStatus
import com.masterwok.simpletorrentandroid.extensions.*


/**
 * This class represents the current state of a torrent session. To receive
 * state updates, set the listener of the [@see TorrentSession].
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
class TorrentSessionStatus private constructor(
        val torrentUri: Uri
        , val state: State
        , val seederCount: Int
        , val downloadRate: Int
        , val uploadRate: Int
        , val progress: Float
        , val bytesDownloaded: Long
        , val bytesWanted: Long
        , val saveLocationUri: Uri
        , val videoFileUri: Uri
        , val torrentSessionBuffer: TorrentSessionBuffer
) {
    internal companion object {
        fun createInstance(
                torrentUri: Uri
                , torrentHandle: TorrentHandle
                , torrentSessionBuffer: TorrentSessionBuffer
                , saveLocationUri: Uri
                , largestFileUri: Uri
        ): TorrentSessionStatus = TorrentSessionStatus(
                torrentUri
                , torrentHandle.status().state().toTorrentStreamStatusSate()
                , torrentHandle.getSeederCount()
                , torrentHandle.getDownloadRate()
                , torrentHandle.getUploadRate()
                , torrentHandle.getProgress()
                , torrentHandle.getTotalDone()
                , torrentHandle.getTotalWanted()
                , saveLocationUri
                , largestFileUri
                , torrentSessionBuffer
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

    override fun toString(): String {
        return "State: $state" +
                ", Seeder Count: $seederCount" +
                ", Download Rate: $downloadRate" +
                ", Upload Rate: $uploadRate" +
                ", Progress: $bytesDownloaded/$bytesWanted ($progress)" +
                ", $torrentSessionBuffer" +
                ", Torrent Uri: $torrentUri" +
                ", Save Location: $saveLocationUri" +
                ", Video File: $videoFileUri"
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

