# Logger Library

A flexible logging library for Java that supports different log levels, custom output formatting, and log entry management. This library provides easy-to-use logging functionality with asynchronous support, JSON export, and various sorting/filtering capabilities.

## Features

- **Log Levels**: Supports `INFO`, `WARN`, `ERROR`, `DEBUG`.
- **Log Management**: Stores logs with options to limit the number of logs.
- **Custom Output**: Multiple output streams (e.g., console, file).
- **Global Logger**: A global logger for easy usage across the application.
- **Custom Patterns**: Customize log output format.
- **Log Listener**: Set actions when new logs are created.
- **Watch Timer**: Measure the time taken for specific log entries or operations.
- **Asynchronous Logging**: Logs are processed asynchronously for non-blocking performance.
- **Log Export to JSON**: Export log entries as a JSON array for easy integration or analysis.
- **Log Sorting and Filtering**: Sort and filter logs by time, level, message, and more.

## Installation

To add the `Logger` library to your project, download the JAR file.

## Usage

### Basic Logging

Log messages with different levels:

```java
GlobalLogger.info("Info message.");
GlobalLogger.warn("Warning message.");
GlobalLogger.error("Error message.");
GlobalLogger.debug("Debug message.");
```

### Set Custom Logger

You can create and use a custom logger with a specific output and log limit:

```java
Logger customLogger = new DefaultLogger(new LoggerOutput(LoggerOutput.DEFAULT_PATTERN), 2);
GlobalLogger.setLogger(customLogger);
```

### Access Logs

Retrieve the most recent log or all logs:

```java
LogEntry lastLog = GlobalLogger.getLastLog();
List<LogEntry> allLogs = GlobalLogger.getAllLogs();
```

### Log Level Control

Control the number of logs to keep:

```java
GlobalLogger.setMaxLogsCount(100);  // Limit log entries
GlobalLogger.disableMaxLogsCount(); // Disable limit
```

### Custom Log Pattern

Set a custom pattern for log output:

```java
GlobalLogger.setPattern("[%level] - %message");
```

### Watch Timer Integration

The **WatchTimer** can be used to measure elapsed time for specific operations and log it for performance tracking.

#### Basic Timer Usage

```java
WatchTimer timer = new WatchTimer();
timer.start();

// Perform some operation
GlobalLogger.info("Operation started...");
Thread.sleep(1000); // Simulate some operation

timer.stop();
long elapsedMillis = timer.getElapsedMillis();
GlobalLogger.info("Operation completed in " + elapsedMillis + " ms.");
```

#### Timer with Custom Logs

```java
long elapsedTime = WatchTimer.calculateElapsedMillis(() -> {
    // Your code to measure
    try { Thread.sleep(1500); } catch (InterruptedException e) { }
});

GlobalLogger.info("Task completed in " + elapsedTime + " milliseconds.");
```

#### Timer with Multiple Logs

```java
WatchTimer timer1 = new WatchTimer();
timer1.start();
GlobalLogger.info("First task started...");
Thread.sleep(500);
timer1.stop();

WatchTimer timer2 = new WatchTimer();
timer2.start();
GlobalLogger.info("Second task started...");
Thread.sleep(1000);
timer2.stop();

GlobalLogger.info("First task duration: " + timer1.getElapsedMillis() + " ms.");
GlobalLogger.info("Second task duration: " + timer2.getElapsedMillis() + " ms.");
```

---

## Classes

### Logger

The main interface for logging methods, including:
- `log(LogLevel level, String message)`
- `getLastLog()`
- `getAllLogs()`

### LogEntry

Represents a single log entry, containing information like timestamp, level, message, and caller information.

### LoggerOutput

Manages log output and formatting. It can display logs in the console, write them to a file, or direct logs to any other stream.

### DefaultLogger

A default implementation of the `Logger` interface that supports logging with customizable output and pattern.

### GlobalLogger

A global static logger for easy access throughout the application.

### LogLevel

An enum representing log levels: `INFO`, `WARN`, `ERROR`, `DEBUG`.

### WatchTimer

A utility class to measure elapsed time for specific tasks or operations. It helps to track performance by logging time measurements.

### ExtendedLogger

An extension of the `Logger` interface, adding functionality for log management (like limiting the number of logs) and JSON export.

### AsyncLogger

Asynchronous logger implementation that processes log entries in a background thread, supporting multi-threaded environments without blocking the main thread.

### LogUtility

A utility class that helps with sorting and filtering logs. It supports sorting by criteria such as time, level, message, or module. Additionally, it allows exporting logs as a JSON array and filtering by log level, time, or other attributes.

---

## Advanced Features

### Asynchronous Logging with `AsyncLogger`

The `AsyncLogger` allows for non-blocking logging by offloading the log processing to a background thread. This is ideal for performance-critical applications where logging should not interfere with the main logic flow.

```java
AsyncLogger asyncLogger = new AsyncLogger(new ConsoleLoggerOutput());
asyncLogger.info("This is an async log.");
```

Logs will be processed asynchronously, and you can also set a custom consumer to handle logs as they are created.

### Log Sorting and Filtering

Sort and filter logs based on various criteria using the `LogUtility` class:

```java
List<LogEntry> sortedLogs = LogUtility.sortBy(allLogs, "time");
List<LogEntry> filteredLogs = LogUtility.filterByLevel(allLogs, LogLevel.ERROR);
```

Logs can be sorted by time, method, level, etc., and filtered by time range or log level.

---

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.