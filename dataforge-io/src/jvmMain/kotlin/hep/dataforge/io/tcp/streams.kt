package hep.dataforge.io.tcp

import kotlinx.io.*
import kotlinx.io.buffer.Buffer
import kotlinx.io.buffer.set
import java.io.InputStream
import java.io.OutputStream

private class BlockingStreamInput(val source: InputStream) : Input() {
    override fun closeSource() {
        source.close()
    }

    override fun fill(buffer: Buffer, startIndex: Int, endIndex: Int): Int {
        while (source.available() == 0) {
            //block until input is available
        }
        // Zero-copy attempt
        if (buffer.buffer.hasArray()) {
            val result = source.read(buffer.buffer.array(), startIndex, endIndex - startIndex)
            return result.coerceAtLeast(0) // -1 when IS is closed
        }

        for (i in startIndex until endIndex) {
            val byte = source.read()
            if (byte == -1) return (i - startIndex)
            buffer[i] = byte.toByte()
        }
        return endIndex - startIndex
    }
}

fun <R> InputStream.read(size: Int, block: Input.() -> R): R {
    val buffer = ByteArray(size)
    read(buffer)
    return buffer.asBinary().read(block = block)
}

fun <R> InputStream.read(block: Input.() -> R): R = asInput().block()

fun <R> InputStream.readBlocking(block: Input.() -> R): R = BlockingStreamInput(this).block()

inline fun OutputStream.write(block: Output.() -> Unit) {
    asOutput().block()
}