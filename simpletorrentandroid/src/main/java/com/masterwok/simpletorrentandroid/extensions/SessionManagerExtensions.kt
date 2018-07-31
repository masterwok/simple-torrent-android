package com.masterwok.simpletorrentandroid.extensions

import android.content.Context
import android.net.Uri
import com.frostwire.jlibtorrent.SessionManager
import com.frostwire.jlibtorrent.TorrentInfo
import java.io.File
import java.net.HttpURLConnection
import java.net.URL


/**
 * Download a torrent from the [torrentUrl] to the [downloadLocation] destination. The
 * download will be aborted if it does not complete before the provided [timeout].
 */
internal fun SessionManager.downloadUsingNetworkUri(
        downloadLocation: File
        , torrentUrl: URL
        , timeout: Int
): Boolean {
    val timeoutMs = timeout * 1000

    val connection = (torrentUrl.openConnection() as HttpURLConnection).apply {
        requestMethod = "GET"
        instanceFollowRedirects = true
        connectTimeout = timeoutMs
        readTimeout = timeoutMs
    }

    try {
        connection.connect()

        if (connection.responseCode != 200) {
            return false
        }

        val data = connection
                .inputStream
                .readBytes()

        if (data.isEmpty()) {
            return false
        }

        download(
                TorrentInfo.bdecode(data)
                , downloadLocation
        )

    } catch (ex: Exception) {
        return false
    }

    return true
}


/**
 * Download the torrent using a content URI. The provided [context] is used
 * to resolve the content resolver.
 */
internal fun SessionManager.downloadUsingContentUri(
        context: Context
        , downloadLocation: File
        , torrentUri: Uri
): Boolean {
    val bytes = context
            .contentResolver
            .openInputStream(torrentUri).readBytes()

    if (bytes.isEmpty()) {
        return false
    }

    download(
            TorrentInfo.bdecode(bytes)
            , downloadLocation
    )

    return true
}