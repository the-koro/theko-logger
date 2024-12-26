package org.theko.logger;

import java.io.OutputStream;
import java.util.List;

public class GlobalLogger {
    protected static final Logger logger;
    protected static final LoggerOutput loggerOutput;

    static {
        loggerOutput = new LoggerOutput(LoggerOutput.DEFAULT_PATTERN);
        loggerOutput.addOutputStream(System.out);
        logger = new DefaultLogger(loggerOutput, 2);
    }

    // Logging methods
    public static void log(LogLevel level, String message) {
        logger.log(level, message);
    }

    public static void info(String message) {
        logger.log(LogLevel.INFO, message);
    }

    public static void warn(String message) {
        logger.log(LogLevel.WARN, message);
    }

    public static void error(String message) {
        logger.log(LogLevel.ERROR, message);
    }

    public static void debug(String message) {
        logger.log(LogLevel.DEBUG, message);
    }

    // Access last log entry
    public static LogEntry getLastLog() {
        return logger.getLastLog();
    }

    // Access all log entries
    public static List<LogEntry> getAllLogs() {
        return logger.getAllLogs();
    }

    // LoggerOutput management
    public static void addOutputStream(OutputStream os) {
        loggerOutput.addOutputStream(os);
    }

    public static void removeAllOutputStreams() {
        loggerOutput.removeAllOutputStreams();
    }

    public static List<OutputStream> getOutputStreams() {
        return loggerOutput.getOutputStreams();
    }

    public static void setPattern(String pattern) {
        loggerOutput.pattern = pattern;
    }

    public static String getPattern() {
        return loggerOutput.pattern;
    }

    // Max log count management
    public static void setMaxLogsCount(int maxLogsCount) {
        if (logger instanceof DefaultLogger) {
            ((DefaultLogger) logger).setMaxLogsCount(maxLogsCount);
        }
    }

    public static void disableMaxLogsCount() {
        if (logger instanceof DefaultLogger) {
            ((DefaultLogger) logger).disableMaxLogsCount();
        }
    }
}
