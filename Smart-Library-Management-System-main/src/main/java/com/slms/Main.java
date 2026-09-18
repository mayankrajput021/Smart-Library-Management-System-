package com.slms;

import com.slms.ui.ConsoleUI;
import com.slms.util.AppLogger;

/** Application entry point. */
public class Main {
    public static void main(String[] args) {
        AppLogger.getInstance().info("Application starting...");
        try {
            new ConsoleUI().start();
        } catch (Exception e) {
            AppLogger.getInstance().error("Unhandled exception, shutting down", e);
            System.out.println("A fatal error occurred. Check logs/app.log for details.");
        } finally {
            AppLogger.getInstance().info("Application stopped.");
        }
    }
}
