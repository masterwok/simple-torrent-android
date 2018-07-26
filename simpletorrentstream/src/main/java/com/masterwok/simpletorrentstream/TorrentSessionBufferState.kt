package com.masterwok.simpletorrentstream

@Suppress("CanBeParameter", "MemberVisibilityCanBePrivate")
class TorrentSessionBufferState constructor(
        val startIndex: Int
        , val endIndex: Int
        , val bufferSize: Int
) {
    val pieceCount = endIndex - startIndex

    private val pieceDownloadStates = ByteArray(pieceCount)
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


    @Synchronized
    fun setPieceDownloaded(index: Int) {
        // Index should never be less than the head of the buffer.
        if (index < bufferHeadIndex) {
            return
        }

        pieceDownloadStates[index] = 1
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

}


