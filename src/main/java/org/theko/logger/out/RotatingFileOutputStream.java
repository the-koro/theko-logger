package org.theko.logger.out;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class RotatingFileOutputStream extends OutputStream {
    private OutputStream currentStream;
    private File currentFile;
    private long maxSize;
    private long expireOffsetMillis;
    private long lastRotationTime;
    private String logDirectory;
    private String logFilePrefix;
    private int maxFiles; // Максимальное количество файлов
    private File logDirFile;

    public RotatingFileOutputStream(String logDirectory, long maxSize, String expireOffset, int maxFiles) {
        if (logDirectory == null || logDirectory.isEmpty()) {
            throw new IllegalArgumentException("Log directory cannot be null or empty.");
        }
        if (maxSize <= 0) {
            throw new IllegalArgumentException("Max size must be greater than 0.");
        }
        if (maxFiles <= 0) {
            throw new IllegalArgumentException("Max files must be greater than 0.");
        }
        
        this.logDirectory = logDirectory;
        this.maxSize = maxSize;
        this.logFilePrefix = "log_";
        this.expireOffsetMillis = parseExpireOffset(expireOffset);
        this.lastRotationTime = System.currentTimeMillis();
        this.maxFiles = maxFiles;
        this.logDirFile = new File(logDirectory);
        
        if (!logDirFile.exists() && !logDirFile.mkdirs()) {
            throw new RuntimeException("Failed to create log directory.");
        }

        try {
            this.currentFile = getNewLogFile();
            this.currentStream = new FileOutputStream(currentFile, true);
        } catch (IOException e) {
            throw new RuntimeException("Failed to open log file.", e);
        }
    }

    @Override
    public synchronized void write(int b) throws IOException {
        if (shouldRotate()) {
            rotate();
        }
        currentStream.write(b);
    }

    @Override
    public synchronized void write(byte[] b, int off, int len) throws IOException {
        if (shouldRotate()) {
            rotate();
        }
        currentStream.write(b, off, len);
    }

    private boolean shouldRotate() {
        long currentTime = System.currentTimeMillis();
        boolean isTimeExpired = (currentTime - lastRotationTime) >= expireOffsetMillis;
        boolean isSizeExceeded = currentFile.length() >= maxSize;
        return isTimeExpired || isSizeExceeded;
    }

    private void rotate() {
        try {
            currentStream.close();
            archiveOrRemoveOldFile(currentFile);
            currentFile = getNewLogFile();
            currentStream = new FileOutputStream(currentFile, true);
            lastRotationTime = System.currentTimeMillis();
            checkAndRemoveOldFiles();
        } catch (IOException e) {
            throw new RuntimeException("Failed to rotate log file.", e);
        }
    }

    private void archiveOrRemoveOldFile(File file) {
        File archiveFile = new File(logDirectory, "archived_" + file.getName());
        int count = 1;
        while (archiveFile.exists()) {
            archiveFile = new File(logDirectory, "archived_" + count + "_" + file.getName());
            count++;
        }
        boolean renamed = file.renameTo(archiveFile);
        if (renamed) {
            System.out.println("Archived old log file: " + archiveFile.getName());
        } else {
            System.err.println("Failed to archive log file: " + file.getName());
        }
    }

    private File getNewLogFile() {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
        String fileName = logFilePrefix + timestamp + ".log";
        return new File(logDirectory, fileName);
    }

    private long parseExpireOffset(String expireOffset) {
        long expireMillis = 0;
        if (expireOffset.endsWith("d")) {
            int days = Integer.parseInt(expireOffset.replace("d", ""));
            expireMillis = TimeUnit.DAYS.toMillis(days);
        } else if (expireOffset.endsWith("h")) {
            int hours = Integer.parseInt(expireOffset.replace("h", ""));
            expireMillis = TimeUnit.HOURS.toMillis(hours);
        } else if (expireOffset.endsWith("m")) {
            int minutes = Integer.parseInt(expireOffset.replace("m", ""));
            expireMillis = TimeUnit.MINUTES.toMillis(minutes);
        } else if (expireOffset.endsWith("s")) {
            int seconds = Integer.parseInt(expireOffset.replace("s", ""));
            expireMillis = TimeUnit.SECONDS.toMillis(seconds);
        } else {
            throw new IllegalArgumentException("Invalid expire offset format.");
        }
        return expireMillis;
    }

    private void checkAndRemoveOldFiles() {
        File[] files = logDirFile.listFiles((dir, name) -> name.startsWith(logFilePrefix));
        if (files != null && files.length > maxFiles) {
            Arrays.sort(files, Comparator.comparingLong(File::lastModified));
            int filesToDelete = files.length - maxFiles;
            for (int i = 0; i < filesToDelete; i++) {
                boolean deleted = files[i].delete();
                if (deleted) {
                    System.out.println("Deleted old log file: " + files[i].getName());
                } else {
                    System.err.println("Failed to delete old log file: " + files[i].getName());
                }
            }
        }
    }

    @Override
    public void close() throws IOException {
        if (currentStream != null) {
            currentStream.close();
        }
    }

    @Override
    public void flush() throws IOException {
        if (currentStream != null) {
            currentStream.flush();
        }
    }

    public static void main(String[] args) throws IOException {
        String logDir = "./logs";
        long maxSize = 10 * 1024; // 10 KB
        String expireOffset = "+1d"; // Rotate after 1 day
        int maxFiles = 5; // Keep a maximum of 5 log files

        try (RotatingFileOutputStream rotatingStream = new RotatingFileOutputStream(logDir, maxSize, expireOffset, maxFiles)) {
            for (int i = 0; i < 5000; i++) {
                rotatingStream.write(("Log message " + i + "\n").getBytes());
            }
        }
    }
}
