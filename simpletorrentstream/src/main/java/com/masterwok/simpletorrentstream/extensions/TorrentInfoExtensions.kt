package com.masterwok.simpletorrentstream.extensions

import com.frostwire.jlibtorrent.TorrentInfo

/**
 * Get the largest file index of the [TorrentInfo].
 */
internal fun TorrentInfo.getLargestFileIndex(): Int = files().getLargestFileIndex()

/**
 * Get the file name of the largest file of the [TorrentInfo].
 */
internal fun TorrentInfo.getLargestFileName(): String = files().fileName(getLargestFileIndex())


