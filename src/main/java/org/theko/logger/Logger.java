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
     * @param level   The {@link LogLevel} for this message (e.g., DEBUG, INFO, ERROR).
     * @param message The message to log.
     * 
     * @return Created {@link LogEntry} from log method.
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
     * @return A list of all log entries.
     */
    List<LogEntry> getAllLogs();
}
