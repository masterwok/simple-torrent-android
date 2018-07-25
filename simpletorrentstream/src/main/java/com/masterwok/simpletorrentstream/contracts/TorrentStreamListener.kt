package com.masterwok.simpletorrentstream.contracts

import com.masterwok.simpletorrentstream.models.TorrentStreamStatus

interface TorrentStreamListener {

    fun onPieceFinished(torrentStreamStatus: TorrentStreamStatus)

    fun onAddTorrent(torrentStreamStatus: TorrentStreamStatus)

    fun onTorrentError(torrentStreamStatus: TorrentStreamStatus)

    fun onTorrentFinished(torrentStreamStatus: TorrentStreamStatus)

    fun onMetadataFailed(torrentStreamStatus: TorrentStreamStatus)

    fun onMetadataReceived(torrentStreamStatus: TorrentStreamStatus)

}
