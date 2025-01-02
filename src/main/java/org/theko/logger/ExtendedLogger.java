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

    /**
     * Logs a message at the specified log level, including details of an exception if provided.
     *
     * @param level The log level at which the message should be logged.
     * @param message The message to be logged.
     * @param e The exception whose stack trace will be logged, if applicable.
     */
    public void log(LogLevel level, String message, Throwable e) {
        this.log(level, message);
        if (e != null) {
            this.log(level, "Exception: " + e.toString());
            for (StackTraceElement element : e.getStackTrace()) {
                this.log(level, "\tat " + element.toString());
            }
        }
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
}
