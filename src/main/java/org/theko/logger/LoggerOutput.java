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

/**
 * LoggerOutput is responsible for managing log output, including formatting the log entries
 * and writing them to specified output streams.
 */
public class LoggerOutput {
    /** The minimal pattern without the time, class, and method information */
    public static final String MINIMAL_PATTERN = "{level} | {message}";

    /** The light pattern without the class, method information */
    public static final String LIGHT_PATTERN = "[{time HH:mm:ss:SSS}] {level} | {message}";

    /** The default pattern used for formatting log entries */
    public static final String DEFAULT_PATTERN = "[{time HH:mm:ss:SSS}] {level} | {class}.{method} > {message}";

    /** The detailed pattern with the file name, and line number information */
    public static final String DETAILED_PATTERN = "[{time HH:mm:ss:SSS}] {level} | File {file} at {lineNumber} | {class}.{method} > {message}";

    /** The pattern used to format log entries */
    protected String pattern;

    /** The formatter used to format log entries */
    protected Formatter formatter;

    /** The preferred log level to filter the output */
    protected LogLevel preferredLevel;

    /** List of output streams where log entries are written */
    protected List<OutputStream> outputStreams;

    /**
     * Constructor to initialize the LoggerOutput with a given pattern.
     * 
     * @param pattern The pattern used for formatting log entries.
     */
    public LoggerOutput(String pattern) {
        this.pattern = pattern;
        this.outputStreams = new CopyOnWriteArrayList<>();
        this.formatter = new Formatter();
    }

    /**
     * Sets the preferred log level. Only logs at this level or higher will be output.
     * 
     * @param level The preferred log level.
     */
    public void setPreferredLevel(LogLevel level) {
        this.preferredLevel = level;
    }

    /**
     * Gets the current preferred log level.
     * 
     * @return The preferred log level.
     */
    public LogLevel getPreferredLevel() {
        return this.preferredLevel;
    }

    /**
     * Removes all output streams from the logger.
     */
    public void removeAllOutputStreams() {
        outputStreams.clear();
    }

    /**
     * Adds an output stream where log entries will be written.
     * 
     * @param os The output stream to add.
     */
    public void addOutputStream(OutputStream os) {
        outputStreams.add(os);
    }

    /**
     * Gets the list of output streams currently used by the logger.
     * 
     * @return A list of output streams.
     */
    public List<OutputStream> getOutputStreams() {
        return outputStreams;
    }

    /**
     * Formats a log entry using the instance's pattern.
     * 
     * @param entry The log entry to format.
     * @return A formatted log entry string.
     */
    public String format(LogEntry entry) {
        return format(entry, pattern);
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
     * Outputs a formatted log entry to all the registered output streams.
     * If the log level of the entry is greater than or equal to the preferred level, the entry is written.
     * 
     * @param entry The log entry to output.
     */
    public void outputLogEntry(LogEntry entry) {
        // Only log entries with a level higher or equal to the preferred level are output
        if (entry.getLevel().ordinal() >= preferredLevel.ordinal()) {
            for (OutputStream os : outputStreams) {
                try {
                    // Write the formatted log entry to the output stream in UTF-8 encoding
                    os.write(format(entry, pattern).getBytes(StandardCharsets.UTF_8));
                } catch (IOException e) {
                    // Handle any IOExceptions that occur while writing to the stream
                    System.err.println("Failed to write to output stream: " + e.getMessage());
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
                    switch (placeholder) {
                        case "level":
                            result.append(entry.getLevel());
                            break;
                        case "time":
                            result.append(entry.getTime());
                            break;
                        case "class":
                            result.append(entry.getClassName());
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