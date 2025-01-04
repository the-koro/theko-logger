package org.theko.logger;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * The ExtendedLogger class extends the Logger interface, adding functionality for managing and storing log entries.
 * It provides methods for setting a maximum log count, disabling this limit, and exporting logs as a JSON array.
 */
public abstract class ExtendedLogger implements Logger {
    // List to store all log entries
    protected List<LogEntry> logs;
    
    // Maximum number of logs to store; -1 means no limit
    protected int maxLogsCount = -1;

    protected static final String className = ExtendedLogger.class.getName();

    /**
     * Logs a message at the specified log level, including details of an exception if provided.
     *
     * @param level The log level at which the message should be logged.
     * @param message The message to be logged.
     * @param e The exception whose stack trace will be logged, if applicable.
     */
    public void log(LogLevel level, String message, Throwable e) {
        this.log(level, message, e, 2);
    }

    /**
     * Logs a message at the specified log level, including details of an exception if provided.
     *
     * @param level The log level at which the message should be logged.
     * @param message The message to be logged.
     * @param e The exception whose stack trace will be logged, if applicable.
     * @param stackTraceOffset The stack trace offset to identify the caller info.
     */
    public LogEntry log(LogLevel level, String message, Throwable e, int stackTraceOffset) {
        LogEntry log = this.log(level, message, stackTraceOffset+2);
        if (e != null) {
            this.log(level, "Exception: " + e.toString(), stackTraceOffset+2);
            for (StackTraceElement element : e.getStackTrace()) {
                this.log(level, "\tat " + element.toString(), stackTraceOffset+2);
            }
        }
        return log;
    }

    /**
     * Logs a message with the specified log level. The log entry will include the caller's information.
     * 
     * @param level   The log level (e.g., DEBUG, ERROR, etc.).
     * @param message The message to log.
     * @param stackTraceOffset The stack trace offset to identify the caller info.
     */
    public LogEntry log(LogLevel level, String message, int stackTraceOffset) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();  // Get the current thread's stack trace
        StackTraceElement callerElement = null;

        // Iterate through the stack trace to find the method that called 'log'
        for (int i = 0; i < stackTrace.length; i++) {
            StackTraceElement element = stackTrace[i];
            
            if (element.getMethodName().equals("log") && element.getClassName().equals(className)) {
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

        return log;
    }

    /**
     * Logs a message with the specified log level. The log entry will include the caller's information.
     * 
     * @param level   The log level (e.g., DEBUG, ERROR, etc.).
     * @param message The message to log.
     */
    @Override
    public LogEntry log(LogLevel level, String message) {
        return this.log(level, message, 2);
    }

    /**
     * Sets the maximum number of logs to store.
     * If the log count exceeds this limit, older logs will be discarded.
     * 
     * @param maxLogsCount The maximum number of logs to keep. Should be greater than 5 or -1 to disable.
     * @throws IllegalArgumentException if the maxLogsCount is less than 5 and not equal to -1.
     */
    public void setMaxLogsCount(int maxLogsCount) {
        if (maxLogsCount < 5 && maxLogsCount != -1) {
            throw new IllegalArgumentException("Max logs count cannot be less than 5.");
        }
        this.maxLogsCount = maxLogsCount;
    }

    /**
     * Disables the maximum log count, allowing logs to accumulate indefinitely.
     */
    public void disableMaxLogsCount() {
        this.maxLogsCount = -1;
    }

    /**
     * Converts all the log entries into a JSON array.
     * Each log entry is serialized to a JSONObject using the {@link LogUtility}.
     * 
     * @return A JSON object containing all the log entries as JSON objects, under the "logs" key.
     */
    public JSONObject getAllLogsAsJSON() {
        JSONObject allLogsJson = new JSONObject();
        JSONArray logsJsonArray = LogUtility.exportLogsToJSON(this.logs);
        allLogsJson.put("logs", logsJsonArray);  // Add the logs array under the "logs" key
        return allLogsJson;
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
