package test.theko.logger;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.theko.logger.GlobalLogger;
import org.theko.logger.LogLevel;
import org.theko.logger.LogOutputSettings;
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
        GlobalLogger.getOutputsWith(System.out).forEach(output -> {
            output.setPattern("{colored}" + LoggerOutput.MINIMAL_PATTERN + "\n");
            output.setPreferredLevel(LogLevel.DEBUG);
        });

        // Use try-with-resources to ensure proper file handling
        try (FileOutputStream logFileStream = new FileOutputStream("test2.log")) {
            LogOutputSettings fileOut = new LogOutputSettings(
                logFileStream,
                LoggerOutput.DETAILED_PATTERN + "\n",
                LogLevel.DEBUG
            );

            GlobalLogger.addOutput(fileOut);
            GlobalLogger.info("Program launched.");

            new Test2();

            exportLogsToJSON(); // Handle JSON export in a separate method
        } catch (IOException e) {
            GlobalLogger.log(LogLevel.ERROR, "Error writing to log file or exporting JSON", e);
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
            GlobalLogger.log(LogLevel.ERROR, "Error writing logs to JSON file: ", e);
        }
    }
}
