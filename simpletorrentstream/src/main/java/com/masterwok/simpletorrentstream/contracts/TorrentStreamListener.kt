package com.masterwok.simpletorrentstream.contracts

import com.masterwok.simpletorrentstream.models.TorrentStreamStatus

interface TorrentStreamListener {
    fun onPieceFinished(torrentStreamStatus: TorrentStreamStatus)
}
