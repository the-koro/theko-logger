package org.theko.logger;

import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.theko.logger.LoggerOutput.Formatter;

public class LoggerOutput {
    protected String pattern;
    protected Formatter formatter;

    public LoggerOutput(String pattern) {
        this.pattern = pattern;
        this.formatter = new Formatter();
    }

    public String format(LogEntry entry) {
        return formatter.format(entry, pattern);
    }

    public class Formatter {
        public String format(LogEntry entry, String pattern) {
            if (entry == null || pattern == null) {
                return "";
            }
    
            StringBuilder result = new StringBuilder();
            Pattern regex = Pattern.compile("\\{([^}]+)}");
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
                        case "className":
                            result.append(entry.getClassName());
                            break;
                        case "methodName":
                            result.append(entry.getMethodName());
                            break;
                        case "isNativeMethod":
                            result.append(entry.isNativeMethod());
                            break;
                        case "moduleName":
                            result.append(entry.getModuleName());
                            break;
                        case "moduleVersion":
                            result.append(entry.getModuleVersion());
                            break;
                        case "classLoaderName":
                            result.append(entry.getClassLoaderName());
                            break;
                        case "threadName":
                            result.append(entry.getThreadName());
                            break;
                        case "fileName":
                            result.append(entry.getFileName());
                            break;
                        case "lineNumber":
                            result.append(entry.getLineNumber());
                            break;
                        case "message":
                            result.append(entry.getMessage());
                            break;
                        default:
                            result.append("UNKNOWN_PLACEHOLDER");
                    }
                }
    
                lastMatchEnd = matcher.end();
            }
    
            result.append(pattern, lastMatchEnd, pattern.length());
            return result.toString();
        }
    
        private String formatTime(Date time, String dateFormat) {
            if (time == null || dateFormat == null || dateFormat.isEmpty()) {
                return "";
            }
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
                return sdf.format(time, null, null).toString(); // Explicitly correct and unambiguous
            } catch (IllegalArgumentException e) {
                return "INVALID_TIME_FORMAT";
            }
        }
    }
}
