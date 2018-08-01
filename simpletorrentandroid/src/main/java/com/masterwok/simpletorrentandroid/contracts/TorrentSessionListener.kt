package com.masterwok.simpletorrentandroid.contracts

import com.masterwok.simpletorrentandroid.models.TorrentSessionStatus


/**
 * This contract provides callbacks to [@see TorrentSession] status updates.
 */
interface TorrentSessionListener {

    /**
     * Invoked when a piece is finished downloading.
     */
    fun onPieceFinished(torrentSessionStatus: TorrentSessionStatus)

    /**
     * Invoked when the torrent is added.
     */
    fun onAddTorrent(torrentSessionStatus: TorrentSessionStatus)

    /**
     * Invoked when a torrent error occurs.
     */
    fun onTorrentError(torrentSessionStatus: TorrentSessionStatus)

    /**
     * Invoked when the torrent finishes downloading.
     */
    fun onTorrentFinished(torrentSessionStatus: TorrentSessionStatus)

    /**
     * Invoked when fetching metadata for the provided magnet fails.
     */
    fun onMetadataFailed(torrentSessionStatus: TorrentSessionStatus)

    /**
     * Invoked when metadata is successfully fetched.
     */
    fun onMetadataReceived(torrentSessionStatus: TorrentSessionStatus)

    /**
     * Invoked when deleting the torrent fails.
     */
    fun onTorrentDeleteFailed(torrentSessionStatus: TorrentSessionStatus)

    /**
     * Invoked when the torrent is paused.
     */
    fun onTorrentPaused(torrentSessionStatus: TorrentSessionStatus)

    /**
     * Invoked when the torrent is deleted.
     */
    fun onTorrentDeleted(torrentSessionStatus: TorrentSessionStatus)

    /**
     * Invoked when the torrent is removed.
     */
    fun onTorrentRemoved(torrentSessionStatus: TorrentSessionStatus)

    /**
     * Invoked when the torrent is removed.
     */
    fun onTorrentResumed(torrentSessionStatus: TorrentSessionStatus)

    /**
     * Invoked when a block of the torrent is uploaded to a peer.
     */
    fun onBlockUploaded(torrentSessionStatus: TorrentSessionStatus)

}
