package org.theko.logger;

public enum LogLevel {
    DEBUG("DEBUG"),
    INFO("INFO"),
    WARN("WARNING"),
    ERROR("ERROR"),
    FATAL("FATAL"),
    NONE("NONE");

    private final String level;

    LogLevel(String level) {
        this.level = level;
    }

    @Override
    public String toString() {
        return level;
    }

    public String getLevel() {
        return level;
    }
}
