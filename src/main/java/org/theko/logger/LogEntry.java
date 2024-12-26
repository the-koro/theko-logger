package org.theko.logger;

public class LogEntry {
    private final LogLevel level;
    private final long time;
    private final CallerInfo caller;
    private final String message;

    public LogEntry (LogLevel level, long time, CallerInfo caller, String message) {
        this.level = level;
        this.time = time;
        this.caller = caller;
        this.message = message;
    }

    public LogEntry (LogLevel level, long time, String message) {
        this(level, time, null, message);
    }

    public LogLevel getLevel() {
        return level;
    }

    public long getTime() {
        return time;
    }

    public CallerInfo getCallerInfo() {
        return caller;
    }

    public String getClassName() {
        return caller.getClassName();
    }

    public String getMethodName() {
        return caller.getMethodName();
    }

    public boolean isNativeMethod() {
        return caller.isNativeMethod();
    }

    public String getModuleName() {
        return caller.getModuleName();
    }

    public String getModuleVersion() {
        return caller.getModuleVersion();
    }

    public String getClassLoaderName() {
        return caller.getClassLoaderName();
    }

    public String getThreadName() {
        return caller.getThreadName();
    }

    public String getFileName() {
        return caller.getFileName();
    }

    public int getLineNumber() {
        return caller.getLineNumber();
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return LoggerOutput.format(this, LoggerOutput.DEFAULT_PATTERN);
    }
}
