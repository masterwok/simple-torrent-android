package com.masterwok.simpletorrentstream.extensions

import com.frostwire.jlibtorrent.Priority
import com.frostwire.jlibtorrent.TorrentHandle


/**
 * Get the largest file index of the [TorrentHandle].
 */
internal fun TorrentHandle.getLargestFileIndex(): Int = torrentFile()
        .files()
        .getLargestFileIndex()

/**
 * Prioritize the largest file of the [TorrentHandle].
 */
internal fun TorrentHandle.prioritizeLargestFile(
        priority: Priority
) = filePriority(getLargestFileIndex(), priority)


/**
 * Ignore all of the files of the [TorrentHandle].
 */
internal fun TorrentHandle.ignoreAllFiles() = prioritizeFiles(
        Array(torrentFile().numFiles()) { Priority.IGNORE }
)

/**
 * Get the first non-ignored piece index of the [TorrentHandle].
 * If a non-ignored piece can't be found, then -1 is returned.
 */
internal fun TorrentHandle.getFirstNonIgnoredPieceIndex(): Int = piecePriorities().indexOfFirst {
    it != Priority.IGNORE
}

/**
 * Get the last non-ignored piece of the [TorrentHandle].
 * If a non-ignored piece can't be found, then -1 is returned.
 */
internal fun TorrentHandle.getLastNonIgnoredPieceIndex(): Int = piecePriorities().indexOfLast {
    it != Priority.IGNORE
}

/**
 * Get the first non-downloaded piece between the range of, [startIndex] and [endIndex]
 * of the [TorrentHandle]. If all pieces have been downloaded, then -1 is returned.
 */
internal fun TorrentHandle.getFirstNonDownloadedPieceInRange(
        startIndex: Int
        , endIndex: Int
): Int = (startIndex..endIndex).indexOfFirst {
    !havePiece(it)
}

/**
 * Set the priorities of the pieces of the [TorrentHandle] so that pieces are downloaded
 * as close to in-order as possible. For example, let n equal some number less than or
 * equal to [bufferSize] where n represents the number of pieces set to the highest
 * download priority. Each of the n pieces will be a piece between the first non-downloaded,
 * non-ignored piece index + [bufferSize].
 */
internal fun TorrentHandle.setBufferPriorities(
        bufferSize: Int
) {
    // Finished, nothing to do..
    if (status().isFinished) {
        return
    }

    val firstNonDownloadedIndex = getFirstNonDownloadedPieceInRange(
            getFirstNonIgnoredPieceIndex()
            , getLastNonIgnoredPieceIndex()
    )

    (firstNonDownloadedIndex..(firstNonDownloadedIndex + bufferSize)).forEach {
        piecePriority(it, Priority.SEVEN)
        setPieceDeadline(it, 1000)
    }
}
