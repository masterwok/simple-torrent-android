package com.masterwok.simpletorrentandroid.extensions

import com.frostwire.jlibtorrent.FileStorage


/**
 * Get the index of the largest file within the current [FileStorage].
 * If no files exist, then -1 is returned.
 */
internal fun FileStorage.getLargestFileIndex(): Int {
    val fileCount = numFiles()
    var largestFileSize = 0L
    var largestFileIndex = -1

    // No files, return invalid index..
    if (fileCount == 0) {
        return -1
    }

    for (i in 0..(fileCount - 1)) {
        val fileSize = fileSize(i)

        if (fileSize > largestFileSize) {
            largestFileSize = fileSize
            largestFileIndex = i
        }
    }

    return largestFileIndex
}
