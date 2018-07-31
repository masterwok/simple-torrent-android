package com.masterwok.simpletorrentandroid

import com.frostwire.jlibtorrent.SessionParams
import com.frostwire.jlibtorrent.SettingsPack
import java.io.File

data class TorrentSessionOptions(
        val bufferSize: Int
        , val onlyDownloadLargestFile: Boolean
        , val shouldStream: Boolean
        , val downloadLocation: File
        , val downloadRateLimit: Int
        , val uploadRateLimit: Int
        , val connectionsLimit: Int
        , val dhtNodeMinimum: Int
        , val dhtNodeLimit: Int
        , val anonymousMode: Boolean
) {
    internal fun build(): SessionParams {
        val settingsPack = SettingsPack()
                .downloadRateLimit(downloadRateLimit)
                .uploadRateLimit(uploadRateLimit)
                .connectionsLimit(connectionsLimit)
                .activeDhtLimit(dhtNodeLimit)
                .anonymousMode(anonymousMode)

        return SessionParams(settingsPack)
    }

    @Suppress("unused")
    class Builder constructor(
            private val downloadLocation: File
    ) {
        private var onlyDownloadLargestFile: Boolean = false
        private var shouldStream: Boolean = false
        private var bufferSize: Int = 8
        private var downloadRateLimit: Int = 0
        private var uploadRateLimit: Int = 0
        private var connectionsLimit: Int = 200
        private var dhtNodeMinimum: Int = 10
        private var dhtNodeLimit: Int = 88
        private var anonymousMode: Boolean = false

        fun build(): TorrentSessionOptions {
            return TorrentSessionOptions(
                    bufferSize
                    , onlyDownloadLargestFile
                    , shouldStream
                    , downloadLocation
                    , downloadRateLimit
                    , uploadRateLimit
                    , connectionsLimit
                    , dhtNodeMinimum
                    , dhtNodeLimit
                    , anonymousMode
            )
        }

        fun onlyDownloadLargestFile(onlyDownloadLargestFile: Boolean): Builder {
            this.onlyDownloadLargestFile = onlyDownloadLargestFile
            return this
        }

        fun stream(shouldStream: Boolean): Builder {
            this.shouldStream = shouldStream
            return this
        }

        fun streamBufferSize(bufferSize: Int): Builder {
            this.bufferSize = bufferSize
            return this
        }

        fun downloadRateLimit(downloadRateLimit: Int): Builder {
            this.downloadRateLimit = downloadRateLimit
            return this
        }

        fun uploadRateLimit(uploadRateLimit: Int): Builder {
            this.uploadRateLimit = uploadRateLimit
            return this
        }

        fun connectionsLimit(connectionsLimit: Int): Builder {
            this.connectionsLimit = connectionsLimit
            return this
        }

        fun dhtNodeMinimum(dhtMin: Int): Builder {
            this.dhtNodeMinimum = dhtMin
            return this
        }

        fun dhtNodeLimit(dhtLimit: Int): Builder {
            this.dhtNodeLimit = dhtLimit
            return this
        }

        fun anonymousMode(useAnonymousMode: Boolean): Builder {
            this.anonymousMode = useAnonymousMode
            return this
        }

    }
}

