package org.theko.logger;

import java.util.List;

public interface Logger {
    void log(LogLevel level, String message);
    LogEntry getLastLog();
    List<LogEntry> getAllLogs();
}
