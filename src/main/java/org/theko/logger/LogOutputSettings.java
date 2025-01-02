package org.theko.logger;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Class representing the settings for logging output.
 */
public class LogOutputSettings {
    protected OutputStream os;
    protected String pattern;
    protected LogLevel preferredLevel;

    /**
     * Constructor for LoggerOutputSettings.
     *
     * @param os the OutputStream for logging
     * @param pattern the log pattern
     * @param preferredLevel the log level to prefer
     */
    public LogOutputSettings(OutputStream os, String pattern, LogLevel preferredLevel) {
        this.os = os;
        this.pattern = pattern;
        this.preferredLevel = preferredLevel;
    }

    /**
     * Constructor with default log level (WARN).
     *
     * @param os the OutputStream for logging
     * @param pattern the log pattern
     */
    public LogOutputSettings(OutputStream os, String pattern) {
        this(os, pattern, LogLevel.WARN);
    }

    /**
     * Constructor with default pattern (MINIMAL_PATTERN) and log level (WARN).
     *
     * @param os the OutputStream for logging
     */
    public LogOutputSettings(OutputStream os) {
        this(os, LoggerOutput.MINIMAL_PATTERN, LogLevel.WARN);
    }

    public OutputStream getOutputStream() {
        return os;
    }

    public String getPattern() {
        return pattern;
    }

    public LogLevel getPreferredLevel() {
        return preferredLevel;
    }

    public void setOutputStream(OutputStream os) {
        if (os == null) {
            throw new IllegalArgumentException("OutputStream cannot be null");
        }
        this.os = os;
    }

    public void setPattern(String pattern) {
        if (pattern == null) {
            throw new IllegalArgumentException("Pattern cannot be null");
        }
        this.pattern = pattern;
    }

    public void setPreferredLevel(LogLevel preferredLevel) {
        if (preferredLevel == null) {
            throw new IllegalArgumentException("LogLevel cannot be null");
        }
        this.preferredLevel = preferredLevel;
    }

    /**
     * Closes the underlying OutputStream, if it is not already closed.
     * @throws IOException if an I/O error occurs during closing
     */
    public void close() throws IOException {
        if (os != null) {
            os.close();
        }
    }
}
