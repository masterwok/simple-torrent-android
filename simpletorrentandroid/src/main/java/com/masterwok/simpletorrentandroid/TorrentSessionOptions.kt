package com.masterwok.simpletorrentandroid

import com.frostwire.jlibtorrent.SettingsPack
import com.frostwire.jlibtorrent.swig.settings_pack
import java.io.File


/**
 * This class provides the options for the [@see TorrentSession].
 *
 * For more information, [@see https://www.libtorrent.org/reference-Settings.html]
 */
@Suppress("MemberVisibilityCanBePrivate", "CanBeParameter")
data class TorrentSessionOptions constructor(

        /**
         * The root directory to download the torrent into.
         */
        val downloadLocation: File

        /**
         * If [onlyDownloadLargestFile] is true, then only the largest file in
         * the torrent is downloaded. Default value is, false.
         */
        , val onlyDownloadLargestFile: Boolean = false

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
        , val anonymousMode: Boolean = false

        /**
         * Enable verbose logging of the torrent session.
         */
        , val enableLogging: Boolean = false

        /**
         * If [shouldStream] is true, then all downloaded files are downloaded
         * sequentially. Default value is, false
         */
        , val shouldStream: Boolean = false

        /**
         * When streaming, the value of [streamBufferSize] is used to determine the maximum
         * number of pieces to prioritize in the [@see TorrentSessionBuffer]. Default
         * value, 8.
         */
        , val streamBufferSize: Int = 8

        /**
         * The session-global limits of upload and download rate limits, in bytes per second.
         * By default peers on the local network are not rate limited. Default value, 0 (infinity).
         */
        , val downloadRateLimit: Int = 0

        /**
         * The session-global limits of upload and download rate limits, in bytes per second.
         * By default peers on the local network are not rate limited. Default value, 0 (infinity).
         */
        , val uploadRateLimit: Int = 0

        /**
         * The global limit on the number of connections opened. The number of connections
         * is set to a hard minimum of at least two per torrent, so if you set a too low
         * connections limit, and open too many torrents, the limit will not be met. Default
         * value, 200.
         */
        , val connectionsLimit: Int = 200

        /**
         * The minimum number of DHT nodes to wait for until magnet link downloads will start.
         * Default value, 10.
         */
        , val dhtNodeMinimum: Int = 10

        /**
         * The max number of torrents to announce to the DHT. By default this is set to 88,
         * which is no more than one DHT announce every 10 seconds.
         */
        , val dhtNodeLimit: Int = 88
) {
    val settingsPack: SettingsPack = SettingsPack()
            .downloadRateLimit(downloadRateLimit)
            .uploadRateLimit(uploadRateLimit)
            .connectionsLimit(connectionsLimit)
            .activeDhtLimit(dhtNodeLimit)
            .anonymousMode(anonymousMode)

    init {
        settingsPack.setString(settings_pack.string_types.dht_bootstrap_nodes.swigValue(), getDhtBootstrapNodeString())
    }

    /**
     * Default list of DHT nodes.
     */
    private fun getDhtBootstrapNodeString(): String =
            "router.bittorrent.com:6681" +
                    ",dht.transmissionbt.com:6881" +
                    ",dht.libtorrent.org:25401" +
                    ",dht.aelitis.com:6881" +
                    ",router.bitcomet.com:6881" +
                    ",router.bitcomet.com:6881" +
                    ",dht.transmissionbt.com:6881" +
                    ",router.silotis.us:6881" // IPv6

}
