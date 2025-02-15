package org.theko.logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.InvalidPathException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.theko.logger.out.RotatingFileOutputStream;

/**
 * LoggerConfig handles the configuration for the logging system.
 * It loads configuration from JSON and sets up the logger and its outputs.
 */
public class LoggerConfig {
    private final JSONObject config;
    private ExtendedLogger logger;
    private LoggerOutput loggerOutput;

    /**
     * Constructs a LoggerConfig from a JSONObject.
     *
     * @param config The configuration as a JSONObject.
     */
    public LoggerConfig(JSONObject config) {
        this.config = Objects.requireNonNull(config, "Configuration cannot be null.");
    }

    /**
     * Constructs a LoggerConfig from a configuration file.
     *
     * @param configFile The configuration file.
     */
    public LoggerConfig(File configFile) {
        Objects.requireNonNull(configFile, "Configuration file cannot be null.");
        if (!configFile.exists()) {
            throw new IllegalArgumentException("Configuration file does not exist: " + configFile.getPath());
        }
        try (FileReader reader = new FileReader(configFile)) {
            this.config = new JSONObject(new JSONTokener(reader));
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read configuration file: " + configFile.getPath(), e);
        }
    }

    /**
     * Constructs a LoggerConfig from a configuration file path.
     *
     * @param filePath The path to the configuration file.
     */
    public LoggerConfig(String filePath) {
        this(new File(filePath));
    }

    /**
     * Loads the configuration settings and initializes the logger and output.
     */
    public void load() {
        JSONObject loggerJson = config.optJSONObject("logger");
        JSONArray outputsJsonArray = config.optJSONArray("outputs");

        if (loggerJson == null && outputsJsonArray == null) {
            throw new IllegalArgumentException("Missing 'logger' or 'outputs' configuration.");
        }

        logger = loadLogger(loggerJson);
        loggerOutput = loadOutput(outputsJsonArray);
    }

    /**
     * Retrieves the initialized logger.
     *
     * @return The initialized ExtendedLogger.
     * @throws IllegalStateException If the logger is not initialized.
     */
    public ExtendedLogger getLogger() {
        return Optional.ofNullable(logger).orElseThrow(() -> new IllegalStateException("Logger is not initialized."));
    }

    /**
     * Retrieves the initialized logger output.
     *
     * @return The initialized LoggerOutput.
     * @throws IllegalStateException If the logger output is not initialized.
     */
    public LoggerOutput getLoggerOutput() {
        return Optional.ofNullable(loggerOutput).orElseThrow(() -> new IllegalStateException("LoggerOutput is not initialized."));
    }

    // Private helper methods below

    /**
     * Loads the log output settings from the configuration.
     *
     * @param outputs The JSON array of output settings.
     * @return The LoggerOutput object.
     */
    private LoggerOutput loadOutput(JSONArray outputs) {
        List<LogOutputSettings> outputSettings = new ArrayList<>();
        for (Object obj : outputs) {
            if (obj instanceof JSONObject) {
                outputSettings.add(loadLogOutputSettings((JSONObject) obj));
            }
        }
        return new LoggerOutput(outputSettings);
    }

    /**
     * Loads the settings for a single log output.
     *
     * @param output The JSON object for the log output settings.
     * @return The LogOutputSettings object.
     */
    private LogOutputSettings loadLogOutputSettings(JSONObject output) {
        String outputName = output.optString("name", String.valueOf(output.hashCode()));
        AtomicBoolean isJsonOutput = new AtomicBoolean();
        OutputStream os = createOutputStream(output, outputName, isJsonOutput);

        String levelStr = output.optString("level", "INFO");
        if (levelStr.equalsIgnoreCase("ALL")) {
            levelStr = "DEBUG";
        }
        LogLevel preferredLevel = LogLevel.fromString(levelStr);
        Map<LogLevel, String> patternsMap = loadPatternsMap(output.optJSONObject("patterns"));

        LogOutputSettings los = new LogOutputSettings(outputName, os, patternsMap, preferredLevel);
        los.setAsJsonOutput(isJsonOutput.get());
        System.out.println(isJsonOutput.get());

        return los;
    }

    /**
     * Creates an OutputStream based on the specified output settings.
     *
     * @param output The JSON object for the output settings.
     * @param outputName The name of the output.
     * @return The created OutputStream.
     */
    private OutputStream createOutputStream(JSONObject output, String outputName, AtomicBoolean isJsonOutput) {
        String targetOutput = output.optString("target", "file").toLowerCase();
        switch (targetOutput) {
            case "terminal":
                return System.out;
            case "file":
                return createFileOutputStream(output, outputName);
            case "json":
                isJsonOutput.set(true);
                return createFileOutputStream(output, outputName);
            default:
                throw new IllegalArgumentException("Invalid output target: " + targetOutput);
        }
    }

    /**
     * Creates a FileOutputStream for file-based logging.
     *
     * @param output The JSON object for the output settings.
     * @param outputName The name of the output.
     * @return The created FileOutputStream.
     */
    private OutputStream createFileOutputStream(JSONObject output, String outputName) {
        String formattedTime = formatTime(new Date(LogUtility.APPLICATION_START_MILLIS), "yyyy-MM-dd_HH.mm.ss");
        String filePath = parseFilePath(output.optString("filePath", "$temp\\log\\" + outputName + "\\"));
        File file = createFileFromFilePath(filePath, formattedTime);

        try {
            return output.has("rotation") ? loadRotation(output, file) : new FileOutputStream(file, true);
        } catch (IOException e) {
            throw new UncheckedIOException("Error creating file output stream", e);
        }
    }

    /**
     * Formats the given time using the specified date format.
     *
     * @param time The time to be formatted.
     * @param dateFormat The date format.
     * @return The formatted time string.
     */
    private String formatTime(Date time, String dateFormat) {
        if (time == null || dateFormat == null || dateFormat.isEmpty()) {
            return ""; // Return empty string if time or date format is invalid
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat); // Create a SimpleDateFormat
            return sdf.format(time); // Format the time
        } catch (IllegalArgumentException e) {
            return time.toString();
        }
    }

    /**
     * Loads rotation settings for file-based logging.
     *
     * @param output The JSON object containing rotation settings.
     * @param file The file to be used for rotation.
     * @return The RotatingFileOutputStream.
     * @throws IOException If an error occurs during file creation.
     */
    private OutputStream loadRotation(JSONObject output, File file) throws IOException {
        JSONObject rotationJson = output.getJSONObject("rotation");
        float maxSize = rotationJson.optFloat("maxSizeMB", 5.0f) * 1_000_000;
        int maxFiles = rotationJson.optInt("maxFiles", 10);
        String expireTime = rotationJson.optString("expireTime", "+7d");

        return new RotatingFileOutputStream(file.getParentFile().getCanonicalPath(), (long) maxSize, expireTime, maxFiles);
    }

    /**
     * Creates a file from the specified file path and name.
     *
     * @param filePath The path to create the file in.
     * @param fileName The name of the file to create.
     * @return The created file.
     */
    private File createFileFromFilePath(String filePath, String fileName) {
        filePath = filePath.replace("/", File.separator).replace("\\", File.separator);
        File file = new File(filePath);

        if (file.isDirectory() || filePath.endsWith(File.separator)) {
            file.mkdirs();
            file = new File(file, fileName + ".log");
        } else {
            file.getParentFile().mkdirs();
        }

        try {
            return file.exists() || file.createNewFile() ? file : null;
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to create log file: " + file.getAbsolutePath(), e);
        }
    }

    /**
     * Parses file paths and replaces placeholders with actual values.
     *
     * @param path The file path with placeholders.
     * @return The parsed file path with placeholders replaced.
     */
    private String parseFilePath(final String path) {
        Matcher matcher = Pattern.compile("\\$\\w+").matcher(path);
        String outPath = path;
        while (matcher.find()) {
            outPath = outPath.replace(matcher.group(), Optional.ofNullable(PATH_MAPPING.get(matcher.group()))
                    .orElseThrow(() -> new InvalidPathException(String.valueOf(path), "Unknown placeholder: " + matcher.group())));

        }
        return outPath.replace("/", "\\");
    }

    private static final Map<String, String> PATH_MAPPING = Map.of(
        "$temp", System.getenv("TEMP"),
        "$appdata", System.getenv("APPDATA"),
        "$user", System.getenv("USERPROFILE"),
        "$desktop", System.getenv("USERPROFILE") + "\\Desktop"
    );

    /**
     * Loads the patterns map from the configuration.
     *
     * @param patterns The JSON object containing the patterns.
     * @return The map of log levels to pattern strings.
     */
    private Map<LogLevel, String> loadPatternsMap(JSONObject patterns) {
        Map<LogLevel, String> patternsMap = new EnumMap<>(LogLevel.class);
        String defaultPattern = patterns != null ? patterns.optString("default", LoggerOutput.MINIMAL_PATTERN) : LoggerOutput.MINIMAL_PATTERN;

        for (LogLevel level : LogLevel.values()) {
            patternsMap.put(level, patterns != null ? patterns.optString(level.name(), defaultPattern) : defaultPattern);
        }
        patternsMap.put(LogLevel.NONE, "");

        return patternsMap;
    }

    /**
     * Loads the logger configuration from the JSON object.
     *
     * @param loggerJson The JSON object containing logger configuration.
     * @return The initialized ExtendedLogger.
     */
    private ExtendedLogger loadLogger(JSONObject loggerJson) {
        if (loggerJson == null) {
            return new DefaultLogger();
        }
        ExtendedLogger logger = loggerJson.optBoolean("async", false) ? new AsyncLogger() : new DefaultLogger();
        logger.setMaxLogsCount(loggerJson.optInt("maxLogs", -1));
        return logger;
    }
}
