package com.masterwok.simpletorrentstream.contracts

import com.masterwok.simpletorrentstream.models.TorrentSessionStatus

interface TorrentStreamListener {

    fun onPieceFinished(torrentSessionStatus: TorrentSessionStatus)

    fun onAddTorrent(torrentSessionStatus: TorrentSessionStatus)

    fun onTorrentError(torrentSessionStatus: TorrentSessionStatus)

    fun onTorrentFinished(torrentSessionStatus: TorrentSessionStatus)

    fun onMetadataFailed(torrentSessionStatus: TorrentSessionStatus)

    fun onMetadataReceived(torrentSessionStatus: TorrentSessionStatus)

}
