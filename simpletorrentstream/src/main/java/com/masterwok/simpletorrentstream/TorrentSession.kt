package com.masterwok.simpletorrentstream

import android.util.Log
import com.frostwire.jlibtorrent.AlertListener
import com.frostwire.jlibtorrent.Priority
import com.frostwire.jlibtorrent.SessionManager
import com.frostwire.jlibtorrent.TorrentHandle
import com.frostwire.jlibtorrent.alerts.*
import com.masterwok.simpletorrentstream.contracts.TorrentSessionListener
import com.masterwok.simpletorrentstream.extensions.ignoreAllFiles
import com.masterwok.simpletorrentstream.extensions.prioritizeLargestFile
import com.masterwok.simpletorrentstream.extensions.setBufferPriorities
import com.masterwok.simpletorrentstream.models.TorrentSessionStatus
import java.net.URLDecoder


// TODO: Add remove torrent method.
// TODO: Implement multiple torrent downloads.
class TorrentSession(
        private val torrentSessionOptions: TorrentSessionOptions
) {
    companion object {
        private const val MaxPrioritizedPieceCount = 8
    }

    // TODO: Indexes missing when torrent was started previously.
    private val downloadedPieceIndexes: ArrayList<Int> = ArrayList()
    private var torrentSessionListener: TorrentSessionListener? = null
    private val sessionManager = SessionManager()
    private val dhtLock = Object()


    // TODO: It appears add/remove events are being dispatched multiple times.
    private val alertListener = object : AlertListener {
        override fun alert(alert: Alert<*>) {
            when (alert.type()) {
                AlertType.DHT_BOOTSTRAP -> onDhtBootstrap()
                AlertType.DHT_STATS -> onDhtStats()
                AlertType.METADATA_RECEIVED -> onMetadataReceived(alert as MetadataReceivedAlert)
                AlertType.METADATA_FAILED -> onMetadataFailed(alert as MetadataFailedAlert)
                AlertType.PIECE_FINISHED -> onPieceFinished(alert as PieceFinishedAlert)
                AlertType.TORRENT_DELETE_FAILED -> onTorrentDeleteFailed(alert as TorrentDeleteFailedAlert)
                AlertType.TORRENT_DELETED -> onTorrentDeleted(alert as TorrentDeletedAlert)
                AlertType.TORRENT_REMOVED -> onTorrentRemoved(alert as TorrentRemovedAlert)
                AlertType.TORRENT_RESUMED -> onTorrentResumed(alert as TorrentResumedAlert)
                AlertType.TORRENT_PAUSED -> onTorrentPaused(alert as TorrentPausedAlert)
                AlertType.TORRENT_FINISHED -> onTorrentFinished(alert as TorrentFinishedAlert)
                AlertType.TORRENT_ERROR -> onTorrentError(alert as TorrentErrorAlert)
                AlertType.ADD_TORRENT -> onAddTorrent(alert as AddTorrentAlert)
                else -> Log.d("NON HANDLED ALERT", alert.toString())
            }
        }

        override fun types(): IntArray? = null
    }

    private fun createTorrentSessionStatus(torrentHandle: TorrentHandle) =
            TorrentSessionStatus.createInstance(
                    torrentHandle
                    , downloadedPieceIndexes
            )

    private fun onTorrentDeleteFailed(torrentDeleteFailedAlert: TorrentDeleteFailedAlert) {
        val torrentHandle = torrentDeleteFailedAlert.handle()

        torrentSessionListener?.onTorrentDeleteFailed(createTorrentSessionStatus(torrentHandle))
    }

    private fun onTorrentPaused(torrentPausedAlert: TorrentPausedAlert) {
        val torrentHandle = torrentPausedAlert.handle()

        torrentSessionListener?.onTorrentPaused(createTorrentSessionStatus(torrentHandle))
    }

    private fun onTorrentResumed(torrentResumedAlert: TorrentResumedAlert) {
        val torrentHandle = torrentResumedAlert.handle()

        torrentSessionListener?.onTorrentResumed(createTorrentSessionStatus(torrentHandle))
    }

    private fun onTorrentRemoved(torrentRemovedAlert: TorrentRemovedAlert) {
        val torrentHandle = torrentRemovedAlert.handle()

        torrentSessionListener?.onTorrentRemoved(createTorrentSessionStatus(torrentHandle))
    }

    private fun onTorrentDeleted(torrentDeletedAlert: TorrentDeletedAlert) {
        val torrentHandle = torrentDeletedAlert.handle()

        torrentSessionListener?.onTorrentDeleted(createTorrentSessionStatus(torrentHandle))
    }

    private fun onMetadataReceived(metadataReceivedAlert: MetadataReceivedAlert) {
        val torrentHandle = metadataReceivedAlert.handle()

        torrentSessionListener?.onMetadataReceived(createTorrentSessionStatus(torrentHandle))

        sessionManager.download(
                torrentHandle.torrentFile()
                , torrentSessionOptions.downloadLocation
        )
    }

    private fun onPieceFinished(pieceFinishedAlert: PieceFinishedAlert) {
        val torrentHandle = pieceFinishedAlert.handle()

        downloadedPieceIndexes.add(pieceFinishedAlert.pieceIndex())

        torrentSessionListener?.onPieceFinished(createTorrentSessionStatus(torrentHandle))

        torrentHandle.setBufferPriorities(MaxPrioritizedPieceCount)
    }

    private fun onAddTorrent(addTorrentAlert: AddTorrentAlert) {
        val torrentHandle = addTorrentAlert.handle()

        downloadedPieceIndexes.clear()

        torrentSessionListener?.onAddTorrent(createTorrentSessionStatus(torrentHandle))

        torrentHandle.ignoreAllFiles()
        torrentHandle.prioritizeLargestFile(Priority.NORMAL)
        torrentHandle.setBufferPriorities(MaxPrioritizedPieceCount)

        addTorrentAlert
                .handle()
                .resume()
    }

    private fun onTorrentError(torrentErrorAlert: TorrentErrorAlert) =
            torrentSessionListener?.onTorrentError(
                    createTorrentSessionStatus(torrentErrorAlert.handle())
            )

    private fun onTorrentFinished(torrentFinishedAlert: TorrentFinishedAlert) =
            torrentSessionListener?.onTorrentFinished(
                    createTorrentSessionStatus(torrentFinishedAlert.handle())
            )

    private fun onMetadataFailed(metadataFailedAlert: MetadataFailedAlert) =
            torrentSessionListener?.onMetadataFailed(
                    createTorrentSessionStatus(metadataFailedAlert.handle())
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
        sessionManager.start(torrentSessionOptions.build())
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

    fun setListener(torrentSessionListener: TorrentSessionListener?) {
        this.torrentSessionListener = torrentSessionListener
    }

}

