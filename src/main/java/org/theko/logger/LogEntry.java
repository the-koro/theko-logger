package org.theko.logger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Represents a single log entry containing information about the log level, timestamp, 
 * caller information, tags, and the actual log message.
 */
public class LogEntry implements Serializable {
    private static final long serialVersionUID = 1L;  // Version control for serialization

    private final LogLevel level;  // Log level (DEBUG, INFO, ERROR, etc.)
    private final long time;  // Timestamp of when the log entry was created
    private final CallerInfo caller;  // Information about the caller (class, method, etc.)
    private final String message;  // The log message
    private final List<String> tags;  // Tags associated with the log entry for categorization or filtering

    /**
     * Constructs a LogEntry with all the required details.
     * 
     * @param level   The log level for the entry.
     * @param time    The time when the log entry was created.
     * @param caller  The caller information for the log entry.
     * @param message The log message.
     * @param tags    A list of tags associated with the log entry.
     */
    public LogEntry(LogLevel level, long time, CallerInfo caller, String message, List<String> tags) {
        if (level == LogLevel.NONE) {
            throw new IllegalArgumentException("Log level cannot be NONE.");
        }

        this.level = level;
        this.time = time;
        this.caller = caller;
        this.message = message;
        this.tags = tags;
    }

    /**
     * Constructs a LogEntry without tags.
     * 
     * @param level   The log level for the entry.
     * @param time    The time when the log entry was created.
     * @param caller  The caller information for the log entry.
     * @param message The log message.
     */
    public LogEntry(LogLevel level, long time, CallerInfo caller, String message) {
        this(level, time, caller, message, new ArrayList<>());
    }

    /**
     * Constructs a LogEntry with the specified level, time, and message.
     * The caller information is set to null, and no tags are assigned.
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
     * Retrieves the list of tags associated with the log entry.
     * 
     * @return A list of tags.
     */
    public List<String> getTags() {
        return tags;
    }

    /**
     * Retrieves the tags as an array.
     * 
     * @return An array of tags.
     */
    public String[] getTagsArray() {
        return tags.toArray(new String[0]);
    }

    /**
     * Converts the log entry to a JSON object representation.
     * 
     * @return A JSONObject containing the log entry details.
     */
    public JSONObject getJSONObject() {
        JSONObject json = new JSONObject();
        
        // Adding log level, timestamp, and message to JSON
        json.put("level", level.toString());
        json.put("time", time);
        json.put("message", message);

        JSONArray tagsArray = new JSONArray();
        if (tags == null || tags.size() == 0) {
            for (int i = 0; i < tags.size(); i++) {
                tagsArray.put(tags.get(i));
            }
        }

        json.put("tags", tagsArray);
        
        // If caller information is available, add those details as well
        if (caller != null) {
            JSONObject callerJson = new JSONObject();
            callerJson.put("className", caller.getClassName());
            callerJson.put("methodName", caller.getMethodName());
            callerJson.put("nativeMethod", caller.isNativeMethod());

            if (checkString(caller.getModuleName())) {
                callerJson.put("moduleName", caller.getModuleName());
            }
            if (checkString(caller.getModuleVersion())) {
                callerJson.put("moduleVersion", caller.getModuleVersion());
            }
            if (checkString(caller.getClassLoaderName())) {
                callerJson.put("classLoaderName", caller.getClassLoaderName());
            }
            if (checkString(caller.getThreadName())) {
                callerJson.put("threadName", caller.getThreadName());
            }
            callerJson.put("fileName", caller.getFileName());
            callerJson.put("lineNumber", caller.getLineNumber());
            
            // Add caller info as a nested object in the main JSON object
            json.put("caller", callerJson);
        }
        
        // Add tags if they exist
        if (!tags.isEmpty()) {
            json.put("tags", tags);
        }
        
        return json;
    }

    private static boolean checkString(String s) {
        return s != null && !s.isEmpty();
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
