package com.mojang.logging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class LogUtils {
    private LogUtils() {
    }

    public static Logger getLogger() {
        return LogManager.getLogger("Minecraft");
    }
}
