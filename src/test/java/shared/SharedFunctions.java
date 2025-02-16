package shared;
import java.net.URL;
import java.util.Objects;

import org.theko.logger.DefaultLogger;
import org.theko.logger.Logger;
import org.theko.logger.LoggerConfig;

public class SharedFunctions {
    public static Logger loadLogger(URL resourceUrl) {
        DefaultLogger logger = null;
        try {
            if (resourceUrl == null) {
                throw new IllegalStateException("Config file not found.");
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
