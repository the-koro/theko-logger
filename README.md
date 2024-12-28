# Logger Library

A simple and flexible logging library that supports different log levels, custom output formatting, and log entry management.

## Features

- **Log Levels**: Supports INFO, WARN, ERROR, DEBUG.
- **Log Management**: Stores logs with options to limit the number of logs.
- **Custom Output**: Multiple output streams (e.g., console, file).
- **Global Logger**: A global logger for easy usage across the application.
- **Custom Patterns**: Customize log output format.
- **Log Listener**: Set actions when new logs are created.

## Usage

### Basic Logging

```java
GlobalLogger.info("Info message.");
GlobalLogger.warn("Warning message.");
GlobalLogger.error("Error message.");
GlobalLogger.debug("Debug message.");
```

### Set Custom Logger

```java
Logger customLogger = new DefaultLogger(new LoggerOutput(LoggerOutput.DEFAULT_PATTERN), 2);
GlobalLogger.setLogger(customLogger);
```

### Access Logs

```java
LogEntry lastLog = GlobalLogger.getLastLog();
List<LogEntry> allLogs = GlobalLogger.getAllLogs();
```

### Log Level Control

```java
GlobalLogger.setMaxLogsCount(100); // Limit log entries
GlobalLogger.disableMaxLogsCount(); // Disable limit
```

### Custom Log Pattern

```java
GlobalLogger.setPattern("[%level] - %message");
```

## Classes

- **Logger**: Interface for logging methods (`log()`, `getLastLog()`, `getAllLogs()`).
- **LogEntry**: Represents a single log entry.
- **LoggerOutput**: Manages log output and formatting.
- **DefaultLogger**: Default implementation of `Logger`.
- **GlobalLogger**: Global static logger for easy access.
- **LogLevel**: Enum for log levels (`INFO`, `WARN`, `ERROR`, `DEBUG`).

## Example

```java
public class Main {
    public static void main(String[] args) {
        GlobalLogger.info("Info message");
        LogEntry lastLog = GlobalLogger.getLastLog();
        System.out.println("Last Log: " + lastLog);
    }
}
```

## License

MIT License - see the [LICENSE](LICENSE) file for details.

---
