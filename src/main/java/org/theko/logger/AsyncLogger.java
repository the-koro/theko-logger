package org.theko.logger;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * AsyncLogger extends DefaultLogger to handle logging asynchronously.
 * Log entries are created synchronously and added to a queue for asynchronous processing.
 */
public class AsyncLogger extends DefaultLogger {
    private final BlockingQueue<LogEntry> logQueue;
    private final ExecutorService executor;

    /**
     * Constructs an AsyncLogger with a specified LoggerOutput and thread pool size.
     *
     * @param loggerOutput The LoggerOutput to handle log display/output.
     */
    public AsyncLogger(LoggerOutput loggerOutput) {
        super(loggerOutput);
        this.logQueue = new LinkedBlockingQueue<>();
        this.executor = Executors.newSingleThreadExecutor();
        startLogProcessor();
    }

    /**
     * Constructs an AsyncLogger with thread pool size.
     */
    public AsyncLogger() {
        super();
        this.logQueue = new LinkedBlockingQueue<>();
        this.executor = Executors.newSingleThreadExecutor();
        startLogProcessor();
    }

    /**
     * Starts the log processor to handle log entries asynchronously.
     */
    private void startLogProcessor() {
        executor.submit(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    LogEntry log = logQueue.take(); // Block until a log is available
                    if (loggerOutput != null) {
                        loggerOutput.processToOut(log);
                    }
                    if (onLogCreated != null) {
                        onLogCreated.accept(log);
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Handle graceful shutdown
            }
        });
    }

    @Override
    public LogEntry log(LogLevel level, String message, int stackTraceOffset) {
        LogEntry log = createLogEntry(level, message, stackTraceOffset);
        logQueue.offer(log); // Add log to the processing queue
        return log;
    }

    public void drain() throws InterruptedException {
        while (!logQueue.isEmpty()) {
            LogEntry log = logQueue.take(); // Block until a log is available
            if (loggerOutput != null) {
                loggerOutput.processToOut(log);
            }
            if (onLogCreated != null) {
                onLogCreated.accept(log);
            }
        }
    }

    /**
     * Shuts down the logger's executor service.
     * This method should be called when the logger is no longer needed to release resources.
     */
    public void shutdown() {
        executor.shutdownNow();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                System.err.println("AsyncLogger did not terminate in the specified time.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
