package org.theko.logger;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONArray;

public class LogUtility {
     /**
     * Sorts the provided list of log entries based on the specified sorting criteria.
     * 
     * @param logs The list of log entries to sort.
     * @param sortBy The criterion to sort by (class name, method name, file name, line, time, message, level, module).
     * @return A sorted list of log entries.
     */
    public static List<LogEntry> sortBy(List<LogEntry> logs, String sortBy) {
        switch (sortBy.toLowerCase()) {
            case "class":
                return logs.stream()
                        .sorted(Comparator.comparing(log -> log.getClassName()))
                        .collect(Collectors.toList());
            case "method":
                return logs.stream()
                        .sorted(Comparator.comparing(log -> log.getMethodName()))
                        .collect(Collectors.toList());
            case "file":
                return logs.stream()
                        .sorted(Comparator.comparing(log -> log.getFileName()))
                        .collect(Collectors.toList());
            case "linenumber":
                return logs.stream()
                        .sorted(Comparator.comparingInt(LogEntry::getLineNumber))
                        .collect(Collectors.toList());
            case "time":
                return logs.stream()
                        .sorted(Comparator.comparingLong(LogEntry::getTime))
                        .collect(Collectors.toList());
            case "message":
                return logs.stream()
                        .sorted(Comparator.comparing(LogEntry::getMessage))
                        .collect(Collectors.toList());
            case "level":
                return logs.stream()
                        .sorted(Comparator.comparingInt(log -> log.getLevel().ordinal()))
                        .collect(Collectors.toList());
            case "module":
                return logs.stream()
                        .sorted(Comparator.comparing(LogEntry::getModuleName))
                        .collect(Collectors.toList());
            default:
                throw new IllegalArgumentException("Invalid sort criterion: " + sortBy);
        }
    }

    /**
     * Filters the provided list of log entries based on the specified range criterion.
     * 
     * @param logs The list of log entries to filter.
     * @param rangeBy The criterion to filter by (line number, log level ordinal, time).
     * @param start The start value for the range (inclusive).
     * @param end The end value for the range (exclusive for line number, inclusive for time and level).
     * @return A list of log entries that fall within the specified range.
     */
    public static List<LogEntry> range(List<LogEntry> logs, String rangeBy, int start, int end) {
        switch (rangeBy.toLowerCase()) {
            case "linenumber":
                return logs.stream()
                        .filter(log -> log.getLineNumber() >= start && log.getLineNumber() <= end)
                        .collect(Collectors.toList());
            case "level":
                return logs.stream()
                        .filter(log -> log.getLevel().ordinal() >= start && log.getLevel().ordinal() <= end)
                        .collect(Collectors.toList());
            case "time":
                return logs.stream()
                        .filter(log -> log.getTime() >= start && log.getTime() <= end)
                        .collect(Collectors.toList());
            default:
                throw new IllegalArgumentException("Invalid range criterion: " + rangeBy);
        }
    }

    /**
     * Filters the logs based on a start time and end time range.
     * 
     * @param logs The list of log entries to filter.
     * @param startTime The start time for the range (inclusive).
     * @param endTime The end time for the range (inclusive).
     * @return A list of log entries that fall within the specified time range.
     */
    public static List<LogEntry> filterByTime(List<LogEntry> logs, long startTime, long endTime) {
        return logs.stream()
                .filter(log -> log.getTime() >= startTime && log.getTime() <= endTime)
                .collect(Collectors.toList());
    }

    /**
     * Filters logs by their log level.
     * 
     * @param logs The list of log entries to filter.
     * @param level The log level to filter by (e.g., INFO, DEBUG, ERROR).
     * @return A list of log entries that match the specified log level.
     */
    public static List<LogEntry> filterByLevel(List<LogEntry> logs, LogLevel level) {
        return logs.stream()
                .filter(log -> log.getLevel() == level)
                .collect(Collectors.toList());
    }

    /**
     * Exports a list of log entries to a JSON array.
     * Each log entry is serialized into a JSON object with relevant log information.
     * 
     * @param logs The list of log entries to export.
     * @return A JSONArray containing all log entries in JSON format.
     */
    public static JSONArray exportLogsToJSON(List<LogEntry> logs) {
        JSONArray jsonArray = new JSONArray();

        for (LogEntry log : logs) {
            jsonArray.put(log.getJSONObject());
        }
        return jsonArray;
    }
}