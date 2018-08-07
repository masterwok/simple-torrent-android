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
import java.security.InvalidParameterException


/**
 * This class is used to control a torrent download session for the provided [torrentUri].
 * It is configured using the provided [torrentSessionOptions].
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
class TorrentSession(
        val torrentUri: Uri
        , private val torrentSessionOptions: TorrentSessionOptions
) : SessionManager(
        torrentSessionOptions.enableLogging
) {
    companion object {
        private const val Tag = "TorrentSession"
    }

    /**
     * The provided [listener] will receive status updates during the torrent download.
     */
    var listener: TorrentSessionListener? = null

    private lateinit var torrentSessionBuffer: TorrentSessionBuffer
    private var saveLocationUri: Uri = Uri.EMPTY
    private var largestFileUri: Uri = Uri.EMPTY

    private val sessionParams = SessionParams(torrentSessionOptions.settingsPack)
    private val alertListener = TorrentSessionAlertListener(this)

    init {
        if (!hasValidTorrentUri) {
            throw InvalidParameterException("Unrecognized torrent URI: $torrentUri")
        }

        addListener(alertListener)
    }

    private fun isTorrentPaused(torrentHandle: TorrentHandle): Boolean =
            torrentHandle.status().flags().and_(TorrentFlags.PAUSED).nonZero()
                    || isPaused

    private val hasValidTorrentUri: Boolean
        get() {
            val path = torrentUri.toString()

            return path.startsWith("magnet")
                    || URLUtil.isNetworkUrl(path)
                    || URLUtil.isFileUrl(path)
                    || URLUtil.isContentUrl(path)
        }

    private fun createSessionStatus(torrentHandle: TorrentHandle): TorrentSessionStatus =
            TorrentSessionStatus.createInstance(
                    torrentUri
                    , torrentHandle
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

    private fun isDhtReady() = stats().dhtNodes() >= torrentSessionOptions.dhtNodeMinimum

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

        if (torrentSessionOptions.onlyDownloadLargestFile) {
            torrentHandle.ignoreAllFiles()
            torrentHandle.prioritizeLargestFile(Priority.NORMAL)
        }

        torrentSessionBuffer = TorrentSessionBuffer(
                torrentSessionBuffer.bufferSize
                , torrentHandle.getFirstNonIgnoredPieceIndex()
                , torrentHandle.getLastNonIgnoredPieceIndex()
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

        download(
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

        download(
                TorrentInfo.bdecode(bytes)
                , downloadLocation
        )
    }

    /**
     * Attempt to start a torrent download. The provided [Context] is used to resolve
     * an input stream from the content resolver when the URI is a file or content scheme.
     */
    fun start(context: Context) {
        val path = torrentUri.toString()

        saveLocationUri = Uri.EMPTY
        largestFileUri = Uri.EMPTY

        torrentSessionBuffer = TorrentSessionBuffer(
                bufferSize = if (torrentSessionOptions.shouldStream) torrentSessionOptions.streamBufferSize else 0
        )

        if (!isRunning) {
            start(sessionParams)
        }

        if (path.startsWith("magnet")) {
            download(
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


    override fun stop() {
        super.stop()

        removeListener(alertListener)
    }

}

