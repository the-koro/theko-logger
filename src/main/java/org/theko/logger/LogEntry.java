package org.theko.logger;

import java.io.Serializable;

import org.json.JSONObject;

/**
 * Represents a single log entry containing information about the log level, timestamp, 
 * caller information, and the actual log message.
 */
public class LogEntry implements Serializable {
    private static final long serialVersionUID = 1L;  // Version control for serialization

    private final LogLevel level;  // Log level (DEBUG, INFO, ERROR, etc.)
    private final long time;  // Timestamp of when the log entry was created
    private final CallerInfo caller;  // Information about the caller (class, method, etc.)
    private final String message;  // The log message

    /**
     * Constructs a LogEntry with all the required details.
     * 
     * @param level   The log level for the entry.
     * @param time    The time when the log entry was created.
     * @param caller  The caller information for the log entry.
     * @param message The log message.
     */
    public LogEntry(LogLevel level, long time, CallerInfo caller, String message) {
        if (level == LogLevel.NONE) {
            throw new IllegalArgumentException("Log level cannot be NONE.");
        }

        this.level = level;
        this.time = time;
        this.caller = caller;
        this.message = message;
    }

    /**
     * Constructs a LogEntry with the specified level, time, and message.
     * The caller information is set to null.
     * 
     * @param level   The log level for the entry.
     * @param time    The time when the log entry was created.
     * @param message The log message.
     */
    public LogEntry(LogLevel level, long time, String message) {
        this(level, time, null, message);
    }

    // Getter methods for retrieving log entry details

    public LogLevel getLevel() {
        return level;
    }

    public long getTime() {
        return time;
    }

    public CallerInfo getCallerInfo() {
        return caller;
    }

    public String getClassName() {
        return caller.getClassName();
    }

    public String getMethodName() {
        return caller.getMethodName();
    }

    public boolean isNativeMethod() {
        return caller.isNativeMethod();
    }

    public String getModuleName() {
        return caller.getModuleName();
    }

    public String getModuleVersion() {
        return caller.getModuleVersion();
    }

    public String getClassLoaderName() {
        return caller.getClassLoaderName();
    }

    public String getThreadName() {
        return caller.getThreadName();
    }

    public String getFileName() {
        return caller.getFileName();
    }

    public int getLineNumber() {
        return caller.getLineNumber();
    }

    public String getMessage() {
        return message;
    }

    /**
     * Converts the log entry to a JSON object representation.
     * 
     * @return A JSONObject containing the log entry details.
     */
    public JSONObject getJSONObject() {
        JSONObject json = new JSONObject();
        
        // Adding log level, timestamp, and message to JSON
        json.put("level", level.name());
        json.put("time", time);
        json.put("message", message);
        
        // If caller information is available, add those details as well
        if (caller != null) {
            JSONObject callerJson = new JSONObject();
            callerJson.put("className", caller.getClassName());
            callerJson.put("methodName", caller.getMethodName());
            callerJson.put("nativeMethod", caller.isNativeMethod());
            callerJson.put("moduleName", caller.getModuleName());
            callerJson.put("moduleVersion", caller.getModuleVersion());
            callerJson.put("classLoaderName", caller.getClassLoaderName());
            callerJson.put("threadName", caller.getThreadName());
            callerJson.put("fileName", caller.getFileName());
            callerJson.put("lineNumber", caller.getLineNumber());
            
            // Add caller info as a nested object in the main JSON object
            json.put("caller", callerJson);
        }
        return json;
    }

    /**
     * Returns a string representation of the LogEntry, formatted using the default pattern.
     * 
     * @return The formatted log entry.
     */
    @Override
    public String toString() {
        return LoggerOutput.format(this, LoggerOutput.DEFAULT_PATTERN);
    }
}
