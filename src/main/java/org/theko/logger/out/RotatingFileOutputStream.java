package org.theko.logger.out;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicLong;

public class RotatingFileOutputStream extends OutputStream {
    private final String baseFilePath; // Base file path without the number
    private final long maxSize;
    private OutputStream currentStream;
    private BufferedOutputStream bufferedStream;
    private File currentFile;
    private final AtomicLong currentFileSize; // Atomic for thread-safe file size tracking
    private int currentFileIndex = 1; // Tracks the current file number

    public RotatingFileOutputStream(String baseFilePath, long maxSize) throws IOException {
        this.baseFilePath = baseFilePath;
        this.maxSize = maxSize;
        this.currentFileSize = new AtomicLong(0);
        this.currentFile = new File(baseFilePath + currentFileIndex + ".log");
        openStream(); // Initialize the stream
    }

    /**
     * Opens the output stream to the current file.
     */
    private synchronized void openStream() throws IOException {
        closeStreams(); // Ensure previous streams are closed
        currentStream = new FileOutputStream(currentFile, true); // Open file in append mode
        bufferedStream = new BufferedOutputStream(currentStream);
        currentFileSize.set(currentFile.length()); // Update the current file size
    }

    /**
     * Checks if rotation is needed based on the current file size.
     */
    private synchronized void rotateIfNeeded() throws IOException {
        if (currentFileSize.get() >= maxSize) {
            closeStreams(); // Close current streams before rotation
            rotateToNewFile(); // Create a new file
            openStream(); // Reopen stream for the new file
        }
    }

    /**
     * Creates a new log file and increments the file index.
     */
    private void rotateToNewFile() {
        currentFileIndex++; // Increment the file index
        currentFile = new File(baseFilePath + currentFileIndex + ".log"); // Create the new file
    }

    /**
     * Closes the current streams.
     */
    private synchronized void closeStreams() throws IOException {
        if (bufferedStream != null) {
            bufferedStream.flush();
            bufferedStream.close();
            bufferedStream = null;
        }
        if (currentStream != null) {
            currentStream.close();
            currentStream = null;
        }
    }

    @Override
    public void write(int b) throws IOException {
        rotateIfNeeded();
        bufferedStream.write(b);
        currentFileSize.incrementAndGet();
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        rotateIfNeeded();
        bufferedStream.write(b, off, len);
        currentFileSize.addAndGet(len);
    }

    @Override
    public void flush() throws IOException {
        if (bufferedStream != null) {
            bufferedStream.flush();
        }
    }

    @Override
    public void close() throws IOException {
        closeStreams();
    }
}