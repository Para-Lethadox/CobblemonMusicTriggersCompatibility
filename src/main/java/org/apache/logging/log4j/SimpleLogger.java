package org.apache.logging.log4j;

final class SimpleLogger implements Logger {
    private final String name;

    SimpleLogger(String name) {
        this.name = name;
    }

    @Override
    public void info(String message) {
        System.out.println("[INFO] [" + name + "] " + message);
    }

    @Override
    public void debug(String message) {
        System.out.println("[DEBUG] [" + name + "] " + message);
    }

    @Override
    public void debug(String message, Object arg) {
        System.out.println("[DEBUG] [" + name + "] " + message.replace("{}", String.valueOf(arg)));
    }
}
