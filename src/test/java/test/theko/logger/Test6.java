package test.theko.logger;

import java.io.InputStream;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.theko.logger.GlobalLogger;
import org.theko.logger.LogLevel;
import org.theko.logger.LoggerOutput;

public class Test6 {
    public static void main(String[] args) {
        try {
            InputStream is = Test5.class.getClassLoader().getResourceAsStream("outputConfig.json");
            GlobalLogger.setLoggerOutput(LoggerOutput.loadFrom(
                new JSONObject(new JSONTokener(is))
            ));
            GlobalLogger.setMaxLogsCount(100);
            GlobalLogger.log(LogLevel.INFO, "Logger initialized successfully.");
        } catch (Exception e) {
            System.err.println("Failed to initialize logger: " + e.getMessage());
        }

        for (long i = 0; i < Long.MAX_VALUE; i++) {
            GlobalLogger.debug("I: " + i);
        }
    }
}
