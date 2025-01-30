package org.theko.logger;

import java.util.List;

/**
 * Interface defining the behavior for a logger that can log messages at different levels,
 * retrieve the last log entry, and return all logged entries.
 */
public interface Logger {
    /**
     * Logs a message with a specified log level and tags.
     * 
     * @param level   The {@link LogLevel} for this message (e.g., {@code DEBUG}, {@code INFO}, {@code ERROR}).
     * @param message The message to log.
     * @param tags    The tags associated with the log entry.
     * 
     * @return The created {@link LogEntry} from the log method.
     */
    LogEntry log(LogLevel level, String message, String... tags);

    /**
     * Logs a message with a specified log level.
     * 
     * @param level   The {@link LogLevel} for this message (e.g., {@code DEBUG}, {@code INFO}, {@code ERROR}).
     * @param message The message to log.
     * 
     * @return The created {@link LogEntry} from the log method.
     */
    LogEntry log(LogLevel level, String message);

    /**
     * Retrieves the last log entry.
     * 
     * @return The last {@link LogEntry}.
     */
    LogEntry getLastLog();

    /**
     * Retrieves all log entries.
     * 
     * @return A list of all {@link LogEntry} objects.
     */
    List<LogEntry> getAllLogs();
}
