package org.theko.logger;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * DefaultLogger is the implementation of the Logger interface.
 * It manages logging entries, storing them in memory and optionally outputting them via LoggerOutput.
 * Logs can also be processed by a consumer function once created.
 */
public class DefaultLogger extends ExtendedLogger {
    protected LoggerOutput loggerOutput;  // Logger output handler to display logs
    protected Consumer<LogEntry> onLogCreated;  // A consumer that can handle the log entry after it's created
    private final int stackTraceOffset;  // Offset to find the actual caller method in the stack trace

    protected static final int STACK_TRACE_OFFSET_DEFAULT = 1;  // Default offset for stack trace

    /**
     * Constructs a DefaultLogger with specified LoggerOutput and stack trace offset.
     * 
     * @param loggerOutput  The LoggerOutput to handle log display/output.
     * @param stackTraceOffset The offset used to find the actual caller method in the stack trace.
     */
    public DefaultLogger(LoggerOutput loggerOutput, int stackTraceOffset) {
        this.loggerOutput = loggerOutput;
        this.stackTraceOffset = stackTraceOffset;
        this.logs = new CopyOnWriteArrayList<>();  // Initialize the logs list with thread-safe copy-on-write
    }

    /**
     * Constructs a DefaultLogger with specified LoggerOutput and default stack trace offset.
     * 
     * @param loggerOutput  The LoggerOutput to handle log display/output.
     */
    public DefaultLogger(LoggerOutput loggerOutput) {
        this(loggerOutput, STACK_TRACE_OFFSET_DEFAULT);
    }

    /**
     * Default constructor, initializes with null LoggerOutput and default stack trace offset.
     */
    public DefaultLogger() {
        this(null, STACK_TRACE_OFFSET_DEFAULT);
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
     * Logs a message with the specified log level. The log entry will include the caller's information.
     * 
     * @param level   The log level (e.g., DEBUG, ERROR, etc.).
     * @param message The message to log.
     */
    @Override
    public void log(LogLevel level, String message) {
        StackTraceElement[] stackTrace = getStackTrace();  // Get the current thread's stack trace
        StackTraceElement callerElement = null;

        // Iterate through the stack trace to find the method that called 'log'
        for (int i = 0; i < stackTrace.length; i++) {
            StackTraceElement element = stackTrace[i];
            
            if (element.getMethodName().equals("log") && element.getClassName().equals(this.getClass().getName())) {
                if (i + stackTraceOffset < stackTrace.length) {
                    callerElement = stackTrace[i + stackTraceOffset];  // Get the caller method's stack trace element
                }
                break;
            }
        }

        // Create a LogEntry with the gathered information
        LogEntry log = new LogEntry(
                level,
                System.currentTimeMillis(),
                new CallerInfo(callerElement, Thread.currentThread().getName()),
                message
        );
        
        logs.add(log);  // Add the new log entry to the list

        // If maxLogsCount is set, trim the log list to respect the limit
        if (maxLogsCount != -1) {
            if (logs.size() > maxLogsCount) {
                logs.subList(0, logs.size() - maxLogsCount).clear();  // Remove excess logs
            }
        }
        
        // Output the log entry using the loggerOutput, if it's set
        if (loggerOutput != null) {
            loggerOutput.processToOut(log);
        }

        // If a consumer is set, process the log entry
        if (onLogCreated != null) {
            onLogCreated.accept(log);
        }
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
        if (logs == null || logs.isEmpty()) {
            return null;
        }
        return logs.get(logs.size() - 1);  // Return the most recent log entry
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
}
