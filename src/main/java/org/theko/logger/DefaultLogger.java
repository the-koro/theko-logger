package org.theko.logger;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class DefaultLogger implements Logger {
    protected List<LogEntry> logs;
    protected LoggerOutput loggerOutput;
    protected Consumer<LogEntry> onLogCreated;
    private final int stackTraceOffset;

    protected int maxLogsCount = -1;

    protected static final int STACK_TRACE_OFFSET_DEFAULT = 1;

    public DefaultLogger (LoggerOutput loggerOutput, int stackTraceOffset) {
        this.loggerOutput = loggerOutput;
        this.stackTraceOffset = stackTraceOffset;
        this.logs = new CopyOnWriteArrayList<>();
    }

    public DefaultLogger (LoggerOutput loggerOutput) {
        this(loggerOutput, STACK_TRACE_OFFSET_DEFAULT);
    }
    
    public DefaultLogger() {
        this(null, STACK_TRACE_OFFSET_DEFAULT);
    }

    public void setLoggerOutput(LoggerOutput loggerOutput) {
        this.loggerOutput = loggerOutput;
    }

    @Override
    public void log(LogLevel level, String message) {
        StackTraceElement[] stackTrace = getStackTrace();
        StackTraceElement callerElement = null;

        // Find the calling method to include in the log entry
        for (int i = 0; i < stackTrace.length; i++) {
            StackTraceElement element = stackTrace[i];
            
            if (element.getMethodName().equals("log") && element.getClassName().equals(this.getClass().getName())) {
                if (i + stackTraceOffset < stackTrace.length) {
                    callerElement = stackTrace[i + stackTraceOffset];
                }
                break;
            }
        }

        // Create and add the log entry
        LogEntry log = new LogEntry(
                level,
                System.currentTimeMillis(),
                new CallerInfo(callerElement, Thread.currentThread().getName()),
                message
            );
        logs.add(log);

        if (maxLogsCount != -1) {
            if (logs.size() > maxLogsCount) {
                logs.subList(0, logs.size() - maxLogsCount).clear();
            }
        }
        
        // Output the log entry if the loggerOutput is set
        if (loggerOutput != null) {
            loggerOutput.outputLogEntry(log);
        }

        if (onLogCreated != null) {
            onLogCreated.accept(log);
        }
    }

    public void setOnLogCreated(Consumer<LogEntry> onLogCreated) {
        this.onLogCreated = onLogCreated;
    }

    protected StackTraceElement[] getStackTrace() {
        return Thread.currentThread().getStackTrace();
    }
    
    public void setMaxLogsCount(int maxLogsCount) {
        if (maxLogsCount < 5 && maxLogsCount != -1) {
            throw new IllegalArgumentException("maxLogsCount need to be greater than 5, or equals to -1.");
        }
        this.maxLogsCount = maxLogsCount;
    }

    public void disableMaxLogsCount() {
        this.maxLogsCount = -1;
    }

    @Override
    public LogEntry getLastLog() {
        if (logs == null || logs.isEmpty()) {
            return null;
        }
        return logs.get(logs.size() - 1);
    }

    @Override
    public List<LogEntry> getAllLogs() {
        return logs;
    }
}
