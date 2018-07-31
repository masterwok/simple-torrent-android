package com.masterwok.simpletorrentandroid.extensions

import com.frostwire.jlibtorrent.TorrentInfo


/**
 * Get the largest file index of the [TorrentInfo].
 */
internal fun TorrentInfo.getLargestFileIndex(): Int = files().getLargestFileIndex()
