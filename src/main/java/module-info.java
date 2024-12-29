module org.theko.logger {
    requires java.base;
    requires transitive org.json;

    exports org.theko.logger;
    exports org.theko.logger.timer;
}
