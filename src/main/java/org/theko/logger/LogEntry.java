package org.theko.logger;

public class LogEntry {
    private final LogLevel level;
    private final CallerInfo caller;
    private final String message;

    public LogEntry (LogLevel level, CallerInfo caller, String message) {
        this.level = level;
        this.caller = caller;
        this.message = message;
    }

    public LogEntry (LogLevel level, String message) {
        this(level, null, message);
    }

    public LogLevel getLevel() {
        return level;
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
        LoggerOutput.format(this, LoggerOutput.DEFAULT_PATTERN);
    }
}
