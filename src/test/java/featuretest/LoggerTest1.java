package featuretest;

import org.theko.logger.DefaultLogger;
import org.theko.logger.LogLevel;

import shared.SharedFunctions;

public class LoggerTest1 {
    public static void main(String[] args) {
        DefaultLogger logger = (DefaultLogger)SharedFunctions.loadLogger(LoggerTest1.class.getResource("config1.json"));
        logger.debug("This is debug message.");
        logger.info("This is info message.");
        logger.warn("This is warn message.");
        logger.error("This is error message.");
        logger.fatal("This is fatal message.");

        // logger.[debug,info,warn,error,fatal](String message, String... tags)
        logger.info("This is message with tags.", "TAG1", "TAG2");
        logger.info("This is second message with tags.", "TAG1", "TAG2", "TAG3", "TAG4");

        String[] tags = {"TAG1", "TAG2", "TAG3"};
        logger.info("This is third message with tags.", tags);

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
            // logger.log(LogLevel level, String message, Throwable exception, String... tags)
            logger.log(LogLevel.ERROR, ex.getMessage(), ex, "TEST");
        }

        logger.info("Random value: " + random, "RANDOM");
    }
}
