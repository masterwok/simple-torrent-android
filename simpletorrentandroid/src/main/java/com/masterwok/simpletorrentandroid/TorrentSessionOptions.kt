package com.masterwok.simpletorrentandroid

import com.frostwire.jlibtorrent.SessionParams
import com.frostwire.jlibtorrent.SettingsPack
import java.io.File


/**
 * This class provides the options for the [@see TorrentSession].
 *
 * For more information, [@see https://www.libtorrent.org/reference-Settings.html]
 */
@Suppress("MemberVisibilityCanBePrivate")
class TorrentSessionOptions private constructor(
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

        /**
         * Build the [TorrentSessionOptions] instance.
         */
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

        /**
         * If [onlyDownloadLargestFile] is true, then only the largest file in
         * the torrent is downloaded. Default value is, false.
         */
        fun onlyDownloadLargestFile(onlyDownloadLargestFile: Boolean): Builder {
            this.onlyDownloadLargestFile = onlyDownloadLargestFile
            return this
        }

        /**
         * If [shouldStream] is true, then all downloaded files are downloaded
         * sequentially. Default value is, false
         */
        fun stream(shouldStream: Boolean): Builder {
            this.shouldStream = shouldStream
            return this
        }

        /**
         * When streaming, the value of [bufferSize] is used to determine the maximum
         * number of pieces to prioritize in the [@see TorrentSessionBuffer]. Default
         * value, 8.
         */
        fun streamBufferSize(bufferSize: Int): Builder {
            this.bufferSize = bufferSize
            return this
        }

        /**
         * The session-global limits of upload and download rate limits, in bytes per second.
         * By default peers on the local network are not rate limited. Default value, 0 (infinity).
         */
        fun downloadRateLimit(downloadRateLimit: Int): Builder {
            this.downloadRateLimit = downloadRateLimit
            return this
        }

        /**
         * The session-global limits of upload and download rate limits, in bytes per second.
         * By default peers on the local network are not rate limited. Default value, 0 (infinity).
         */
        fun uploadRateLimit(uploadRateLimit: Int): Builder {
            this.uploadRateLimit = uploadRateLimit
            return this
        }

        /**
         * The global limit on the number of connections opened. The number of connections
         * is set to a hard minimum of at least two per torrent, so if you set a too low
         * connections limit, and open too many torrents, the limit will not be met. Default
         * value, 200.
         */
        fun connectionsLimit(connectionsLimit: Int): Builder {
            this.connectionsLimit = connectionsLimit
            return this
        }

        /**
         * The minimum number of DHT nodes to wait for until magnet link downloads will start.
         * Default value, 10.
         */
        fun dhtNodeMinimum(dhtMin: Int): Builder {
            this.dhtNodeMinimum = dhtMin
            return this
        }

        /**
         * The max number of torrents to announce to the DHT. By default this is set to 88,
         * which is no more than one DHT announce every 10 seconds.
         */
        fun dhtNodeLimit(dhtLimit: Int): Builder {
            this.dhtNodeLimit = dhtLimit
            return this
        }

        /**
         * When [useAnonymousMode]is true, the client tries to hide its identity to a certain
         * degree. The peer-ID will no longer include the client's fingerprint. The user-agent
         * will be reset to an empty string. Trackers will only be used if they are using
         * a proxy server. The listen sockets are closed, and incoming connections will
         * only be accepted through a SOCKS5 or I2P proxy (if a peer proxy is set up and
         * is run on the same machine as the tracker proxy). Since no incoming
         * connections are accepted, NAT-PMP, UPnP, DHT and local peer discovery are all
         * turned off when this setting is enabled.
         */
        fun anonymousMode(useAnonymousMode: Boolean): Builder {
            this.anonymousMode = useAnonymousMode
            return this
        }

    }
}

