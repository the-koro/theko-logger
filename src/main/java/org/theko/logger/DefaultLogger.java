package org.theko.logger;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * DefaultLogger is the implementation of the Logger interface.
 * It manages logging entries, storing them in memory and optionally outputting them via LoggerOutput.
 * Logs can also be processed by a consumer function once created.
 */
public class DefaultLogger extends ExtendedLogger {
    // Logger output handler to display logs
    protected LoggerOutput loggerOutput;

    private static final String[] EMPTY_TAGS = new String[]{""};
    
    // A consumer that can handle the log entry after it's created
    protected Consumer<LogEntry> onLogCreated;

    /**
     * Constructs a DefaultLogger with specified LoggerOutput.
     *
     * @param loggerOutput The LoggerOutput to handle log display/output.
     */
    public DefaultLogger(LoggerOutput loggerOutput) {
        this.loggerOutput = loggerOutput;
        this.logs = new CopyOnWriteArrayList<>();  // Initialize the logs list with thread-safe copy-on-write
    }

    /**
     * Default constructor, initializes with null LoggerOutput and default stack trace offset.
     */
    public DefaultLogger() {
        this(null);
    }

    /**
     * Logs a message with the specified log level. The log entry will include the caller's information.
     *
     * @param level The log level (e.g., DEBUG, ERROR, etc.).
     * @param message The message to log.
     * @param tags The tags associated with the log.
     * @param stackTraceOffset The stack trace offset to identify the caller info.
     * @return The created LogEntry.
     */
    @Override
    public LogEntry log(LogLevel level, String message, String[] tags, int stackTraceOffset) {
        LogEntry log = super.log(level, message, tags, stackTraceOffset + 1);
        
        // If loggerOutput is set, process the log entry to output
        if (loggerOutput != null) {
            loggerOutput.processToOut(log);
        }

        // If a consumer is set, apply it to the log entry
        if (onLogCreated != null) {
            onLogCreated.accept(log);
        }
        return log;
    }

    /**
     * Creates a log entry with the specified log level and message.
     * 
     * @param level The log level (e.g., DEBUG, ERROR, etc.).
     * @param message The message to log.
     * @param stackTraceOffset The stack trace offset to identify the caller info.
     * @return The created LogEntry.
     */
    protected LogEntry createLogEntry(LogLevel level, String message, int stackTraceOffset) {
        return super.log(level, message, stackTraceOffset + 2);
    }

    /**
     * Creates a log entry with the specified log level, message, and tags.
     * 
     * @param level The log level (e.g., DEBUG, ERROR, etc.).
     * @param message The message to log.
     * @param tags The tags associated with the log.
     * @param stackTraceOffset The stack trace offset to identify the caller info.
     * @return The created LogEntry.
     */
    protected LogEntry createLogEntry(LogLevel level, String message, String[] tags, int stackTraceOffset) {
        return super.log(level, message, tags, stackTraceOffset + 2);
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
     * Logs an informational message.
     * 
     * @param message The message to log.
     * @param tags The tags associated with the log.
     * @return The created LogEntry.
     */
    public LogEntry info(String message, String... tags) {
        return log(LogLevel.INFO, message, tags, 2);
    }

    /**
     * Logs a warning message.
     * 
     * @param message The message to log.
     * @param tags The tags associated with the log.
     * @return The created LogEntry.
     */
    public LogEntry warn(String message, String... tags) {
        return log(LogLevel.WARN, message, tags, 2);
    }

    /**
     * Logs an error message.
     * 
     * @param message The message to log.
     * @param tags The tags associated with the log.
     * @return The created LogEntry.
     */
    public LogEntry error(String message, String... tags) {
        return log(LogLevel.ERROR, message, tags, 2);
    }

    /**
     * Logs a debug message.
     * 
     * @param message The message to log.
     * @param tags The tags associated with the log.
     * @return The created LogEntry.
     */
    public LogEntry debug(String message, String... tags) {
        return log(LogLevel.DEBUG, message, tags, 2);
    }

    /**
     * Logs an informational message.
     * 
     * @param message The message to log.
     * @return The created LogEntry.
     */
    public LogEntry info(String message) {
        return log(LogLevel.INFO, message, EMPTY_TAGS, 2);
    }

    /**
     * Logs a warning message.
     * 
     * @param message The message to log.
     * @return The created LogEntry.
     */
    public LogEntry warn(String message) {
        return log(LogLevel.WARN, message, EMPTY_TAGS, 2);
    }

    /**
     * Logs an error message.
     * 
     * @param message The message to log.
     * @return The created LogEntry.
     */
    public LogEntry error(String message) {
        return log(LogLevel.ERROR, message, EMPTY_TAGS, 2);
    }

    /**
     * Logs a debug message.
     * 
     * @param message The message to log.
     * @return The created LogEntry.
     */
    public LogEntry debug(String message) {
        return log(LogLevel.DEBUG, message, EMPTY_TAGS, 2);
    }

    /**
     * Sets the consumer function to be executed when a log entry is created.
     * 
     * @param onLogCreated The consumer function to handle new log entries.
     */
    public void setOnLogCreated(Consumer<LogEntry> onLogCreated) {
        this.onLogCreated = onLogCreated;
    }
}
