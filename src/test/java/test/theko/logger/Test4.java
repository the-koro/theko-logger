package test.theko.logger;

import static org.theko.logger.LoggerOutput.*;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.theko.logger.GlobalLogger;
import org.theko.logger.LogLevel;
import org.theko.logger.LogOutputSettings;

public class Test4 {
    public static void main(String[] args) throws FileNotFoundException {
        GlobalLogger.getOutputsWith(System.out).forEach(output -> {
            output.setPattern("{colored}" + MINIMAL_PATTERN + "\n");
            output.setPreferredLevel(LogLevel.DEBUG);
        });
        
        LogOutputSettings fileOut = new LogOutputSettings(
            new FileOutputStream("test5.log"),
            DETAILED_PATTERN + "\n",
            LogLevel.DEBUG
        );

        GlobalLogger.addOutput(fileOut);

        GlobalLogger.log(LogLevel.DEBUG, "This is debug message.");
        GlobalLogger.log(LogLevel.INFO, "This is info message.");
        GlobalLogger.log(LogLevel.WARN, "This is warning message.");
        GlobalLogger.log(LogLevel.ERROR, "This is error message.");
        GlobalLogger.log(LogLevel.FATAL, "This is fatal message.");

        method1();
    }

    private static void method1() { method2(); }
    private static void method2() { method3(); }
    private static void method3() { method4(); }
    private static void method4() { method5(); }

    private static void method5() {
        try {
            throw new IOException("Something happened!");
        } catch (IOException ioex) {
            GlobalLogger.log(LogLevel.WARN, "An exception occured.", ioex);
        }
    }
}
