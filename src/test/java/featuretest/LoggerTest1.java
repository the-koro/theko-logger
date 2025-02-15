package featuretest;

import java.net.URL;
import java.util.Objects;

import org.theko.logger.DefaultLogger;
import org.theko.logger.LogLevel;
import org.theko.logger.Logger;
import org.theko.logger.LoggerConfig;

public class LoggerTest1 {
    public static void main(String[] args) {
        DefaultLogger logger = (DefaultLogger)loadLogger();
        logger.debug("This is debug message.");
        logger.info("This is info message.");
        logger.warn("This is warn message.");
        logger.error("This is error message.");
        logger.log(LogLevel.FATAL, "This is fatal message.");

        float random = (float)Math.random();
        random *= 5;
        try {
            switch (Math.round(random)) {
                case 0: throw new IllegalArgumentException("Exception 1");
                case 1: throw new IllegalAccessException("Exception 2");
                case 2: throw new IllegalStateException("Exception 3");
                case 3: throw new NullPointerException("Exception 4");
                case 4: throw new NoSuchFieldError("Exception 5");
                case 5: throw new Exception("Exception 6");
            }
        } catch (Exception ex) {
            logger.log(LogLevel.ERROR, ex.getMessage(), ex, "TEST");
        }

        logger.info("Random value: " + random, "RANDOM");
        
    }

    private static Logger loadLogger() {
        DefaultLogger logger = null;
        try {
            URL resourceUrl = LoggerTest1.class.getResource("config1.json");
            if (resourceUrl == null) {
                throw new IllegalStateException("Config file not found: config1.json");
            }

            String configPath = resourceUrl.toExternalForm().substring(6);
            System.out.println("Loading logger configuration from: " + configPath);

            LoggerConfig config = new LoggerConfig(configPath);
            config.load();
            System.out.println("Logger configuration loaded successfully.");

            logger = (DefaultLogger) config.getLogger();
            if (logger == null) {
                throw new IllegalStateException("Logger instance is null.");
            }

            logger.setLoggerOutput(Objects.requireNonNull(config.getLoggerOutput(), "Logger output is null."));
            logger.debug("Logger initialized and ready to use.", "LOGGER", "INIT");
            return logger;
        } catch (Exception e) {
            System.err.println("Error initializing logger: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
