package ch.exasoft.rededge.autocapture.logging

import android.util.Log
import ch.exasoft.rededge.autocapture.logTag
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.time.LocalDateTime


interface StorageWriter {
    fun writeData(data: String)
}


class FileStorageWriter(publicStorageDir: File) : StorageWriter {

    // TODO close writer when finished/paused

    private val writer: BufferedWriter

    init {
        val fileWriter = FileWriter(publicStorageDir.absolutePath + "/rededge.txt", true)
        writer = BufferedWriter(fileWriter)

    }

    override fun writeData(data: String) {
        try {
            val line = "timestamp: ${LocalDateTime.now()}; " + data
            writer.write(line)
            Log.i(logTag, line)
            writer.newLine()
        } catch (e: Exception) {
            Log.e(logTag, "write to file failed", e)
        } finally {
            writer.flush()
        }
    }
}

class NoOpWriter : StorageWriter {
    override fun writeData(data: String) {
    }
}