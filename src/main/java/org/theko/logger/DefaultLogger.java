package org.theko.logger;

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

    /**
     * Constructs a DefaultLogger with specified LoggerOutput.
     * 
     * @param loggerOutput  The LoggerOutput to handle log display/output.
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
     * @param level   The log level (e.g., DEBUG, ERROR, etc.).
     * @param message The message to log.
     * @param stackTraceOffset The stack trace offset to identify the caller info.
     */
    @Override
    public LogEntry log(LogLevel level, String message, int stackTraceOffset) {
        LogEntry log = super.log(level, message, stackTraceOffset+2);
        if (loggerOutput != null) {
            loggerOutput.processToOut(log);
        }

        if (onLogCreated != null) {
            onLogCreated.accept(log);
        }
        return log;
    }

    protected LogEntry createLogEntry(LogLevel level, String message, int stackTraceOffset) {
        return super.log(level, message, stackTraceOffset+2);
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
     */
    public LogEntry info(String message) {
        return log(LogLevel.INFO, message, 3);
    }

    /**
     * Logs a warning message.
     * 
     * @param message The message to log.
     */
    public LogEntry warn(String message) {
        return super.log(LogLevel.WARN, message, 2);
    }

    /**
     * Logs an error message.
     * 
     * @param message The message to log.
     */
    public LogEntry error(String message) {
        return super.log(LogLevel.ERROR, message, 2);
    }

    /**
     * Logs a debug message.
     * 
     * @param message The message to log.
     */
    public LogEntry debug(String message) {
        return super.log(LogLevel.DEBUG, message, 2);
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
