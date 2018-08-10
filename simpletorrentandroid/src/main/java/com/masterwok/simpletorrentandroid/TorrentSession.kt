package com.masterwok.simpletorrentandroid

import android.content.Context
import android.net.Uri
import android.util.Log
import android.webkit.URLUtil
import com.frostwire.jlibtorrent.*
import com.frostwire.jlibtorrent.alerts.*
import com.masterwok.simpletorrentandroid.contracts.TorrentSessionListener
import com.masterwok.simpletorrentandroid.extensions.*
import com.masterwok.simpletorrentandroid.models.TorrentSessionBuffer
import com.masterwok.simpletorrentandroid.models.TorrentSessionStatus
import java.io.File
import java.lang.ref.WeakReference
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLDecoder
import java.security.InvalidParameterException


/**
 * This class is used to control a torrent download session.
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
open class TorrentSession(
        private val torrentSessionOptions: TorrentSessionOptions
) {
    companion object {
        private const val Tag = "TorrentSession"
    }

    /**
     * The provided [listener] will receive status updates during the torrent download.
     */
    var listener: TorrentSessionListener? = null

    private val sessionParams = SessionParams(torrentSessionOptions.settingsPack)
    private val alertListener = TorrentSessionAlertListener(this)

    private val sessionManager = SessionManager(torrentSessionOptions.enableLogging)

    private lateinit var torrentSessionBuffer: TorrentSessionBuffer

    private var shouldDownloadMagnetOnResume: Boolean = false
    private var bencode: ByteArray = ByteArray(0)
    private var saveLocationUri: Uri = Uri.EMPTY
    private var largestFileUri: Uri = Uri.EMPTY
    private var magnetUri: Uri = Uri.EMPTY

    init {
        sessionManager.addListener(alertListener)
    }

    private fun isTorrentPaused(torrentHandle: TorrentHandle): Boolean =
            torrentHandle.status().flags().and_(TorrentFlags.PAUSED).nonZero()
                    || sessionManager.isPaused

    private fun isValidTorrentUri(torrentUri: Uri): Boolean {
        val path = torrentUri.toString()

        return path.startsWith("magnet")
                || URLUtil.isNetworkUrl(path)
                || URLUtil.isFileUrl(path)
                || URLUtil.isContentUrl(path)
    }

    private fun createSessionStatus(torrentHandle: TorrentHandle): TorrentSessionStatus =
            TorrentSessionStatus.createInstance(
                    magnetUri
                    , torrentHandle
                    , bencode
                    , torrentSessionBuffer
                    , saveLocationUri
                    , largestFileUri
            )

    private class TorrentSessionAlertListener(
            torrentSession: TorrentSession
    ) : AlertListener {

        private val torrentSession: WeakReference<TorrentSession> = WeakReference(torrentSession)

        @Synchronized
        override fun alert(alert: Alert<*>) {
            try {
                if (alert.isTorrentAlert() && !alert.hasValidTorrentHandle()) {
                    Log.w(Tag, "Ignoring alert with invalid torrent handle: ${alert.type()}")
                    return
                }

                when (alert.type()) {
                    AlertType.DHT_BOOTSTRAP -> torrentSession.get()?.onDhtBootstrap()
                    AlertType.DHT_STATS -> torrentSession.get()?.onDhtStats()
                    AlertType.METADATA_RECEIVED -> torrentSession.get()?.onMetadataReceived(alert as MetadataReceivedAlert)
                    AlertType.METADATA_FAILED -> torrentSession.get()?.onMetadataFailed(alert as MetadataFailedAlert)
                    AlertType.PIECE_FINISHED -> torrentSession.get()?.onPieceFinished(alert as PieceFinishedAlert)
                    AlertType.TORRENT_DELETE_FAILED -> torrentSession.get()?.onTorrentDeleteFailed(alert as TorrentDeleteFailedAlert)
                    AlertType.TORRENT_DELETED -> torrentSession.get()?.onTorrentDeleted(alert as TorrentDeletedAlert)
                    AlertType.TORRENT_REMOVED -> torrentSession.get()?.onTorrentRemoved(alert as TorrentRemovedAlert)
                    AlertType.TORRENT_RESUMED -> torrentSession.get()?.onTorrentResumed(alert as TorrentResumedAlert)
                    AlertType.TORRENT_PAUSED -> torrentSession.get()?.onTorrentPaused(alert as TorrentPausedAlert)
                    AlertType.TORRENT_FINISHED -> torrentSession.get()?.onTorrentFinished(alert as TorrentFinishedAlert)
                    AlertType.TORRENT_ERROR -> torrentSession.get()?.onTorrentError(alert as TorrentErrorAlert)
                    AlertType.ADD_TORRENT -> torrentSession.get()?.onAddTorrent(alert as AddTorrentAlert)
                    AlertType.BLOCK_UPLOADED -> torrentSession.get()?.onBlockUploaded(alert as BlockUploadedAlert)
                    else -> Log.d(Tag, "Unhandled alert: $alert")
                }
            } catch (e: Exception) {
                Log.e(Tag, "An exception occurred within torrent session callback", e)
            }
        }

        override fun types(): IntArray? = null
    }

    private fun isDhtReady() = sessionManager.stats().dhtNodes() >= torrentSessionOptions.dhtNodeMinimum

    private val dhtLock = Object()

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

    private fun onMetadataReceived(metadataReceivedAlert: MetadataReceivedAlert) {
        val torrentHandle = metadataReceivedAlert.handle()

        setInitialTorrentState(torrentHandle)

        listener?.onMetadataReceived(
                torrentHandle
                , createSessionStatus(torrentHandle)
        )
    }

    private fun setInitialTorrentState(torrentHandle: TorrentHandle) {
        if (torrentHandle.torrentFile() == null) {
            return
        }

        largestFileUri = torrentHandle.getLargestFileUri(torrentSessionOptions.downloadLocation)
        saveLocationUri = Uri.fromFile(torrentSessionOptions.downloadLocation)
        bencode = torrentHandle.getBencode()
        magnetUri = Uri.parse(URLDecoder.decode(torrentHandle.makeMagnetUri(), "utf-8"))

        if (torrentSessionOptions.onlyDownloadLargestFile) {
            torrentHandle.ignoreAllFiles()
            torrentHandle.prioritizeLargestFile(Priority.NORMAL)
        }

        val largestFileIndex = torrentHandle.getLargestFileIndex()

        torrentSessionBuffer = TorrentSessionBuffer(
                torrentSessionBuffer.bufferSize
                , torrentHandle.getStartPieceIndex(largestFileIndex)
                , torrentHandle.getEndPieceIndex(largestFileIndex)
        )

        if (torrentSessionOptions.shouldStream) {
            torrentHandle.setBufferPriorities(torrentSessionBuffer)
        }
    }

    private fun onAddTorrent(addTorrentAlert: AddTorrentAlert) {
        val torrentHandle = addTorrentAlert.handle()

        setInitialTorrentState(torrentHandle)

        listener?.onAddTorrent(
                torrentHandle
                , createSessionStatus(torrentHandle)
        )

        addTorrentAlert
                .handle()
                .resume()
    }

    private fun onPieceFinished(pieceFinishedAlert: PieceFinishedAlert) {
        val torrentHandle = pieceFinishedAlert.handle()

        val pieceIndex = pieceFinishedAlert.pieceIndex()

        if (pieceIndex < torrentSessionBuffer.startIndex || pieceIndex > torrentSessionBuffer.endIndex) {
            Log.w(Tag, "Out of range piece downloaded.")
            return
        }

        torrentSessionBuffer.setPieceDownloaded(pieceIndex)

        if (torrentSessionOptions.shouldStream) {
            torrentHandle.setBufferPriorities(torrentSessionBuffer)
        }

        listener?.onPieceFinished(
                torrentHandle
                , createSessionStatus(torrentHandle)
        )
    }

    private fun onMetadataFailed(metadataFailedAlert: MetadataFailedAlert) {
        val torrentHandle = metadataFailedAlert.handle()

        listener?.onMetadataFailed(
                torrentHandle
                , createSessionStatus(torrentHandle)
        )
    }

    private fun onTorrentDeleteFailed(torrentDeleteFailedAlert: TorrentDeleteFailedAlert) {
        val torrentHandle = torrentDeleteFailedAlert.handle()

        listener?.onTorrentDeleteFailed(
                torrentHandle
                , createSessionStatus(torrentHandle)
        )
    }

    private fun onTorrentPaused(torrentPausedAlert: TorrentPausedAlert) {
        val torrentHandle = torrentPausedAlert.handle()

        // When the user pauses the session before the magnet is added, and then
        // resumes the session, an extraneous TorrentPausedAlert occurs after the
        // TorrentResumedAlert. Guard against this case as the session is not
        // really paused at this point.
        if (!isTorrentPaused(torrentHandle)) {
            return
        }

        listener?.onTorrentPaused(
                torrentHandle
                , createSessionStatus(torrentHandle)
        )
    }

    private fun onTorrentResumed(torrentResumedAlert: TorrentResumedAlert) {
        val torrentHandle = torrentResumedAlert.handle()

        listener?.onTorrentResumed(
                torrentHandle
                , createSessionStatus(torrentHandle)
        )
    }

    private fun onTorrentRemoved(torrentRemovedAlert: TorrentRemovedAlert) {
        val torrentHandle = torrentRemovedAlert.handle()

        listener?.onTorrentRemoved(
                torrentHandle
                , createSessionStatus(torrentHandle)
        )
    }

    private fun onTorrentDeleted(torrentDeletedAlert: TorrentDeletedAlert) {
        val torrentHandle = torrentDeletedAlert.handle()

        listener?.onTorrentDeleted(
                torrentHandle
                , createSessionStatus(torrentHandle)
        )
    }

    private fun onTorrentError(torrentErrorAlert: TorrentErrorAlert) {
        val torrentHandle = torrentErrorAlert.handle()

        listener?.onTorrentError(
                torrentHandle
                , createSessionStatus(torrentHandle)
        )
    }

    private fun onTorrentFinished(torrentFinishedAlert: TorrentFinishedAlert) {
        val torrentHandle = torrentFinishedAlert.handle()

        listener?.onTorrentFinished(
                torrentHandle
                , createSessionStatus(torrentHandle)
        )
    }

    private fun onBlockUploaded(blockUploadedAlert: BlockUploadedAlert) {
        val torrentHandle = blockUploadedAlert.handle()

        listener?.onBlockUploaded(
                torrentHandle
                , createSessionStatus(torrentHandle)
        )
    }


    /**
     * Download a torrent from the [torrentUrl] to the [downloadLocation] destination.
     */
    private fun downloadUsingNetworkUri(
            downloadLocation: File
            , torrentUrl: URL
    ) {
        val connection = (torrentUrl.openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            instanceFollowRedirects = true
        }

        connection.connect()

        if (connection.responseCode != 200) {
            Log.w(Tag, "Unexpected response code returned from server: ${connection.responseCode}")
        }

        val data = connection
                .inputStream
                .readBytes()

        sessionManager.download(
                TorrentInfo.bdecode(data)
                , downloadLocation
        )
    }


    /**
     * Download the torrent using a content URI. The provided [context] is used
     * to resolve the content resolver.
     */
    private fun downloadUsingContentUri(
            context: Context
            , downloadLocation: File
            , torrentUri: Uri
    ) {
        val bytes = context
                .contentResolver
                .openInputStream(torrentUri)
                .readBytes()

        sessionManager.download(
                TorrentInfo.bdecode(bytes)
                , downloadLocation
        )
    }

    private fun downloadUsingMagnetUri(
            magnetUrl: String
            , downloadLocation: File
    ) = synchronized(dhtLock) {
        shouldDownloadMagnetOnResume = false

        // We must wait for DHT to start
        if (!isDhtReady()) {
            dhtLock.wait()
        }

        // Session was paused while waiting on DHT, defer until resume
        if (sessionManager.isPaused) {
            shouldDownloadMagnetOnResume = true
            return
        }

        sessionManager.download(
                URLDecoder.decode(magnetUrl, "utf-8")
                , downloadLocation
        )
    }

    fun resume() {
        sessionManager.resume()

        // Session was paused before DHT could become active, retry magnet download.
        if (shouldDownloadMagnetOnResume) {
            downloadUsingMagnetUri(
                    magnetUri.toString()
                    , torrentSessionOptions.downloadLocation
            )
        }
    }

    private fun setInitialStartState() {
        bencode = ByteArray(0)
        saveLocationUri = Uri.EMPTY
        largestFileUri = Uri.EMPTY

        torrentSessionBuffer = TorrentSessionBuffer(
                bufferSize = if (torrentSessionOptions.shouldStream) torrentSessionOptions.streamBufferSize else 0
        )
    }

    /**
     * Start the session using BEncoded data contained within the [bencode] [ByteArray].
     */
    fun start(bencode: ByteArray) {
        setInitialStartState()

        if (!sessionManager.isRunning) {
            sessionManager.start(sessionParams)
        }

        sessionManager.download(
                TorrentInfo.bdecode(bencode)
                , torrentSessionOptions.downloadLocation
        )
    }

    /**
     * Attempt to start a torrent download. The provided [Context] is used to resolve
     * an input stream from the content resolver when the URI is a file or content scheme.
     */
    fun start(context: Context, torrentUri: Uri) {
        setInitialStartState()

        if (!isValidTorrentUri(torrentUri)) {
            throw InvalidParameterException("Unrecognized torrent URI: $torrentUri")
        }

        val path = torrentUri.toString()

        if (!sessionManager.isRunning) {
            sessionManager.start(sessionParams)
        }

        if (path.startsWith("magnet")) {
            downloadUsingMagnetUri(
                    path
                    , torrentSessionOptions.downloadLocation
            )
        } else if (URLUtil.isNetworkUrl(path)) {
            downloadUsingNetworkUri(
                    torrentSessionOptions.downloadLocation
                    , URL(path)
            )

        } else if (URLUtil.isFileUrl(path) || URLUtil.isContentUrl(path)) {
            downloadUsingContentUri(
                    context
                    , torrentSessionOptions.downloadLocation
                    , torrentUri
            )
        }
    }

    val uploadRate: Long get() = sessionManager.uploadRate()

    val downloadRate: Long get() = sessionManager.downloadRate()

    val isPaused: Boolean get() = sessionManager.isPaused

    val isRunning: Boolean get() = sessionManager.isRunning

    fun pause() = sessionManager.pause()

    fun stop() {
        sessionManager.stop()

        sessionManager.removeListener(alertListener)
    }

}

