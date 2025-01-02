package org.theko.logger;

import static org.theko.logger.LogLevel.*;

import java.util.List;

import org.json.JSONObject;

public class GlobalLogger {
    public static Logger logger;
    public static LoggerOutput loggerOutput;

    static {
        LogOutputSettings defaultOutputSettings = new LogOutputSettings(
                System.out,
                LoggerOutput.MINIMAL_PATTERN + "\n"
        );
        
        loggerOutput = new LoggerOutput(defaultOutputSettings);
        logger = new DefaultLogger(loggerOutput, 2);
    }

    public static void log(LogLevel level, String message) {
        logger.log(level, message);
    }

    public static void log(LogLevel level, String message, Throwable e) {
        logger.log(level, message);
        if (e != null) {
            logger.log(level, "Exception: " + e.toString());
            for (StackTraceElement element : e.getStackTrace()) {
                logger.log(level, "\tat " + element.toString());
            }
        }
    }

    public static void debug(String message) {
        logger.log(DEBUG, message);
    }

    public static void info(String message) {
        logger.log(INFO, message);
    }

    public static void warn(String message) {
        logger.log(WARN, message);
    }

    public static void error(String message) {
        logger.log(ERROR, message);
    }
    
    public static Logger getLogger() {
        return logger;
    }

    public static LoggerOutput getLoggerOutput() {
        return loggerOutput;
    }

    public static List<LogOutputSettings> getOutputs() {
        return loggerOutput.getOutputs();
    }

    public static void setOutputs(List<LogOutputSettings> outputs) {
        loggerOutput.setOutputs(outputs);
    }

    public static void addOutput(LogOutputSettings output) {
        loggerOutput.addOutput(output);
    }

    public static boolean removeOutput(LogOutputSettings output) {
        return loggerOutput.removeOutput(output);
    }
    
    public static void close() {
        loggerOutput.close();
    }

    /**
     * Sets the maximum number of logs to store.
     * If the log count exceeds this limit, older logs will be discarded.
     * 
     * @param maxLogsCount The maximum number of logs to keep. Should be greater than 5 or -1 to disable.
     */
    public void setMaxLogsCount(int maxLogsCount) {
        if (logger instanceof ExtendedLogger) {
            ((ExtendedLogger) logger).setMaxLogsCount(maxLogsCount);
        }
    }

    /**
     * Disables the maximum log count, allowing logs to accumulate indefinitely.
     */
    public void disableMaxLogsCount() {
        if (logger instanceof ExtendedLogger) {
            ((ExtendedLogger) logger).disableMaxLogsCount();
        }
    }

    /**
     * Converts all the log entries into a JSON array.
     * Each log entry is serialized to a JSONObject using the {@link LogUtility}.
     * 
     * @return A JSON array containing all the log entries as JSON objects.
     */
    public static JSONObject getAllLogsAsJSON() {
        if (logger instanceof ExtendedLogger) {
            return ((ExtendedLogger) logger).getAllLogsAsJSON();
        }
        return null;
    }
}