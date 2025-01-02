package org.theko.logger;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

/**
 * AsyncLogger is the implementation of the Logger interface.
 * It manages logging entries asynchronously, storing them in memory and optionally outputting them via LoggerOutput.
 */
public class AsyncLogger extends ExtendedLogger {
    protected final LinkedBlockingQueue<LogEntry> logQueue; // Queue for logs to be processed asynchronously
    protected LoggerOutput loggerOutput; // Logger output handler to display logs
    protected Consumer<LogEntry> onLogCreated; // A consumer that handles log entries after creation
    private final ExecutorService executorService; // Thread pool for asynchronous logging
    private final int stackTraceOffset; // Offset to find the actual caller method in the stack trace

    protected static final int STACK_TRACE_OFFSET_DEFAULT = 1; // Default offset for stack trace

    /**
     * Constructs an AsyncLogger with the specified LoggerOutput and stack trace offset.
     *
     * @param loggerOutput     The LoggerOutput to handle log display/output.
     * @param stackTraceOffset The offset used to find the actual caller method in the stack trace.
     */
    public AsyncLogger(LoggerOutput loggerOutput, int stackTraceOffset) {
        this.loggerOutput = loggerOutput;
        this.stackTraceOffset = stackTraceOffset;
        this.logs = new CopyOnWriteArrayList<>(); // Thread-safe list for log entries
        this.logQueue = new LinkedBlockingQueue<>(4096); // Thread-safe queue for log entries
        this.executorService = Executors.newSingleThreadExecutor(); // Single-thread executor for async processing
        startLogProcessing();
    }

    /**
     * Constructs an AsyncLogger with the specified LoggerOutput and default stack trace offset.
     *
     * @param loggerOutput The LoggerOutput to handle log display/output.
     */
    public AsyncLogger(LoggerOutput loggerOutput) {
        this(loggerOutput, STACK_TRACE_OFFSET_DEFAULT);
    }

    /**
     * Default constructor, initializes with null LoggerOutput and default stack trace offset.
     */
    public AsyncLogger() {
        this(null, STACK_TRACE_OFFSET_DEFAULT);
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
     * Logs a message with the specified log level. The log entry will include the caller's information.
     *
     * @param level   The log level (e.g., DEBUG, ERROR, etc.).
     * @param message The message to log.
     */
    @Override
    public void log(LogLevel level, String message) {
        StackTraceElement[] stackTrace = getStackTrace();
        StackTraceElement callerElement = null;

        for (int i = 0; i < stackTrace.length; i++) {
            StackTraceElement element = stackTrace[i];

            if (element.getMethodName().equals("log") && element.getClassName().equals(this.getClass().getName())) {
                if (i + stackTraceOffset < stackTrace.length) {
                    callerElement = stackTrace[i + stackTraceOffset];
                }
                break;
            }
        }

        LogEntry log = new LogEntry(
            level,
            System.currentTimeMillis(),
            new CallerInfo(callerElement, Thread.currentThread().getName()),
            message
        );

        logQueue.add(log); // Enqueue the log for asynchronous processing
    }

    /**
     * Logs an informational message.
     * 
     * @param message The message to log.
     */
    public void info(String message) {
        log(LogLevel.INFO, message);
    }

    /**
     * Logs a warning message.
     * 
     * @param message The message to log.
     */
    public void warn(String message) {
        log(LogLevel.WARN, message);
    }

    /**
     * Logs an error message.
     * 
     * @param message The message to log.
     */
    public void error(String message) {
        log(LogLevel.ERROR, message);
    }

    /**
     * Logs a debug message.
     * 
     * @param message The message to log.
     */
    public void debug(String message) {
        log(LogLevel.DEBUG, message);
    }

    /**
     * Sets the consumer function to be executed when a log entry is created.
     *
     * @param onLogCreated The consumer function to handle new log entries.
     */
    public void setOnLogCreated(Consumer<LogEntry> onLogCreated) {
        this.onLogCreated = onLogCreated;
    }

    /**
     * Sets the LoggerOutput that handles the output of log entries.
     *
     * @param loggerOutput The LoggerOutput to set.
     */
    public void setLoggerOutput(LoggerOutput loggerOutput) {
        this.loggerOutput = loggerOutput;
    }

    /**
     * Retrieves the stack trace of the current thread.
     *
     * @return The stack trace elements for the current thread.
     */
    protected StackTraceElement[] getStackTrace() {
        return Thread.currentThread().getStackTrace();
    }

    /**
     * Retrieves the last log entry, or null if no logs exist.
     *
     * @return The last log entry, or null if no logs are present.
     */
    @Override
    public LogEntry getLastLog() {
        if (logs.isEmpty()) {
            return null;
        }
        return logs.get(logs.size() - 1);
    }

    /**
     * Retrieves all log entries.
     *
     * @return A list containing all log entries.
     */
    @Override
    public List<LogEntry> getAllLogs() {
        return logs;
    }

    /**
     * Shuts down the executor service, stopping asynchronous logging.
     */
    public void shutdown() {
        executorService.shutdownNow();
    }
}
