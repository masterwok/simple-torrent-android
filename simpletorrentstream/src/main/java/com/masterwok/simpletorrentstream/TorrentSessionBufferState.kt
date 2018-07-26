package com.masterwok.simpletorrentstream

@Suppress("CanBeParameter", "MemberVisibilityCanBePrivate")
class TorrentSessionBufferState constructor(
        val startIndex: Int
        , val endIndex: Int
        , val bufferSize: Int
) {
    val pieceCount = (endIndex - startIndex) + 1

    private val pieceDownloadStates = BooleanArray(pieceCount)

    var downloadedPieceCount = 0
        @Synchronized
        get() = field
        private set(value) {
            field = value
        }

    var bufferHeadIndex = startIndex
        @Synchronized
        get() = field
        private set(value) {
            field = value
        }

    var bufferTailIndex = startIndex + bufferSize
        @Synchronized
        get() = field
        private set(value) {
            field = value
        }

    var lastDownloadedPieceIndex = -1
        @Synchronized
        get() = field
        private set(value) {
            field = value
        }

    @Synchronized
    fun isFinished() = !pieceDownloadStates.contains(false)

    @Synchronized
    fun isPieceDownloaded(position: Int) = pieceDownloadStates[position]

    @Synchronized
    fun setPieceDownloaded(index: Int): Boolean {
        // Ignore if less than head or already downloaded.
        if (index < bufferHeadIndex || isPieceDownloaded(index)) {
            return true
        }

        pieceDownloadStates[index] = true
        lastDownloadedPieceIndex = index
        downloadedPieceCount++

        // Buffer head was downloaded, advance the buffer a position.
        if (index == bufferHeadIndex) {
            bufferHeadIndex++
            bufferTailIndex++

            // Don't let the tail of the buffer go past the last piece.
            if (bufferTailIndex > endIndex) {
                bufferTailIndex = endIndex
            }

            if (isFinished()) {
                bufferHeadIndex = bufferTailIndex
            }
        }

        return false
    }

    @Synchronized
    override fun toString(): String = "Total Pieces: $pieceCount" +
            ", Start: $startIndex" +
            ", End: $endIndex" +
            ", Head: $bufferHeadIndex" +
            ", Tail: $bufferTailIndex" +
            ", Last Piece Downloaded Index: $lastDownloadedPieceIndex" +
            ", IsFinished: ${isFinished()}"
}


