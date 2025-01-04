package org.theko.logger;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * AsyncLogger is the implementation of the Logger interface.
 * It manages logging entries asynchronously, storing them in memory and optionally outputting them via LoggerOutput.
 */
public class AsyncLogger extends DefaultLogger {
    protected final LinkedBlockingQueue<LogEntry> logQueue; // Queue for logs to be processed asynchronously
    private final ExecutorService executorService; // Thread pool for asynchronous logging

    protected static final int STACK_TRACE_OFFSET_DEFAULT = 1; // Default offset for stack trace

    /**
     * Constructs an AsyncLogger with the specified LoggerOutput.
     *
     * @param loggerOutput     The LoggerOutput to handle log display/output.
     */
    public AsyncLogger(LoggerOutput loggerOutput) {
        super(loggerOutput);
        this.logQueue = new LinkedBlockingQueue<>(8000); // Thread-safe queue for log entries
        this.executorService = Executors.newSingleThreadExecutor(); // Single-thread executor for async processing
        startLogProcessing();
    }

    /**
     * Default constructor, initializes with null LoggerOutput and default stack trace offset.
     */
    public AsyncLogger() {
        this(null);
    }

    /**
     * Starts processing logs from the queue asynchronously.
     */
    private void startLogProcessing() {
        executorService.submit(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    LogEntry log = logQueue.take(); // Blocks if the queue is empty
                    processLog(log);
                } catch (InterruptedException e) {
                    System.out.println("A");
                    Thread.currentThread().interrupt(); // Handle thread interruption properly
                    break;
                }
            }
        });
    }

    /**
     * Processes a log entry: adds it to the list, trims the list if needed, outputs via LoggerOutput, and calls the consumer.
     *
     * @param log The log entry to process.
     */
    private void processLog(LogEntry log) {
        logs.add(log); // Add to the log list

        if (logQueue.remainingCapacity() == 0) {
            logQueue.poll(); // Remove the oldest log entry if the queue is full
        }
        //logQueue.offer(log); // Add the new log entry
    
        // Trim the list if maxLogsCount is set
        if (maxLogsCount != -1) {
            while (logs.size() > maxLogsCount) {
                logs.remove(0); // Remove the oldest log entry
            }
        }
    
        // Output the log entry using LoggerOutput if available
        if (loggerOutput != null) {
            loggerOutput.processToOut(log);
        }
    
        // Process the log entry using the consumer if set
        if (onLogCreated != null) {
            onLogCreated.accept(log);
        }
    }

    /**
     * Drains all logs from the queue, processes them, and removes them.
     * This method ensures that all logs in the queue are processed and output immediately,
     * clearing the queue in the process.
     */
    public void drain() throws InterruptedException {
        // Create a CountDownLatch to wait for all logs to be processed
        CountDownLatch latch = new CountDownLatch(logQueue.size());

        // Process and clear the queue asynchronously
        while (!logQueue.isEmpty()) {
            LogEntry log = logQueue.take();  // Blocking call until a log is available
            processLog(log);  // Process the log

            // Decrement the latch after processing each log entry
            latch.countDown();
        }

        // Block the current thread until all logs are processed
        latch.await();
    }

    /**
     * Shuts down the executor service, stopping asynchronous logging.
     */
    public void shutdown() {
        executorService.shutdownNow();
    }
}
