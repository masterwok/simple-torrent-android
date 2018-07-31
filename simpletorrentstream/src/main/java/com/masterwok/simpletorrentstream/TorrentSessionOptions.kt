package com.masterwok.simpletorrentstream

import com.frostwire.jlibtorrent.SessionParams
import com.frostwire.jlibtorrent.SettingsPack
import java.io.File

data class TorrentSessionOptions(
        val onlyDownloadLargestFile: Boolean
        , val shouldStream: Boolean
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
        private var onlyDownloadLargestFile: Boolean = false
        private var shouldStream: Boolean = false
        private var downloadRateLimit: Int = 0
        private var uploadRateLimit: Int = 0
        private var connectionsLimit: Int = 200
        private var dhtLimit: Int = 88
        private var anonymousMode: Boolean = false

        fun build(): TorrentSessionOptions {
            return TorrentSessionOptions(
                    onlyDownloadLargestFile
                    , shouldStream
                    , downloadLocation
                    , downloadRateLimit
                    , uploadRateLimit
                    , connectionsLimit
                    , dhtLimit
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

        fun dhtLimit(dhtLimit: Int): Builder {
            this.dhtLimit = dhtLimit
            return this
        }

        fun anonymousMode(useAnonymousMode: Boolean): Builder {
            this.anonymousMode = useAnonymousMode
            return this
        }

    }
}

