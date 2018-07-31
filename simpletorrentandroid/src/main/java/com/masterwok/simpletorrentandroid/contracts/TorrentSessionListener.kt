package com.masterwok.simpletorrentandroid.contracts

import com.masterwok.simpletorrentandroid.models.TorrentSessionStatus

interface TorrentSessionListener {

    fun onPieceFinished(torrentSessionStatus: TorrentSessionStatus)

    fun onAddTorrent(torrentSessionStatus: TorrentSessionStatus)

    fun onTorrentError(torrentSessionStatus: TorrentSessionStatus)

    fun onTorrentFinished(torrentSessionStatus: TorrentSessionStatus)

    fun onMetadataFailed(torrentSessionStatus: TorrentSessionStatus)

    fun onMetadataReceived(torrentSessionStatus: TorrentSessionStatus)

    fun onTorrentDeleteFailed(torrentSessionStatus: TorrentSessionStatus)

    fun onTorrentPaused(torrentSessionStatus: TorrentSessionStatus)

    fun onTorrentDeleted(torrentSessionStatus: TorrentSessionStatus)

    fun onTorrentRemoved(torrentSessionStatus: TorrentSessionStatus)

    fun onTorrentResumed(torrentSessionStatus: TorrentSessionStatus)

    fun onBlockUploaded(torrentSessionStatus: TorrentSessionStatus)

}
