package com.masterwok.simpletorrentstream

import android.util.Log
import com.frostwire.jlibtorrent.AlertListener
import com.frostwire.jlibtorrent.Priority
import com.frostwire.jlibtorrent.SessionManager
import com.frostwire.jlibtorrent.alerts.*
import com.masterwok.simpletorrentstream.extensions.*
import java.io.File
import java.net.URLDecoder


class TorrentStream(
        private val sessionOptions: StreamOptions
) {
    companion object {
        private const val MaxPrioritizedPieceCount = 8
    }

    private val sessionManager = SessionManager()
    private val dhtLock = Object()

    private val alertListener = object : AlertListener {
        override fun alert(alert: Alert<*>) {
            Log.d("NON HANDLED ALERT", alert.toString())
            when (alert.type()) {
                AlertType.DHT_BOOTSTRAP -> onDhtBootstrap(alert as DhtBootstrapAlert)
                AlertType.DHT_STATS -> onDhtStats(alert as DhtStatsAlert)
                AlertType.METADATA_FAILED -> onMetadataFailed(alert as MetadataFailedAlert)
                AlertType.METADATA_RECEIVED -> onMetadataReceived(alert as MetadataReceivedAlert)
                AlertType.TORRENT_REMOVED -> TODO()
                AlertType.TORRENT_DELETED -> TODO()
                AlertType.TORRENT_PAUSED -> TODO()
                AlertType.TORRENT_RESUMED -> TODO()
                AlertType.TORRENT_CHECKED -> TODO()
                AlertType.TORRENT_NEED_CERT -> TODO()
                AlertType.TORRENT_DELETE_FAILED -> TODO()
                AlertType.ADD_TORRENT -> onAddTorrent(alert as AddTorrentAlert)
                AlertType.TORRENT_FINISHED -> onTorrentFinished(alert as TorrentFinishedAlert)
                AlertType.TORRENT_ERROR -> onTorrentError(alert as TorrentErrorAlert)
                AlertType.TORRENT_LOG -> onTorrentLog(alert as TorrentLogAlert)
                AlertType.PIECE_FINISHED -> onPieceFinished(alert as PieceFinishedAlert)
                AlertType.BLOCK_FINISHED -> onBlockFinished(alert as BlockFinishedAlert)
            }
        }

        override fun types(): IntArray? = null
    }

    private fun onMetadataReceived(metadataReceivedAlert: MetadataReceivedAlert) {
        Log.d("onMetadataReceived", "Metadata Received")

        val torrentHandle = metadataReceivedAlert.handle()
        val torrentInfo = torrentHandle.torrentFile()

        sessionManager.download(torrentInfo, sessionOptions.downloadLocation)
    }

    private fun onPieceFinished(pieceFinishedAlert: PieceFinishedAlert) {
        val torrentHandle = pieceFinishedAlert.handle()
        val piecePriorities = torrentHandle.piecePriorities()
        val totalBytesWanted = torrentHandle.status().totalWanted()
        val totalBytesDone = torrentHandle.status().totalDone()
        val pieceCount = torrentHandle.torrentFile().numPieces()
        val isFinished = torrentHandle.status().isFinished

        val firstMissingPieceIndex = torrentHandle.getFirstNonDownloadedPieceInRange(
                torrentHandle.getFirstNonIgnoredPieceIndex()
                , torrentHandle.getLastNonIgnoredPieceIndex()
        )

        val progress: Int = (torrentHandle
                .status()
                .progress() * 100).toInt()

        Log.d(
                "onPieceFinished",
                "| Piece Count: ${piecePriorities.size}"
                        + ", Piece: ${pieceFinishedAlert.pieceIndex()}/$pieceCount"
                        + ", First Missing Piece Index: $firstMissingPieceIndex"
                        + ", Progress: $totalBytesDone/$totalBytesWanted ($progress%)"
                        + ", Is Finished: $isFinished"
        )

        torrentHandle.setBufferPriorities(MaxPrioritizedPieceCount)
    }

    private fun onBlockFinished(blockFinishedAlert: BlockFinishedAlert) {
        Log.d("onBlockFinished", "| Block Index: ${blockFinishedAlert.blockIndex()}")
    }

    private fun onAddTorrent(addTorrentAlert: AddTorrentAlert) {
        Log.d("onAddTorrent", "Torrent Added")

        val torrentHandle = addTorrentAlert.handle()

        torrentHandle.ignoreAllFiles()
        torrentHandle.prioritizeLargestFile(Priority.NORMAL)
        torrentHandle.setBufferPriorities(MaxPrioritizedPieceCount)

        addTorrentAlert
                .handle()
                .resume()
    }

    private fun onTorrentError(torrentErrorAlert: TorrentErrorAlert) {
        Log.d("onTorrentError", "Torrent Error Occurred")
    }

    private fun onTorrentLog(torrentLogAlert: TorrentLogAlert) {
        Log.d("onTorrentLog", torrentLogAlert.logMessage())
    }

    private fun onTorrentFinished(torrentFinishedAlert: TorrentFinishedAlert) {
        Log.d("onTorrentFinished", "Torrent Finished")
    }

    private fun onMetadataFailed(metadataFailedAlert: MetadataFailedAlert) {
    }

    private fun onDhtStats(dhtStatsAlert: DhtStatsAlert) {
        synchronized(dhtLock) {
            if (isDhtReady()) {
                dhtLock.notify()
            }
        }
    }


    private fun onDhtBootstrap(dhtBootstrapAlert: DhtBootstrapAlert) {
        synchronized(dhtLock) {
            dhtLock.notify()
        }
    }

    init {
        sessionManager.addListener(alertListener)
        sessionManager.start(sessionOptions.build())
    }

    private fun isDhtReady() = sessionManager
            .stats()
            .dhtNodes() >= 10

    suspend fun downloadMagnet(
            uri: String
    ): Unit = synchronized(dhtLock) {
        // We must wait for DHT to start
        if (!isDhtReady()) {
            dhtLock.wait()
        }

        sessionManager.fetchMagnet(
                URLDecoder.decode(uri, "utf-8")
                , 30
        )
    }

    class Builder {
        private lateinit var downloadLocation: File

        fun build(): StreamOptions = StreamOptions(
                downloadLocation
        )

        fun setDownloadLocation(downloadLocation: File): Builder {
            this.downloadLocation = downloadLocation
            return this
        }
    }
}

