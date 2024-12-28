# Logger Library

A simple and flexible logging library that supports different log levels, custom output formatting, and log entry management.

## Features

- **Log Levels**: Supports INFO, WARN, ERROR, DEBUG.
- **Log Management**: Stores logs with options to limit the number of logs.
- **Custom Output**: Multiple output streams (e.g., console, file).
- **Global Logger**: A global logger for easy usage across the application.
- **Custom Patterns**: Customize log output format.
- **Log Listener**: Set actions when new logs are created.
- **Watch Timer**: Measure the time taken for specific log entries or operations.

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

### Watch Timer Integration

#### Basic Timer Usage

The **WatchTimer** can be used to measure elapsed time for specific operations and log it for performance tracking. Here's how to integrate it with your logging:

```java
WatchTimer timer = new WatchTimer();
timer.start();

// Perform some operation
GlobalLogger.info("Operation started...");
// Simulate some operation (e.g., complex calculation or I/O)
Thread.sleep(1000); // Just an example; replace with actual operation

timer.stop();
long elapsedMillis = timer.getElapsedMillis();
GlobalLogger.info("Operation completed in " + elapsedMillis + " ms.");
```

#### Timer with Custom Logs

You can also use the `WatchTimer` to log the time taken for specific code blocks dynamically.

```java
GlobalLogger.info("Starting task...");

long elapsedTime = WatchTimer.calculateElapsedMillis(() -> {
    // Your code to measure
    // Example: complex computation or API call
    try { Thread.sleep(1500); } catch (InterruptedException e) { }
});

GlobalLogger.info("Task completed in " + elapsedTime + " milliseconds.");
```

#### Timer with Multiple Logs

If you need to measure multiple operations in your logs:

```java
WatchTimer timer1 = new WatchTimer();
timer1.start();
// Perform first operation
GlobalLogger.info("First task started...");
Thread.sleep(500);
timer1.stop();

WatchTimer timer2 = new WatchTimer();
timer2.start();
// Perform second operation
GlobalLogger.info("Second task started...");
Thread.sleep(1000);
timer2.stop();

GlobalLogger.info("First task duration: " + timer1.getElapsedMillis() + " ms.");
GlobalLogger.info("Second task duration: " + timer2.getElapsedMillis() + " ms.");
```

---

## Classes

- **Logger**: Interface for logging methods (`log()`, `getLastLog()`, `getAllLogs()`).
- **LogEntry**: Represents a single log entry.
- **LoggerOutput**: Manages log output and formatting.
- **DefaultLogger**: Default implementation of `Logger`.
- **GlobalLogger**: Global static logger for easy access.
- **LogLevel**: Enum for log levels (`INFO`, `WARN`, `ERROR`, `DEBUG`).
- **WatchTimer**: A utility class to measure elapsed time for specific tasks or operations.

## License

MIT License - see the [LICENSE](LICENSE) file for details.

---