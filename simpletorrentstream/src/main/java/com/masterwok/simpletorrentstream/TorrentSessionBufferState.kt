package com.masterwok.simpletorrentstream

@Suppress("CanBeParameter", "MemberVisibilityCanBePrivate")
class TorrentSessionBufferState constructor(
        val startIndex: Int
        , val endIndex: Int
        , val bufferSize: Int
) {
    val pieceCount = (endIndex - startIndex) + 1

    private val pieceDownloadStates = BooleanArray(pieceCount)
        get() = field.clone()

    var bufferHeadIndex = startIndex
        private set(value) {
            field = value
        }

    var bufferTailIndex = startIndex + bufferSize
        private set(value) {
            field = value
        }

    var lastDownloadedPieceIndex = -1
        private set(value) {
            field = value
        }

    // TODO: Protect this from threading issues..
    fun isPieceDownloaded(position: Int) = pieceDownloadStates[position]

    // TODO: Protect this from threading issues..
    fun setPieceDownloaded(index: Int) {
        // Index should never be less than the head of the buffer.
        if (index < bufferHeadIndex) {
            return
        }

        pieceDownloadStates[index] = true
        lastDownloadedPieceIndex = index

        // Buffer head was downloaded, advance the buffer a position.
        if (index == bufferHeadIndex) {
            bufferHeadIndex++
            bufferTailIndex++

            // Don't let the tail of the buffer go past the last piece.
            if (bufferTailIndex > endIndex) {
                bufferTailIndex = endIndex
            }
        }
    }

    override fun toString(): String = "Total Pieces: $pieceCount" +
            ", Start: $startIndex" +
            ", End: $endIndex" +
            ", Head: $bufferHeadIndex" +
            ", Tail: $bufferTailIndex" +
            ", Last Piece Downloaded Index: $lastDownloadedPieceIndex"

}


