package org.apache.logging.log4j;

public final class LogManager {
    private LogManager() {
    }

    public static Logger getLogger(String name) {
        return new SimpleLogger(name);
    }
}
