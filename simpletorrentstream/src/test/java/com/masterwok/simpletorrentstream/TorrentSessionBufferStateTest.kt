package com.masterwok.simpletorrentstream

import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertFalse
import org.junit.Test
import java.util.*

class TorrentSessionBufferStateTest {

    private fun getExceptedPieceCount(startIndex: Int, endIndex: Int): Int = (endIndex - startIndex) + 1

    private fun getExpectedToString(underTest: TorrentSessionBufferState): String = "Total Pieces: ${underTest.pieceCount}" +
            ", Start: ${underTest.startIndex}" +
            ", End: ${underTest.endIndex}" +
            ", Head: ${underTest.bufferHeadIndex}" +
            ", Tail: ${underTest.bufferTailIndex}" +
            ", Last Piece Downloaded Index: ${underTest.lastDownloadedPieceIndex}"

    private fun getExpectedTailIndex(
            bufferHeadIndex: Int
            , endIndex: Int
            , bufferSize: Int
    ): Int {
        val expectedTail = bufferHeadIndex + bufferSize

        return if (expectedTail > endIndex) return endIndex else expectedTail
    }

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
        assertEquals(underTest.bufferTailIndex, getExpectedTailIndex(startIndex, endIndex, bufferSize))
        assertEquals(underTest.pieceCount, getExceptedPieceCount(startIndex, endIndex))
        assertEquals(underTest.endIndex, endIndex)
        assertEquals(underTest.startIndex, startIndex)
        assertEquals(underTest.lastDownloadedPieceIndex, -1)
        assertEquals(underTest.toString(), getExpectedToString(underTest))

        (startIndex..endIndex).forEach {
            assertFalse(underTest.isPieceDownloaded(it))
        }
    }

    @Test
    fun is_finished_is_expected_value_when_finished() {
        val startIndex = 0
        val endIndex = 99
        val bufferSize = 8

        val underTest = TorrentSessionBufferState(
                startIndex
                , endIndex
                , bufferSize
        )

        (startIndex..endIndex).forEach {
            assert(!underTest.isFinished())
            underTest.setPieceDownloaded(it)
        }

        assert(underTest.isFinished())
    }

    @Test
    fun head_index_advances_sequentially() {
        val startIndex = 0
        val endIndex = 99
        val bufferSize = 8

        val underTest = TorrentSessionBufferState(
                startIndex
                , endIndex
                , bufferSize
        )

        (startIndex..endIndex).forEach {
            underTest.setPieceDownloaded(it)

            assertEquals(underTest.bufferHeadIndex, it + 1)
        }
    }

    @Test
    fun piece_count_is_correct() {
        val startIndex = 0
        val endIndex = 99
        val bufferSize = 8

        val underTest = TorrentSessionBufferState(
                startIndex
                , endIndex
                , bufferSize
        )

        (startIndex..endIndex).forEach {
            underTest.setPieceDownloaded(it)

            assertEquals(underTest.downloadedPieceCount, it + 1)
        }
    }

    @Test
    fun tail_index_advances_sequentially() {
        val startIndex = 0
        val endIndex = 99
        val bufferSize = 8

        val underTest = TorrentSessionBufferState(
                startIndex
                , endIndex
                , bufferSize
        )

        (startIndex..endIndex).forEach {
            underTest.setPieceDownloaded(it)

            val expectedTail = getExpectedTailIndex(
                    underTest.bufferHeadIndex
                    , underTest.endIndex
                    , bufferSize
            )

            assertEquals(underTest.bufferTailIndex, expectedTail)
        }
    }

    @Test
    fun piece_count_increments_correctly_on_random() {
        val startIndex = 0
        val endIndex = 100
        val bufferSize = 8
        val expectedPieceCount = getExceptedPieceCount(startIndex, endIndex)

        val underTest = TorrentSessionBufferState(
                startIndex
                , endIndex
                , bufferSize
        )

        var count = 0
        while (!underTest.isFinished()) {
            val randomPieceDownload = (startIndex..endIndex).random()

            if (!underTest.setPieceDownloaded(randomPieceDownload)) {
                count++
            }

            assertEquals(underTest.downloadedPieceCount, count)
            assert(underTest.downloadedPieceCount <= expectedPieceCount)
        }
    }
}

private fun IntRange.random(): Int {
    return Random().nextInt((endInclusive + 1) - start) + start
}
