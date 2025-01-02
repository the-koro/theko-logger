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
import java.util.stream.Collectors;

/**
 * The LoggerOutput class is responsible for managing the output streams where log entries are written.
 * It supports multiple output streams and different log formats.
 */
public class LoggerOutput {

    /**
     * The minimal log pattern, showing only the log level and message.
     * Example: [INFO] This is a log message.
     */
    public static final String MINIMAL_PATTERN = "[{level}] {message}";

    /**
     * A light log pattern that includes the time (hour, minute, second, millisecond), 
     * log level, and the message.
     * Example: [12:34:56:789] [INFO] | This is a log message.
     */
    public static final String LIGHT_PATTERN = "[{time HH:mm:ss:SSS}] [{level}] | {message}";

    /**
     * The default log pattern that includes the time (hour, minute, second, millisecond),
     * log level, the class and method name where the log was called, and the message.
     * Example: [12:34:56:789] [INFO] | com.example.MyClass.myMethod > This is a log message.
     */
    public static final String DEFAULT_PATTERN = "[{time HH:mm:ss:SSS}] [{level}] | {class}.{method} > {message}";

    /**
     * The detailed log pattern that includes the time (year, month, day, hour, minute, second, millisecond),
     * log level, thread name, the class and method name where the log was called, and the message.
     * Example: [2025:01:01 12:34:56:789] [INFO] | [main] | com.example.MyClass.myMethod > This is a log message.
     */
    public static final String DETAILED_PATTERN = "[{time yyyy:MM:dd HH:mm:ss:SSS}] [{level}] | [{thread}] | {class}.{method} > {message}";

    protected List<LogOutputSettings> outputs;

    /**
     * Constructs a LoggerOutput with the specified list of output settings.
     * 
     * @param outputs The list of LogOutputSettings defining where logs are output.
     */
    public LoggerOutput(List<LogOutputSettings> outputs) {
        this.outputs = outputs;
    }

    /**
     * Constructs a LoggerOutput with a single output setting.
     * 
     * @param output A LogOutputSettings defining a single output stream.
     */
    public LoggerOutput(LogOutputSettings output) {
        this.outputs = new CopyOnWriteArrayList<>();
        this.outputs.add(output);
    }

    /**
     * Retrieves the list of log output settings.
     * 
     * @return A list of LogOutputSettings.
     */
    public List<LogOutputSettings> getOutputs() {
        return this.outputs;
    }

    /**
     * Sets the list of log output settings.
     * 
     * @param outputs A list of LogOutputSettings.
     */
    public void setOutputs(List<LogOutputSettings> outputs) {
        this.outputs = outputs;
    }

    /**
     * Retrieves a list of {@link LogOutputSettings} that have a matching {@link OutputStream}.
     * 
     * This method filters the existing outputs to find those whose {@link OutputStream} is equal to
     * the one provided as an argument. If no matching {@link LogOutputSettings} are found, an empty
     * list will be returned.
     * 
     * @param os The {@link OutputStream} to match against the {@link LogOutputSettings}.
     * @return A list of {@link LogOutputSettings} with a matching {@link OutputStream}.
     */
    public List<LogOutputSettings> getOutputsWith(OutputStream os) {
        return outputs.stream()
                .filter(output -> output != null && os.equals(output.getOutputStream()))
                .collect(Collectors.toList());
    }

    /**
     * Adds a new log output setting.
     * 
     * @param output A LogOutputSettings to add.
     */
    public void addOutput(LogOutputSettings output) {
        this.outputs.add(output);
    }

    /**
     * Removes the specified log output setting.
     * 
     * @param output The LogOutputSettings to remove.
     * @return true if the output was removed, false otherwise.
     */
    public boolean removeOutput(LogOutputSettings output) {
        return outputs.remove(output);
    }

    /**
     * Removes all log output settings.
     */
    public void removeAllOutputs() {
        outputs.clear();
    }

    /**
     * Closes all output streams except System.out.
     * This method ensures that the streams are properly closed to prevent resource leaks.
     */
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
                    e.printStackTrace(); // Log the error to standard output
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
                // Check if the log level meets the output's preferred level
                if (entry.getLevel().ordinal() >= output.getPreferredLevel().ordinal()) {
                    String formattedMessage = format(entry, output.getPattern());
                    try {
                        // Write the formatted message to the output stream
                        output.getOutputStream().write(formattedMessage.getBytes(StandardCharsets.UTF_8));
                    } catch (IOException e) {
                        e.printStackTrace(); // Log the error to standard output
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

            boolean colored = false;

            if (pattern.startsWith("{colored}")) {
                colored = true;
                pattern = pattern.replace("{colored}", "");
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
                            if (colored) {
                                result.append(getColorFromLevel(entry.getLevel()));
                            }
                            result.append(entry.getLevel());
                            if (colored) {
                                result.append(getColorFromLevel(LogLevel.NONE));
                            }
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
                            String simpleClassName = className.substring(lastPointIndex + 1);
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

        /**
         * Returns the color associated with the given log level.
         * 
         * @param level The log level.
         * @return The ANSI escape code representing the color for the log level.
         */
        private static String getColorFromLevel(LogLevel level) {
            switch (level) {
                case DEBUG:
                    return "\u001B[34m";  // Blue
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
