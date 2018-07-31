package com.masterwok.simpletorrentstream

import com.frostwire.jlibtorrent.SessionParams
import com.frostwire.jlibtorrent.SettingsPack
import java.io.File

data class TorrentSessionOptions(
        val shouldStream: Boolean
        , val downloadLocation: File
        , val downloadRateLimit: Int
        , val uploadRateLimit: Int
        , val connectionsLimit: Int
        , val dhtLimit: Int
        , val anonymousMode: Boolean
) {
    internal fun build(): SessionParams {
        val settingsPack = SettingsPack()
                .downloadRateLimit(downloadRateLimit)
                .uploadRateLimit(uploadRateLimit)
                .connectionsLimit(connectionsLimit)
                .activeDhtLimit(dhtLimit)
                .anonymousMode(anonymousMode)

        return SessionParams(settingsPack)
    }

    @Suppress("unused")
    class Builder constructor(
            private val downloadLocation: File
    ) {
        private var shouldStream: Boolean = false
        private var downloadRateLimit: Int = 0
        private var uploadRateLimit: Int = 0
        private var connectionsLimit: Int = 200
        private var dhtLimit: Int = 88
        private var anonymousMode: Boolean = false

        fun build(): TorrentSessionOptions {
            return TorrentSessionOptions(
                    shouldStream
                    , downloadLocation
                    , downloadRateLimit
                    , uploadRateLimit
                    , connectionsLimit
                    , dhtLimit
                    , anonymousMode
            )
        }

        fun setStreaming(shouldStream: Boolean): Builder {
            this.shouldStream = shouldStream
            return this
        }

        fun setDownloadRateLimit(downloadRateLimit: Int): Builder {
            this.downloadRateLimit = downloadRateLimit
            return this
        }

        fun setUploadRateLimit(uploadRateLimit: Int): Builder {
            this.uploadRateLimit = uploadRateLimit
            return this
        }

        fun setConnectionsLimit(connectionsLimit: Int): Builder {
            this.connectionsLimit = connectionsLimit
            return this
        }

        fun setDhtLimit(dhtLimit: Int): Builder {
            this.dhtLimit = dhtLimit
            return this
        }

        fun setAnonymousMode(anonymousMode: Boolean): Builder {
            this.anonymousMode = anonymousMode
            return this
        }

    }
}

