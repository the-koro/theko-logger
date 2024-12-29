package org.theko.logger.test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.theko.logger.GlobalLogger;
import org.theko.logger.LoggerOutput;
import org.theko.logger.timer.WatchTimer;

public class Test2 {
    
    class MyClass {
        private int x;

        public MyClass(int x) {
            this.x = x;
            logMyClassCreation();
        }

        private void logMyClassCreation() {
            GlobalLogger.debug("New 'MyClass' instance created.");
        }

        public void printX() {
            GlobalLogger.info("x value is " + x);
            System.out.println(x);
        }
    }

    public Test2() {
        MyClass myClass = new MyClass(42);
        long execElapsed = WatchTimer.calculateElapsedMillis(() -> myClass.printX());
        logExecutionTime(execElapsed);
    }

    private void logExecutionTime(long execElapsed) {
        GlobalLogger.info("printX() function from MyClass executed with elapsed time: " + execElapsed + " ms.");
    }

    public static void main(String[] args) {
        // Set log pattern and output to console and file
        GlobalLogger.setPattern(LoggerOutput.DEFAULT_PATTERN + "\n");

        // Use try-with-resources to ensure proper file handling
        try (FileOutputStream logFileStream = new FileOutputStream("test2.log")) {
            GlobalLogger.addOutputStream(logFileStream);
            GlobalLogger.info("Program launched.");

            new Test2();

            exportLogsToJSON(); // Handle JSON export in a separate method
        } catch (IOException e) {
            GlobalLogger.error("Error writing to log file or exporting JSON", e);
        } finally {
            GlobalLogger.close();
        }
    }

    private static void exportLogsToJSON() {
        // Use try-with-resources for output stream management
        try (FileOutputStream jsonLogsFOS = new FileOutputStream("test2logs.json")) {
            String jsonStr = GlobalLogger.getAllLogsAsJSON().toString(4);
            jsonLogsFOS.write(jsonStr.getBytes(StandardCharsets.UTF_8));
            GlobalLogger.info("Logs exported to JSON successfully.");
        } catch (IOException e) {
            GlobalLogger.error("Error writing logs to JSON file: ", e);
        }
    }
}
