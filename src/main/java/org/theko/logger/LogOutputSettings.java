package org.theko.logger;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Class representing the settings for logging output.
 */
public class LogOutputSettings {
    protected String name;
    protected OutputStream os;
    protected Map<LogLevel, String> patternsMap;
    protected LogLevel preferredLevel;
    //protected Rotation rotationSettings;

    /**
     * Constructor for LoggerOutputSettings.
     *
     * @param name the name of the output
     * @param os the OutputStream for logging
     * @param pattern the log pattern
     * @param preferredLevel the log level to prefer
     */
    public LogOutputSettings(String name, OutputStream os, Map<LogLevel, String> patternsMap, LogLevel preferredLevel) {
        this.name = name;
        this.os = os;
        this.patternsMap = patternsMap;
        this.preferredLevel = preferredLevel;
    }

    /**
     * Constructor with default log level (WARN).
     *
     * @param name the name of the output
     * @param os the OutputStream for logging
     * @param pattern the log pattern
     */
    public LogOutputSettings(String name, OutputStream os, Map<LogLevel, String> patternsMap) {
        this(name, os, patternsMap, LogLevel.WARN);
    }

    /**
     * Constructor with default pattern (MINIMAL_PATTERN) and log level (WARN).
     *
     * @param name the name of the output
     * @param os the OutputStream for logging
     */
    public LogOutputSettings(String name, OutputStream os) {
        this(name, os, getDefaultPatternsMap(), LogLevel.WARN);
    }

    /**
     * Constructor with default pattern (MINIMAL_PATTERN) and log level (WARN).
     *
     * @param os the OutputStream for logging
     */
    public LogOutputSettings(OutputStream os) {
        this(os.toString(), os, getDefaultPatternsMap(), LogLevel.WARN);
    }

    public static Map<LogLevel, String> getDefaultPatternsMap() {
        Map<LogLevel, String> patternsMap = new HashMap<>();
        patternsMap.put(LogLevel.DEBUG, "[{level}] {message}\n");
        patternsMap.put(LogLevel.INFO, "[{level}] {message}\n");
        patternsMap.put(LogLevel.WARN, "[{level}] ({file}:{lineNumber}) {message}\n");
        patternsMap.put(LogLevel.ERROR, "[{level}] [{thread}] | ({file}:{lineNumber}) {message}\n");
        patternsMap.put(LogLevel.FATAL, "[{level}] [{thread}] | ({file}:{lineNumber}) {message}\n");
        // Skip LogLevel.NONE
        return patternsMap;
    }

    public static Map<LogLevel, String> getMapFromSinglePattern(String pattern) {
        Map<LogLevel, String> patternsMap = new HashMap<>();
        patternsMap.put(LogLevel.DEBUG, pattern);
        patternsMap.put(LogLevel.INFO, pattern);
        patternsMap.put(LogLevel.WARN, pattern);
        patternsMap.put(LogLevel.ERROR, pattern);
        patternsMap.put(LogLevel.FATAL, pattern);
        // Skip LogLevel.NONE
        return patternsMap;
    }

    public String getName() {
        return name;
    }

    public OutputStream getOutputStream() {
        return os;
    }

    public Map<LogLevel, String> getPatternsMap() {
        return patternsMap;
    }

    public LogLevel getPreferredLevel() {
        return preferredLevel;
    }

    public void setName(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("The name is empty");
        }
        this.name = name;
    }

    public void setOutputStream(OutputStream os) {
        if (os == null) {
            throw new IllegalArgumentException("OutputStream cannot be null");
        }
        this.os = os;
    }

    public void setPattern(Map<LogLevel, String> patternsMap) {
        if (patternsMap == null || patternsMap.isEmpty()) {
            throw new IllegalArgumentException("Patterns map cannot be null");
        }
        this.patternsMap = patternsMap;
    }

    public void setPattern(String pattern) {
        if (pattern == null || pattern.isEmpty()) {
            throw new IllegalArgumentException("Patterns map cannot be null");
        }
        this.patternsMap = getMapFromSinglePattern(pattern);
    }

    public void setPreferredLevel(LogLevel preferredLevel) {
        if (preferredLevel == null) {
            throw new IllegalArgumentException("LogLevel cannot be null");
        }
        this.preferredLevel = preferredLevel;
    }

    public String getPattern(LogLevel level) {
        return patternsMap.get(level);
    }

    /**
     * Closes the underlying OutputStream, if it is not already closed.
     * @throws IOException if an I/O error occurs during closing
     */
    public void close() throws IOException {
        if (os != null && !os.equals(System.out)) {
            os.close();
        }
    }
}
