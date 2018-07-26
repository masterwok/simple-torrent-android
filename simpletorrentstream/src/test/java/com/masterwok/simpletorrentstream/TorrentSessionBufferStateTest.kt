package com.masterwok.simpletorrentstream

import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertFalse
import org.junit.Test

class TorrentSessionBufferStateTest {

    @Test
    fun new_instance_has_correct_values() {
        val startIndex = 0
        val endIndex = 99
        val bufferSize = 8

        val underTest = TorrentSessionBufferState(
                startIndex
                , endIndex
                , bufferSize
        )

        assertEquals(underTest.bufferHeadIndex, startIndex)
        assertEquals(underTest.bufferTailIndex, startIndex + bufferSize)
        assertEquals(underTest.pieceCount, (endIndex - startIndex) + 1)
        assertEquals(underTest.endIndex, endIndex)
        assertEquals(underTest.startIndex, startIndex)
        assertEquals(underTest.lastDownloadedPieceIndex, -1)
        assertEquals(underTest.toString(), "Total Pieces: 100, Start: 0, End: 99, Head: 0, Tail: 8, Last Piece Downloaded Index: -1")

        (startIndex..endIndex).forEach {
            assertFalse(underTest.isPieceDownloaded(it))
        }
    }
}
