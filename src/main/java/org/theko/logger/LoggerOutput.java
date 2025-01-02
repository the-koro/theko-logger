package org.theko.logger;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoggerOutput {
    // Constants for log patterns
    public static final String MINIMAL_PATTERN = "[{level}] {message}";
    public static final String LIGHT_PATTERN = "[{time HH:mm:ss:SSS}] [{level}] | {message}";
    public static final String DEFAULT_PATTERN = "[{time HH:mm:ss:SSS}] [{level}] | {class}.{method} > {message}";
    public static final String DETAILED_PATTERN = "[{time yyyy:MM:dd HH:mm:ss:SSS}] [{level}] | [{thread}] {class}.{method} > {message}";

    protected List<LogOutputSettings> outputs;

    public LoggerOutput(List<LogOutputSettings> outputs) {
        this.outputs = outputs;
    }

    public LoggerOutput(LogOutputSettings output) {
        this.outputs = new CopyOnWriteArrayList<>();
        this.outputs.add(output);
    }

    public List<LogOutputSettings> getOutputs() {
        return this.outputs;
    }

    public void setOutputs(List<LogOutputSettings> outputs) {
        this.outputs = outputs;
    }

    public void addOutput(LogOutputSettings output) {
        this.outputs.add(output);
    }

    public boolean removeOutput(LogOutputSettings output) {
        return outputs.remove(output);
    }

    public void removeAllOutputs() {
        outputs.clear();
    }

    public void close() {
        outputs.stream()
            .filter(output -> output != null && !output.getOutputStream().equals(System.out))
            .forEach(output -> {
                try {
                    OutputStream os = output.getOutputStream();
                    if (os != null) {
                        os.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        removeAllOutputs();
    }

    /**
     * Formats a log entry using the provided pattern.
     *
     * @param entry The log entry to format.
     * @param pattern The pattern used for formatting.
     * @return A formatted log entry string.
     */
    public static String format(LogEntry entry, String pattern) {
        return Formatter.format(entry, pattern);
    }

    /**
     * Immediately outputs a formatted log entry to all the registered output streams.
     * If the log level of the entry is greater than or equal to the preferred level, the entry is written.
     *
     * @param entry The log entry to output.
     */
    public void processToOut(LogEntry entry) {
        handleLogEntry(entry);
    }

    /**
     * Handles the log entry output to all outputs.
     * 
     * @param entry The log entry to output.
     */
    private void handleLogEntry(LogEntry entry) {
        if (entry != null) {
            for (LogOutputSettings output : outputs) {
                if (entry.getLevel().ordinal() >= output.getPreferredLevel().ordinal()) {
                    String formattedMessage = format(entry, output.getPattern());
                    try {
                        output.getOutputStream().write(formattedMessage.getBytes(StandardCharsets.UTF_8));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * The Formatter class is responsible for formatting log entries based on a pattern.
     */
    public static class Formatter {
        
        /** The pattern used to identify placeholders in the log pattern */
        private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{([^}]+)}");

        /**
         * Formats a log entry using the provided pattern.
         * 
         * @param entry The log entry to format.
         * @param pattern The pattern to format the log entry with.
         * @return The formatted log entry as a string.
         */
        public static String format(LogEntry entry, String pattern) {
            if (entry == null || pattern == null) {
                return ""; // Return empty string if entry or pattern is null
            }

            if (pattern.startsWith("{colored}")) {
                pattern = pattern.replace("{colored}", getColorFromLevel(entry.getLevel()));
                pattern = pattern.concat("\u001B[39m");
            }

            StringBuilder result = new StringBuilder();
            Matcher matcher = PLACEHOLDER_PATTERN.matcher(pattern); // Match placeholders in the pattern

            int lastMatchEnd = 0; // Keep track of the last match position to append non-placeholder text

            while (matcher.find()) {
                // Append the text before the current placeholder
                result.append(pattern, lastMatchEnd, matcher.start());
                String placeholder = matcher.group(1); // Extract the placeholder

                // Process the placeholder and append the corresponding value
                if (placeholder.startsWith("time ")) {
                    String dateFormat = placeholder.substring(5).trim();
                    result.append(formatTime(new Date(entry.getTime()), dateFormat)); // Format time with specified pattern
                } else {
                    // Handle various placeholders and append corresponding values
                    int lastPointIndex = -1;
                    String className;
                    switch (placeholder) {
                        case "level":
                            result.append(entry.getLevel());
                            break;
                        case "time":
                            result.append(entry.getTime());
                            break;
                        case "class":
                            className = entry.getClassName();
                            lastPointIndex = className.lastIndexOf('.');
                            if (lastPointIndex == -1) {
                                result.append(className);
                            }
                            String simpleClassName = className.substring(lastPointIndex+1, className.length());
                            result.append(simpleClassName);
                            break;
                        case "fullClass":
                            result.append(entry.getClassName());
                            break;
                        case "package":
                            className = entry.getClassName();
                            lastPointIndex = className.lastIndexOf('.');
                            if (lastPointIndex == -1) {
                                break;
                            }
                            String packageName = className.substring(0, lastPointIndex);
                            result.append(packageName);
                            break;
                        case "method":
                            result.append(entry.getMethodName());
                            break;
                        case "nativeMethod":
                            result.append(entry.isNativeMethod());
                            break;
                        case "module":
                            result.append(entry.getModuleName());
                            break;
                        case "moduleVersion":
                            result.append(entry.getModuleVersion());
                            break;
                        case "classLoader":
                            result.append(entry.getClassLoaderName());
                            break;
                        case "thread":
                            result.append(entry.getThreadName());
                            break;
                        case "file":
                            result.append(entry.getFileName());
                            break;
                        case "lineNumber":
                            result.append(entry.getLineNumber());
                            break;
                        case "message":
                            result.append(entry.getMessage());
                            break;
                    }
                }

                lastMatchEnd = matcher.end(); // Update the last match position
            }

            // Append the remaining text after the last placeholder
            result.append(pattern, lastMatchEnd, pattern.length());
            return result.toString();
        }

        private static String getColorFromLevel(LogLevel level) {
            switch (level) {
                case DEBUG:
                    return "\u001B[39m";  // Default color
                case INFO:
                    return "\u001B[32m";  // Green
                case WARN:
                    return "\u001B[33m";  // Yellow
                case ERROR:
                    return "\u001B[31m";  // Red
                case FATAL:
                    return "\u001B[35m";  // Purple
                default:
                    return "\u001B[39m";  // Default color if level is unknown
            }
        }

        /**
         * Formats the time using a specific date format.
         * 
         * @param time The time to format.
         * @param dateFormat The format to use for the date.
         * @return The formatted time as a string.
         */
        private static String formatTime(Date time, String dateFormat) {
            if (time == null || dateFormat == null || dateFormat.isEmpty()) {
                return ""; // Return empty string if time or date format is invalid
            }
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(dateFormat); // Create a SimpleDateFormat
                return sdf.format(time); // Format the time
            } catch (IllegalArgumentException e) {
                return "INVALID_TIME_FORMAT"; // Return placeholder if format is invalid
            }
        }
    }
}