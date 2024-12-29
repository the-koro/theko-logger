package org.theko.logger.test;

import org.theko.logger.AsyncLogger;
import org.theko.logger.GlobalLogger;
import org.theko.logger.LoggerOutput;
import org.theko.logger.timer.WatchTimer;

public class Test3 {
    public static void main(String[] args) {
        GlobalLogger.setPattern(LoggerOutput.DEFAULT_PATTERN + "\n");

        long defaultLoggerElapsed = WatchTimer.calculateElapsedMillis(() -> {
            for (int i = 0; i < 16000; i++) {
                GlobalLogger.info("Test. Iteration: " + i);
            }
        });

        GlobalLogger.setLogger(new AsyncLogger(null, 2));
        GlobalLogger.updateLoggerOutput();

        long asyncLoggerElapsed = WatchTimer.calculateElapsedMillis(() -> {
            for (int i = 0; i < 16000; i++) {
                GlobalLogger.info("Test. Iteration: " + i);
            }
        });

        GlobalLogger.info("Default logger elapsed time: " + defaultLoggerElapsed + " ms.");
        GlobalLogger.info("Async logger elapsed time: " + asyncLoggerElapsed + " ms.");
        GlobalLogger.close(); // Shutdown async logger
    }
}
