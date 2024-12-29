package org.theko.logger;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public abstract class ExtendedLogger implements Logger {
    protected List<LogEntry> logs; // List to store all log entries
    protected int maxLogsCount = -1; // Maximum number of logs to store, -1 means no limit
    /**
     * Sets the maximum number of logs to store.
     * If the log count exceeds this limit, older logs will be discarded.
     * 
     * @param maxLogsCount The maximum number of logs to keep. Should be greater than 5 or -1 to disable.
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
     * @return A JSON array containing all the log entries as JSON objects.
     */
    public JSONObject getAllLogsAsJSON() {
        JSONObject allLogsJson = new JSONObject();
        JSONArray logsJsonArray = LogUtility.exportLogsToJSON(this.logs);
        allLogsJson.put("logs", logsJsonArray);  // Add the logs array under the "logs" key
        return allLogsJson;
    }
}
