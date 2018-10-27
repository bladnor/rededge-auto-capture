package ch.exasoft.rededge.autocapture.logging

object LogWriter : StorageWriter {

    private var writer: StorageWriter = NoOpWriter()

    fun setLogWriter(logWriter: StorageWriter) {
        writer = logWriter
    }

    override fun writeData(data: String) {
        writer.writeData(data)
    }
}