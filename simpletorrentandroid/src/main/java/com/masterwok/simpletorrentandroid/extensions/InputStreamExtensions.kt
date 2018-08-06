package com.masterwok.simpletorrentandroid.extensions

import java.io.ByteArrayOutputStream
import java.io.InputStream


/**
 * Read the [InputStream] input a byte array. If reading fails or the [InputStream] is empty,
 * then an empty [ByteArray] is returned.
 */
internal fun InputStream.readBytes(
        bufferSize: Int = 4096
): ByteArray {
    val outputStream = ByteArrayOutputStream()
    val buffer = ByteArray(bufferSize)

    while (true) {
        val byteCount = read(buffer)

        if (byteCount <= 0) {
            break
        }

        outputStream.write(buffer, 0, byteCount)
    }

    return outputStream.toByteArray()
}

