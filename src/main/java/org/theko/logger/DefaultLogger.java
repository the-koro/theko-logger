package org.theko.logger;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * DefaultLogger is the implementation of the Logger interface.
 * It manages logging entries, storing them in memory and optionally outputting them via LoggerOutput.
 * Logs can also be processed by a consumer function once created.
 */
public class DefaultLogger implements Logger {
    protected List<LogEntry> logs;  // List to store all log entries
    protected LoggerOutput loggerOutput;  // Logger output handler to display logs
    protected Consumer<LogEntry> onLogCreated;  // A consumer that can handle the log entry after it's created
    private final int stackTraceOffset;  // Offset to find the actual caller method in the stack trace

    protected int maxLogsCount = -1;  // Maximum number of logs to store, -1 means no limit

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
            loggerOutput.outputLogEntry(log);
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
     * Sets the maximum number of logs to store.
     * If the log count exceeds this limit, older logs will be discarded.
     * 
     * @param maxLogsCount The maximum number of logs to keep. Should be greater than 5 or -1 to disable.
     */
    public void setMaxLogsCount(int maxLogsCount) {
        if (maxLogsCount < 5 && maxLogsCount != -1) {
            throw new IllegalArgumentException("maxLogsCount needs to be greater than 5, or equals to -1.");
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
     * Each log entry is serialized to a JSONObject using the LogEntry's getJSONObject() method.
     * 
     * @return A JSON array containing all the log entries as JSON objects.
     */
    public JSONObject getAllLogsAsJSON() {
        JSONArray logsArray = new JSONArray();  // Create a JSON array to hold all log entries
        
        // Iterate through all logs and convert each to a JSONObject
        for (LogEntry logEntry : logs) {
            logsArray.put(logEntry.getJSONObject());  // Add each log entry's JSONObject to the array
        }
        
        // Create a final JSON object that contains the logs array
        JSONObject allLogsJson = new JSONObject();
        allLogsJson.put("logs", logsArray);  // Add the logs array under the "logs" key
        
        return allLogsJson;  // Return the final JSON object
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
