package org.theko.logger;

import static org.theko.logger.LogLevel.*;

import java.io.OutputStream;
import java.util.List;

import org.json.JSONObject;

/**
 * The GlobalLogger class is a utility for logging messages with different log levels. 
 * It provides methods for logging, output management, and log retention.
 * This class uses a singleton pattern to maintain a single logger instance globally.
 */
public class GlobalLogger {
    // The main logger instance used throughout the application
    public static Logger logger;
    
    // The output configuration for the logger
    public static LoggerOutput loggerOutput;

    // Static initializer block to set up the default logger and output
    static {
        LogOutputSettings defaultOutputSettings = new LogOutputSettings(
                "DefaultLogOutput",
                System.out
        );
        
        // Initialize loggerOutput with default settings
        loggerOutput = new LoggerOutput(defaultOutputSettings);
        
        // Initialize logger with the output and log level set to 2 (INFO)
        logger = new DefaultLogger(loggerOutput);
    }

    /**
     * Logs a message at a specified log level.
     *
     * @param level The log level at which the message should be logged.
     * @param message The message to be logged.
     */
    public static void log(LogLevel level, String message) {
        logger.log(level, message);
    }

    /**
     * Logs a message at a specified log level.
     *
     * @param level The log level at which the message should be logged.
     * @param message The message to be logged.
     * @param stackTraceOffset The stack trace offset to identify the caller info.
     */
    protected static LogEntry log(LogLevel level, String message, int stackTraceOffset) {
        if (logger instanceof ExtendedLogger) {
            return ((ExtendedLogger) logger).log(level, message, stackTraceOffset + 1);
        } else {
            return logger.log(level, message);
        }
    }

    /**
     * Logs a message at a specified log level, including the details of an exception.
     *
     * @param level The log level at which the message should be logged.
     * @param message The message to be logged.
     * @param e The exception whose stack trace will be logged.
     */
    public static LogEntry log(LogLevel level, String message, Throwable e) {
        return log(level, message, e, 1);
    }

    /**
     * Logs a message at a specified log level, including the details of an exception.
     *
     * @param level The log level at which the message should be logged.
     * @param message The message to be logged.
     * @param e The exception whose stack trace will be logged.
     * @param stackTraceOffset The stack trace offset to identify the caller info.
     */
    protected static LogEntry log(LogLevel level, String message, Throwable e, int stackTraceOffset) {
        if (logger instanceof ExtendedLogger) {
            return ((ExtendedLogger) logger).log(level, message, e, stackTraceOffset+1);
        } else {
            LogEntry log = logger.log(level, message);
            if (e != null) {
                logger.log(level, "Exception: " + e.toString());
                for (StackTraceElement element : e.getStackTrace()) {
                    logger.log(level, "\tat " + element.toString());
                }
            }
            return log;
        }
    }

    /**
     * Logs a message at the DEBUG log level.
     *
     * @param message The message to be logged.
     */
    public static LogEntry debug(String message) {
        return log(DEBUG, message, 1);
    }

    /**
     * Logs a message at the INFO log level.
     *
     * @param message The message to be logged.
     */
    public static LogEntry info(String message) {
        return log(INFO, message, 1);
    }

    /**
     * Logs a message at the WARN log level.
     *
     * @param message The message to be logged.
     */
    public static LogEntry warn(String message) {
        return log(WARN, message, 1);
    }

    /**
     * Logs a message at the ERROR log level.
     *
     * @param message The message to be logged.
     */
    public static LogEntry error(String message) {
        return log(ERROR, message, 1);
    }
    
    /**
     * Retrieves the current logger instance.
     * 
     * @return The logger instance being used globally.
     */
    public static Logger getLogger() {
        return logger;
    }

    /**
     * Retrieves the current logger output.
     * 
     * @return The {@link LoggerOutput}.
     */
    public static LoggerOutput getLoggerOutput() {
        return loggerOutput;
    }

    public static void setLogger(Logger logger, boolean addOutputToLogger) {
        GlobalLogger.logger = logger;
        if (addOutputToLogger && logger instanceof DefaultLogger) {
            ((DefaultLogger) GlobalLogger.logger).setLoggerOutput(loggerOutput);
        }
    }

    public static void setLoggerOutput(LoggerOutput loggerOutput) {
        GlobalLogger.loggerOutput = loggerOutput;
        if (logger instanceof DefaultLogger) {
            ((DefaultLogger) logger).setLoggerOutput(loggerOutput);
        }
    }

    /**
     * Retrieves a list of all current output settings.
     * 
     * @return A list of LogOutputSettings.
     */
    public static List<LogOutputSettings> getOutputs() {
        return loggerOutput.getOutputs();
    }

    /**
     * Sets the output settings for the logger.
     * 
     * @param outputs The list of new output settings to be used.
     */
    public static void setOutputs(List<LogOutputSettings> outputs) {
        loggerOutput.setOutputs(outputs);
    }

    /**
     * Adds a new output setting for the logger.
     * 
     * @param output The new output setting to add.
     */
    public static void addOutput(LogOutputSettings output) {
        loggerOutput.addOutput(output);
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
    public static List<LogOutputSettings> getOutputsWith(OutputStream os) {
        return loggerOutput.getOutputsWith(os);
    }

    /**
     * Removes a specific output setting from the logger.
     * 
     * @param output The output setting to remove.
     * @return True if the output was successfully removed, false otherwise.
     */
    public static boolean removeOutput(LogOutputSettings output) {
        return loggerOutput.removeOutput(output);
    }
    
    /**
     * Closes all output streams associated with the logger and clears the output settings.
     */
    public static void close() {
        loggerOutput.close();
    }

    /**
     * Sets the maximum number of logs to store. Older logs will be discarded once this limit is exceeded.
     * 
     * @param maxLogsCount The maximum number of logs to keep. Should be greater than 5 or -1 to disable.
     */
    public static void setMaxLogsCount(int maxLogsCount) {
        if (logger instanceof ExtendedLogger) {
            ((ExtendedLogger) logger).setMaxLogsCount(maxLogsCount);
        }
    }

    /**
     * Disables the maximum log count, allowing logs to accumulate indefinitely.
     */
    public static void disableMaxLogsCount() {
        if (logger instanceof ExtendedLogger) {
            ((ExtendedLogger) logger).disableMaxLogsCount();
        }
    }

    /**
     * Converts all the log entries into a JSON array. Each log entry is serialized to a JSONObject.
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
