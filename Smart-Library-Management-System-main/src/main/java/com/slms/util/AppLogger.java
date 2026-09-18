package com.slms.util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Very small Singleton logger that timestamps messages and writes them to
 * logs/app.log, while also echoing WARN/ERROR to the console.
 * Demonstrates the Singleton design pattern and satisfies the project's
 * "logging / monitoring" non-functional requirement.
 */
public final class AppLogger {

    private static final String LOG_DIR = "logs";
    private static final String LOG_FILE = LOG_DIR + "/app.log";
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static AppLogger instance;

    private AppLogger() {
        new java.io.File(LOG_DIR).mkdirs();
    }

    public static synchronized AppLogger getInstance() {
        if (instance == null) {
            instance = new AppLogger();
        }
        return instance;
    }

    public void info(String message) { write("INFO", message, false); }
    public void warn(String message) { write("WARN", message, true); }
    public void error(String message, Throwable t) {
        write("ERROR", message + (t != null ? " | " + t.getMessage() : ""), true);
    }

    private synchronized void write(String level, String message, boolean alsoConsole) {
        String line = "[" + LocalDateTime.now().format(FMT) + "] [" + level + "] " + message;
        try (PrintWriter pw = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            pw.println(line);
        } catch (IOException e) {
            System.err.println("Logger failed to write to file: " + e.getMessage());
        }
        if (alsoConsole) {
            System.out.println(line);
        }
    }
}
