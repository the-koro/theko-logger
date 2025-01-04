package test.theko.logger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.theko.logger.GlobalLogger;
import org.theko.logger.LogLevel;
import org.theko.logger.LoggerOutput;

public class Test4 {
    public static void main(String[] args) throws FileNotFoundException {
        InputStream is = Test3.class.getClassLoader().getResourceAsStream("outputConfig.json");
        GlobalLogger.setLoggerOutput(LoggerOutput.loadFrom(
            new JSONObject(new JSONTokener(is))
        ));

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
