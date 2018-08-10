package com.masterwok.simpletorrentandroid.models


/**
 * This buffer contains the current download state of torrent pieces and is
 * used internally to keep track of which pieces to prioritize when streaming.
 */
@Suppress("CanBeParameter", "MemberVisibilityCanBePrivate")
class TorrentSessionBuffer constructor(
        val bufferSize: Int = 0
        , val startIndex: Int = 0
        , val endIndex: Int = 0
) {
    @Suppress("unused")
    constructor() : this(0, 0, 0)

    /**
     * The total number of pieces.
     */
    val pieceCount = if (startIndex == 0 && endIndex == 0) 0 else (endIndex - startIndex) + 1

    /**
     * The number of pieces downloaded.
     */
    var downloadedPieceCount = 0
        @Synchronized
        get() = field
        private set(value) {
            field = value
        }

    /**
     * The index of the head of the buffer.
     */
    var bufferHeadIndex = startIndex
        @Synchronized
        get() = field
        private set(value) {
            field = value
        }

    /**
     * The index of the tail of the buffer.
     */
    var bufferTailIndex = if (bufferSize == 0) endIndex else startIndex + bufferSize - 1
        @Synchronized
        get() = field
        private set(value) {
            field = value
        }

    /**
     * The index of the last downloaded piece. If no pieces were downloaded, then
     * this value will be -1.
     */
    var lastDownloadedPieceIndex = -1
        @Synchronized
        get() = field
        private set(value) {
            field = value
        }

    /**
     * Determine if all pieces are downloaded.
     */
    @Synchronized
    fun allPiecesAreDownloaded() = !pieceDownloadStates.contains(false)

    /**
     * Check if piece at [index] is downloaded.
     */
    @Synchronized
    fun isPieceDownloaded(index: Int) = pieceDownloadStates[index]

    @Synchronized
    override fun toString(): String = "Total Pieces: $pieceCount" +
            ", Start: $startIndex" +
            ", End: $endIndex" +
            ", Head: $bufferHeadIndex" +
            ", Tail: $bufferTailIndex" +
            ", Last Piece Downloaded Index: $lastDownloadedPieceIndex" +
            ", All Pieces Downloaded: ${allPiecesAreDownloaded()}"

    private val pieceDownloadStates = BooleanArray(pieceCount)

    @Synchronized
    internal fun setPieceDownloaded(index: Int): Boolean {
        // Ignore if less than head or already downloaded.
        if (index < bufferHeadIndex || isPieceDownloaded(index)) {
            return true
        }

        pieceDownloadStates[index] = true
        lastDownloadedPieceIndex = index
        downloadedPieceCount++

        // Buffer head was downloaded, advance the buffer a position.
        if (index == bufferHeadIndex) {
            bufferHeadIndex = index

            while (bufferHeadIndex < pieceDownloadStates.size
                    && pieceDownloadStates[bufferHeadIndex]) {
                bufferHeadIndex++
                bufferTailIndex++
            }
        }

        // Don't let the tail of the buffer go past the last piece.
        bufferTailIndex = Math.min(bufferTailIndex, endIndex)

        if (allPiecesAreDownloaded()) {
            bufferHeadIndex = bufferTailIndex
        }

        return false
    }
}


