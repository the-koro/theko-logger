package org.theko.logger;

import java.io.OutputStream;
import java.util.List;

import org.json.JSONObject;

/**
 * GlobalLogger is a utility class that manages a static logger instance.
 * It provides convenient static methods for logging messages at different levels and managing logger settings globally.
 */
public class GlobalLogger {
    protected static Logger logger;  // Static logger instance used for logging
    protected static LoggerOutput loggerOutput;  // Static LoggerOutput instance to handle log formatting and output

    // Static initializer block that sets up the default logger and output
    static {
        loggerOutput = new LoggerOutput(LoggerOutput.DEFAULT_PATTERN);
        loggerOutput.addOutputStream(System.out);  // Add standard output stream for logging
        loggerOutput.setPreferredLevel(LogLevel.DEBUG);
        logger = new DefaultLogger(loggerOutput, 2);  // Default logger with stack trace offset of 2
    }

    /**
     * Sets a new logger instance globally.
     * 
     * @param newLogger The new logger to be used globally.
     */
    public static void setLogger(Logger newLogger) {
        logger = newLogger;
    }

    /**
     * Sets the LoggerOutput instance used by the logger.
     * 
     * @param output The new LoggerOutput instance.
     */
    public static void setLoggerOutput(LoggerOutput output) {
        loggerOutput = output;
        // If the current logger is an instance of DefaultLogger, update its LoggerOutput
        if (logger instanceof DefaultLogger) {
            ((DefaultLogger) logger).setLoggerOutput(loggerOutput);
        } else if (logger instanceof AsyncLogger) {
            ((AsyncLogger) logger).setLoggerOutput(loggerOutput);
        }
    }

    // Logging methods for different log levels
    /**
     * Logs a message with the specified log level.
     * 
     * @param level   The log level (e.g., INFO, WARN, etc.).
     * @param message The message to log.
     */
    public static void log(LogLevel level, String message) {
        logger.log(level, message);
    }

    /**
     * Logs an informational message.
     * 
     * @param message The message to log.
     */
    public static void info(String message) {
        logger.log(LogLevel.INFO, message);
    }

    /**
     * Logs a warning message.
     * 
     * @param message The message to log.
     */
    public static void warn(String message) {
        logger.log(LogLevel.WARN, message);
    }

    /**
     * Logs an error message.
     * 
     * @param message The message to log.
     */
    public static void error(String message) {
        logger.log(LogLevel.ERROR, message);
    }

    /**
     * Logs a debug message.
     * 
     * @param message The message to log.
     */
    public static void debug(String message) {
        logger.log(LogLevel.DEBUG, message);
    }

    // Accessing log entries
    /**
     * Retrieves the last log entry.
     * 
     * @return The last log entry, or null if no logs exist.
     */
    public static LogEntry getLastLog() {
        return logger.getLastLog();
    }

    /**
     * Retrieves all log entries.
     * 
     * @return A list of all log entries.
     */
    public static List<LogEntry> getAllLogs() {
        return logger.getAllLogs();
    }

    // LoggerOutput management methods
    /**
     * Sets the preferred log level for the LoggerOutput.
     * 
     * @param level The preferred log level to set.
     */
    public void setPreferredLevel(LogLevel level) {
        loggerOutput.setPreferredLevel(level);
    }

    /**
     * Gets the current preferred log level for the LoggerOutput.
     * 
     * @return The current preferred log level.
     */
    public LogLevel getPreferredLevel() {
        return loggerOutput.getPreferredLevel();
    }

    /**
     * Adds an additional OutputStream to the LoggerOutput.
     * 
     * @param os The OutputStream to add.
     */
    public static void addOutputStream(OutputStream os) {
        loggerOutput.addOutputStream(os);
    }

    /**
     * Removes all OutputStreams from the LoggerOutput.
     */
    public static void removeAllOutputStreams() {
        loggerOutput.removeAllOutputStreams();
    }

    /**
     * Retrieves the list of all OutputStreams currently added to the LoggerOutput.
     * 
     * @return A list of OutputStreams.
     */
    public static List<OutputStream> getOutputStreams() {
        return loggerOutput.getOutputStreams();
    }

    /**
     * Sets the pattern used for formatting log entries in the LoggerOutput.
     * 
     * @param pattern The pattern to use for formatting.
     */
    public static void setPattern(String pattern) {
        loggerOutput.pattern = pattern;
    }

    /**
     * Retrieves the current pattern used for formatting log entries.
     * 
     * @return The current log pattern.
     */
    public static String getPattern() {
        return loggerOutput.pattern;
    }

    // Max log count management
    /**
     * Sets the maximum number of logs that the logger can store.
     * 
     * @param maxLogsCount The maximum number of logs to store. If the count exceeds this limit, older logs will be discarded.
     */
    public static void setMaxLogsCount(int maxLogsCount) {
        if (logger instanceof ExtendedLogger) {
            ((ExtendedLogger) logger).setMaxLogsCount(maxLogsCount);
        }
    }

    /**
     * Disables the max logs count limit, allowing logs to accumulate indefinitely.
     */
    public static void disableMaxLogsCount() {
        if (logger instanceof ExtendedLogger) {
            ((ExtendedLogger) logger).disableMaxLogsCount();
        }
    }

    /**
     * Converts all the log entries into a JSON array.
     * Each log entry is serialized to a JSONObject using the {@link LogUtility}.
     * 
     * @return A JSON array containing all the log entries as JSON objects.
     */
    public static JSONObject getAllLogsAsJSON() {
        if (logger instanceof ExtendedLogger) {
            return ((ExtendedLogger) logger).getAllLogsAsJSON();
        }
        return null;
    }

    public void shutdown() {
        if (logger instanceof AsyncLogger) {
            ((AsyncLogger) logger).shutdown();
        }
    }
}
