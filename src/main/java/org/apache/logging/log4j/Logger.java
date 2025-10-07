package org.apache.logging.log4j;

public interface Logger {
    void info(String message);

    void debug(String message);

    void debug(String message, Object arg);
}
