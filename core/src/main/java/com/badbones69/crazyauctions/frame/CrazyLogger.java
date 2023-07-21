package com.badbones69.crazyauctions.frame;

import com.badbones69.crazyauctions.frame.utils.AdventureUtils;

public class CrazyLogger {

    public static void debug(String message) {
        debug(message, null);
    }

    public static void debug(String message, Exception exception) {
        log(CrazyCore.api().getConsolePrefix() + "<yellow>[DEBUG]</yellow> " + message);

        if (exception != null) exception.printStackTrace();
    }

    public static void info(String message) {
        log(CrazyCore.api().getConsolePrefix() + "<dark_aqua>[INFO]</dark_aqua> " + message);
    }

    public static void severe(String message) {
        severe(message, null);
    }

    public static void severe(String message, Exception exception) {
        log(CrazyCore.api().getConsolePrefix() + "<red>[ERROR]</red> " + message);

        if (exception != null) exception.printStackTrace();
    }

    public static void warn(String message) {
        warn(message, null);
    }

    public static void warn(String message, Exception exception) {
        log(CrazyCore.api().getConsolePrefix() + "<yellow>[WARN]</yellow> " + message);

        if (exception != null) exception.printStackTrace();
    }

    private static void log(String message) {
       CrazyCore.api().adventure().sendMessage(AdventureUtils.parse(message));
    }
}