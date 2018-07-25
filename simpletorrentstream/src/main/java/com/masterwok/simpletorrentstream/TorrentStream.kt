package com.masterwok.simpletorrentstream

import android.util.Log
import com.frostwire.jlibtorrent.AlertListener
import com.frostwire.jlibtorrent.Priority
import com.frostwire.jlibtorrent.SessionManager
import com.frostwire.jlibtorrent.TorrentHandle
import com.frostwire.jlibtorrent.alerts.*
import com.masterwok.simpletorrentstream.contracts.TorrentStreamListener
import com.masterwok.simpletorrentstream.extensions.ignoreAllFiles
import com.masterwok.simpletorrentstream.extensions.prioritizeLargestFile
import com.masterwok.simpletorrentstream.extensions.setBufferPriorities
import com.masterwok.simpletorrentstream.models.TorrentStreamStatus
import java.net.URLDecoder


class TorrentStream(
        private val torrentStreamOptions: TorrentStreamOptions
) {
    companion object {
        private const val MaxPrioritizedPieceCount = 8
    }


    // TODO: Indexes missing when torrent was started previously.
    private val downloadedPieceIndexes: ArrayList<Int> = ArrayList()
    private var torrentStreamListener: TorrentStreamListener? = null
    private val sessionManager = SessionManager()
    private val dhtLock = Object()


    private val alertListener = object : AlertListener {
        override fun alert(alert: Alert<*>) {
            Log.d("NON HANDLED ALERT", alert.toString())
            when (alert.type()) {
                AlertType.DHT_BOOTSTRAP -> onDhtBootstrap()
                AlertType.DHT_STATS -> onDhtStats()
                AlertType.METADATA_RECEIVED -> onMetadataReceived(alert as MetadataReceivedAlert)
                AlertType.METADATA_FAILED -> onMetadataFailed(alert as MetadataFailedAlert)
                AlertType.PIECE_FINISHED -> onPieceFinished(alert as PieceFinishedAlert)
                AlertType.TORRENT_DELETE_FAILED -> TODO()
                AlertType.TORRENT_NEED_CERT -> TODO()
                AlertType.TORRENT_CHECKED -> TODO()
                AlertType.TORRENT_DELETED -> TODO()
                AlertType.TORRENT_REMOVED -> TODO()
                AlertType.TORRENT_RESUMED -> TODO()
                AlertType.TORRENT_PAUSED -> TODO()
                AlertType.TORRENT_FINISHED -> onTorrentFinished(alert as TorrentFinishedAlert)
                AlertType.TORRENT_ERROR -> onTorrentError(alert as TorrentErrorAlert)
                AlertType.ADD_TORRENT -> onAddTorrent(alert as AddTorrentAlert)
            }
        }

        override fun types(): IntArray? = null
    }

    private fun onMetadataReceived(metadataReceivedAlert: MetadataReceivedAlert) {
        Log.d("onMetadataReceived", "Metadata Received")

        val torrentHandle = metadataReceivedAlert.handle()
        val torrentInfo = torrentHandle.torrentFile()

        torrentStreamListener?.onMetadataReceived(createTorrentStreamInstance(torrentHandle))

        sessionManager.download(torrentInfo, torrentStreamOptions.downloadLocation)
    }

    private fun createTorrentStreamInstance(torrentHandle: TorrentHandle) =
            TorrentStreamStatus.createInstance(
                    torrentHandle
                    , downloadedPieceIndexes
            )

    private fun onPieceFinished(pieceFinishedAlert: PieceFinishedAlert) {
        val torrentHandle = pieceFinishedAlert.handle()

        downloadedPieceIndexes.add(pieceFinishedAlert.pieceIndex())

        torrentStreamListener?.onPieceFinished(createTorrentStreamInstance(torrentHandle))

        torrentHandle.setBufferPriorities(MaxPrioritizedPieceCount)
    }

    private fun onAddTorrent(addTorrentAlert: AddTorrentAlert) {
        val torrentHandle = addTorrentAlert.handle()

        downloadedPieceIndexes.clear()

        torrentStreamListener?.onAddTorrent(createTorrentStreamInstance(torrentHandle))

        // TODO: Need to clear normal state once first and last piece indexes are determined.

        torrentHandle.ignoreAllFiles()
        torrentHandle.prioritizeLargestFile(Priority.NORMAL)
        torrentHandle.setBufferPriorities(MaxPrioritizedPieceCount)

        addTorrentAlert
                .handle()
                .resume()
    }

    private fun onTorrentError(torrentErrorAlert: TorrentErrorAlert) =
            torrentStreamListener?.onTorrentError(
                    createTorrentStreamInstance(torrentErrorAlert.handle())
            )

    private fun onTorrentFinished(torrentFinishedAlert: TorrentFinishedAlert) =
            torrentStreamListener?.onTorrentFinished(
                    createTorrentStreamInstance(torrentFinishedAlert.handle())
            )

    private fun onMetadataFailed(metadataFailedAlert: MetadataFailedAlert) =
            torrentStreamListener?.onMetadataFailed(
                    createTorrentStreamInstance(metadataFailedAlert.handle())
            )

    private fun onDhtStats() {
        synchronized(dhtLock) {
            if (isDhtReady()) {
                dhtLock.notify()
            }
        }
    }

    private fun onDhtBootstrap() {
        synchronized(dhtLock) {
            dhtLock.notify()
        }
    }

    init {
        sessionManager.addListener(alertListener)
        sessionManager.start(torrentStreamOptions.build())
    }

    private fun isDhtReady() = sessionManager
            .stats()
            .dhtNodes() >= 10

    /**
     * Download the torrent associated with the provided [magnetUri]. Abandon
     * downloading the torrent if the magnet fails to resolve within the provided
     * [timeout] in seconds.
     */
    suspend fun downloadMagnet(
            magnetUri: String
            , timeout: Int
    ): Unit = synchronized(dhtLock) {
        // We must wait for DHT to start
        if (!isDhtReady()) {
            dhtLock.wait()
        }

        sessionManager.fetchMagnet(
                URLDecoder.decode(magnetUri, "utf-8")
                , timeout
        )
    }

    fun setListener(torrentStreamListener: TorrentStreamListener?) {
        this.torrentStreamListener = torrentStreamListener
    }

}

