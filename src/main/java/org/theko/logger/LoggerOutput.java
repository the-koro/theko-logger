package org.theko.logger;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoggerOutput {
    public static final String DEFAULT_PATTERN = "[{time HH:mm:ss:SSS}] {level} | {class}.{method} > {message}";

    protected String pattern;
    protected Formatter formatter;
    protected LogLevel preferredLevel;

    protected List<OutputStream> outputStreams;

    public LoggerOutput(String pattern) {
        this.pattern = pattern;
        this.outputStreams = new CopyOnWriteArrayList<>();
        this.formatter = new Formatter();
    }

    public void setPreferredLevel(LogLevel level) {
        this.preferredLevel = level;
    }

    public LogLevel getPreferredLevel() {
        return this.preferredLevel;
    }

    public void removeAllOutputStreams() {
        outputStreams.clear();
    }

    public void addOutputStream(OutputStream os) {
        outputStreams.add(os);
    }

    public List<OutputStream> getOutputStreams() {
        return outputStreams;
    }

    public String format(LogEntry entry) {
        return format(entry, pattern);
    }

    public static String format(LogEntry entry, String pattern) {
        return Formatter.format(entry, pattern);
    }

    public void outputLogEntry(LogEntry entry) {
        if (entry.getLevel().ordinal() >= preferredLevel.ordinal())
        for (OutputStream os : outputStreams) {
            try {
                os.write(format(entry, pattern).getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                System.err.println("Failed to write to output stream: " + e.getMessage());
            }
        }
    }

    public static class Formatter {
        private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{([^}]+)}");

        public static String format(LogEntry entry, String pattern) {
            if (entry == null || pattern == null) {
                return "";
            }
    
            StringBuilder result = new StringBuilder();
            Pattern regex = PLACEHOLDER_PATTERN;
            Matcher matcher = regex.matcher(pattern);
    
            int lastMatchEnd = 0;
    
            while (matcher.find()) {
                result.append(pattern, lastMatchEnd, matcher.start());
                String placeholder = matcher.group(1);
    
                if (placeholder.startsWith("time ")) {
                    String dateFormat = placeholder.substring(5).trim();
                    result.append(formatTime(new Date(entry.getTime()), dateFormat));
                } else {
                    switch (placeholder) {
                        case "level":
                            result.append(entry.getLevel());
                            break;
                        case "time":
                            result.append(entry.getTime());
                            break;
                        case "class":
                            result.append(entry.getClassName());
                            break;
                        case "method":
                            result.append(entry.getMethodName());
                            break;
                        case "nativeMethod":
                            result.append(entry.isNativeMethod());
                            break;
                        case "module":
                            result.append(entry.getModuleName());
                            break;
                        case "moduleVersion":
                            result.append(entry.getModuleVersion());
                            break;
                        case "classLoader":
                            result.append(entry.getClassLoaderName());
                            break;
                        case "thread":
                            result.append(entry.getThreadName());
                            break;
                        case "file":
                            result.append(entry.getFileName());
                            break;
                        case "lineNumber":
                            result.append(entry.getLineNumber());
                            break;
                        case "message":
                            result.append(entry.getMessage());
                            break;
                    }
                }
    
                lastMatchEnd = matcher.end();
            }
    
            result.append(pattern, lastMatchEnd, pattern.length());
            return result.toString();
        }
    
        private static String formatTime(Date time, String dateFormat) {
            if (time == null || dateFormat == null || dateFormat.isEmpty()) {
                return "";
            }
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
                return sdf.format(time);
            } catch (IllegalArgumentException e) {
                return "INVALID_TIME_FORMAT";
            }
        }
    }
}
