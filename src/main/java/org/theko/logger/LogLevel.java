package org.theko.logger;

/**
 * Enum representing different log levels used in logging.
 * This allows categorizing log messages by their severity.
 */
public enum LogLevel {
    /** DEBUG log level, typically used for detailed debug information */
    DEBUG("DEBUG"),

    /** INFO log level, used for general informational messages */
    INFO("INFO"),

    /** WARN log level, used for potential issues that aren't critical */
    WARN("WARNING"),

    /** ERROR log level, used for error messages indicating failure */
    ERROR("ERROR"),

    /** FATAL log level, used for critical issues leading to program termination */
    FATAL("FATAL"),

    /** NONE log level, used to suppress all logging */
    NONE("NONE");

    // The string representation of the log level
    private final String level;

    /**
     * Constructor to initialize the log level with its string representation.
     * 
     * @param level The string value of the log level.
     */
    LogLevel(String level) {
        this.level = level;
    }

    /**
     * Returns the string representation of the log level.
     * 
     * @return A string representing the log level.
     */
    @Override
    public String toString() {
        return level;
    }

    /**
     * Gets the string value of the log level.
     * 
     * @return The string value of the log level.
     */
    public String getLevel() {
        return level;
    }

    /**
     * Converts a string to its corresponding LogLevel.
     * 
     * @param string The string representation of the log level.
     * @return The matching LogLevel.
     * @throws IllegalArgumentException if the string does not match any LogLevel.
     */
    public static LogLevel fromString(String string) {
        if (string == null || string.trim().isEmpty()) {
            throw new IllegalArgumentException("LogLevel cannot be null or empty");
        }
        for (LogLevel logLevel : LogLevel.values()) {
            if (logLevel.level.equalsIgnoreCase(string)) {
                return logLevel;
            }
        }
        throw new IllegalArgumentException("Unknown LogLevel: " + string);
    }
}
