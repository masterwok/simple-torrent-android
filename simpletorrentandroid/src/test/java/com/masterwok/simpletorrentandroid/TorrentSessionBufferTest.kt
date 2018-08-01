package com.masterwok.simpletorrentandroid

import com.masterwok.simpletorrentandroid.models.TorrentSessionBuffer
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertFalse
import org.junit.Test
import java.util.*

class TorrentSessionBufferTest {

    private fun getExceptedPieceCount(startIndex: Int, endIndex: Int): Int = (endIndex - startIndex) + 1

    private fun getExpectedToString(underTest: TorrentSessionBuffer): String = "Total Pieces: ${underTest.pieceCount}" +
            ", Start: ${underTest.startIndex}" +
            ", End: ${underTest.endIndex}" +
            ", Head: ${underTest.bufferHeadIndex}" +
            ", Tail: ${underTest.bufferTailIndex}" +
            ", Last Piece Downloaded Index: ${underTest.lastDownloadedPieceIndex}" +
            ", All Pieces Downloaded: ${underTest.allPiecesAreDownloaded()}"

    private fun getExpectedTailIndex(
            bufferHeadIndex: Int
            , endIndex: Int
            , bufferSize: Int
    ): Int = Math.min(bufferHeadIndex + bufferSize - 1, endIndex)

    private fun IntRange.random(): Int {
        return Random().nextInt((endInclusive + 1) - start) + start
    }

    @Test
    fun new_instance_has_correct_values() {
        val startIndex = 0
        val endIndex = 99
        val bufferSize = 8

        val underTest = TorrentSessionBuffer(
                bufferSize
                , startIndex
                , endIndex
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

        val underTest = TorrentSessionBuffer(
                bufferSize
                , startIndex
                , endIndex
        )


        (startIndex..endIndex).forEach {
            assertFalse(
                    "Buffer Head Index: ${underTest.bufferHeadIndex}, End Index: $endIndex",
                    underTest.allPiecesAreDownloaded()
            )

            underTest.setPieceDownloaded(it)
        }

        assert(underTest.allPiecesAreDownloaded())
    }

    @Test
    fun head_index_advances_sequentially() {
        val startIndex = 0
        val endIndex = 99
        val bufferSize = 8

        val underTest = TorrentSessionBuffer(
                bufferSize
                , startIndex
                , endIndex
        )

        (startIndex..endIndex).forEach {
            underTest.setPieceDownloaded(it)

            assertEquals(underTest.bufferHeadIndex, Math.min(it + 1, endIndex))
        }
    }

    @Test
    fun piece_count_is_correct() {
        val startIndex = 0
        val endIndex = 99
        val bufferSize = 8

        val underTest = TorrentSessionBuffer(
                bufferSize
                , startIndex
                , endIndex
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

        val underTest = TorrentSessionBuffer(
                bufferSize
                , startIndex
                , endIndex
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

        val underTest = TorrentSessionBuffer(
                bufferSize
                , startIndex
                , endIndex
        )

        var count = 0
        while (!underTest.allPiecesAreDownloaded()) {
            val randomPieceDownload = (startIndex..endIndex).random()

            if (!underTest.setPieceDownloaded(randomPieceDownload)) {
                count++
            }

            assertEquals(underTest.downloadedPieceCount, count)
            assert(underTest.downloadedPieceCount <= expectedPieceCount)
        }
    }

    @Test
    fun start_index_should_slide_to_last_downloaded_piece() {
        val startIndex = 0
        val endIndex = 100
        val bufferSize = 8

        val underTest = TorrentSessionBuffer(
                bufferSize
                , startIndex
                , endIndex
        )

        (1..29).forEach { underTest.setPieceDownloaded(it) }

        assertEquals(underTest.bufferHeadIndex, startIndex)

        underTest.setPieceDownloaded(0)

        val expectedTail = getExpectedTailIndex(
                underTest.bufferHeadIndex
                , underTest.endIndex
                , bufferSize
        )

        assertEquals(underTest.bufferHeadIndex, 30)
        assertEquals(underTest.bufferTailIndex, Math.min(expectedTail, endIndex))
    }

    @Test
    fun start_index_should_slide_to_last_piece() {
        val startIndex = 0
        val endIndex = 100
        val bufferSize = 8

        val underTest = TorrentSessionBuffer(
                bufferSize
                , startIndex
                , endIndex
        )

        (1..endIndex).forEach { underTest.setPieceDownloaded(it) }

        assertEquals(underTest.bufferHeadIndex, startIndex)

        underTest.setPieceDownloaded(0)

        assertEquals(underTest.bufferHeadIndex, endIndex)
        assertEquals(underTest.bufferTailIndex, Math.min(underTest.bufferHeadIndex + bufferSize, endIndex))
    }
}
