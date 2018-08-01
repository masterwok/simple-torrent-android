package com.masterwok.simpletorrentandroid.contracts

import com.frostwire.jlibtorrent.TorrentHandle
import com.masterwok.simpletorrentandroid.models.TorrentSessionStatus


/**
 * This contract provides callbacks to [@see TorrentSession] status updates.
 */
interface TorrentSessionListener {

    /**
     * Invoked when a piece is finished downloading.
     */
    fun onPieceFinished(
            torrentHandle: TorrentHandle
            , torrentSessionStatus: TorrentSessionStatus
    )

    /**
     * Invoked when the torrent is added.
     */
    fun onAddTorrent(
            torrentHandle: TorrentHandle
            , torrentSessionStatus: TorrentSessionStatus
    )

    /**
     * Invoked when a torrent error occurs.
     */
    fun onTorrentError(
            torrentHandle: TorrentHandle
            , torrentSessionStatus: TorrentSessionStatus
    )

    /**
     * Invoked when the torrent finishes downloading.
     */
    fun onTorrentFinished(
            torrentHandle: TorrentHandle
            , torrentSessionStatus: TorrentSessionStatus
    )

    /**
     * Invoked when fetching metadata for the provided magnet fails.
     */
    fun onMetadataFailed(
            torrentHandle: TorrentHandle
            , torrentSessionStatus: TorrentSessionStatus
    )

    /**
     * Invoked when metadata is successfully fetched.
     */
    fun onMetadataReceived(
            torrentHandle: TorrentHandle
            , torrentSessionStatus: TorrentSessionStatus
    )

    /**
     * Invoked when deleting the torrent fails.
     */
    fun onTorrentDeleteFailed(
            torrentHandle: TorrentHandle
            , torrentSessionStatus: TorrentSessionStatus
    )

    /**
     * Invoked when the torrent is paused.
     */
    fun onTorrentPaused(
            torrentHandle: TorrentHandle
            , torrentSessionStatus: TorrentSessionStatus
    )

    /**
     * Invoked when the torrent is deleted.
     */
    fun onTorrentDeleted(
            torrentHandle: TorrentHandle
            , torrentSessionStatus: TorrentSessionStatus
    )

    /**
     * Invoked when the torrent is removed.
     */
    fun onTorrentRemoved(
            torrentHandle: TorrentHandle
            , torrentSessionStatus: TorrentSessionStatus
    )

    /**
     * Invoked when the torrent is removed.
     */
    fun onTorrentResumed(
            torrentHandle: TorrentHandle
            , torrentSessionStatus: TorrentSessionStatus
    )

    /**
     * Invoked when a block of the torrent is uploaded to a peer.
     */
    fun onBlockUploaded(
            torrentHandle: TorrentHandle
            , torrentSessionStatus: TorrentSessionStatus
    )

}
