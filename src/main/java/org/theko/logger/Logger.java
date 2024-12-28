package org.theko.logger;

import java.util.List;

/**
 * Interface defining the behavior for a logger that can log messages at different levels,
 * retrieve the last log entry, and return all logged entries.
 */
public interface Logger {
    /**
     * Logs a message with a specified log level.
     * 
     * @param level   The log level for this message (e.g., DEBUG, INFO, ERROR).
     * @param message The message to log.
     */
    void log(LogLevel level, String message);

    /**
     * Retrieves the last log entry.
     * 
     * @return The last log entry.
     */
    LogEntry getLastLog();

    /**
     * Retrieves all log entries.
     * 
     * @return A list of all log entries.
     */
    List<LogEntry> getAllLogs();
}
