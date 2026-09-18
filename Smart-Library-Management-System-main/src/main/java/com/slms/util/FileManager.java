package com.slms.util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Singleton helper that centralizes all CSV file persistence for the
 * application (books.csv, users.csv, transactions.csv). Keeping file I/O
 * behind one class makes it trivial to later swap the storage engine
 * (e.g. for JDBC) without touching the DAO callers - Dependency
 * Inversion in spirit.
 */
public final class FileManager {

    private static FileManager instance;
    private static final String DATA_DIR = "data";

    private FileManager() {
        new File(DATA_DIR).mkdirs();
    }

    public static synchronized FileManager getInstance() {
        if (instance == null) {
            instance = new FileManager();
        }
        return instance;
    }

    /** Ensures the given file exists, creating it (with header) if missing. */
    public void ensureFile(String fileName, String header) {
        Path path = Paths.get(DATA_DIR, fileName);
        try {
            if (!Files.exists(path)) {
                Files.write(path, (header + System.lineSeparator()).getBytes(StandardCharsets.UTF_8));
                AppLogger.getInstance().info("Created data file: " + path);
            }
        } catch (IOException e) {
            AppLogger.getInstance().error("Failed to create data file " + fileName, e);
        }
    }

    /** Reads all data lines (excluding the header row). */
    public List<String> readLines(String fileName) {
        Path path = Paths.get(DATA_DIR, fileName);
        List<String> result = new ArrayList<>();
        if (!Files.exists(path)) return result;
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line = reader.readLine(); // skip header
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    result.add(line);
                }
            }
        } catch (IOException e) {
            AppLogger.getInstance().error("Failed to read " + fileName, e);
        }
        return result;
    }

    /** Overwrites the whole file (header + all rows). Used after update/delete. */
    public synchronized void writeAll(String fileName, String header, List<String> rows) {
        Path path = Paths.get(DATA_DIR, fileName);
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            writer.write(header);
            writer.newLine();
            for (String row : rows) {
                writer.write(row);
                writer.newLine();
            }
        } catch (IOException e) {
            AppLogger.getInstance().error("Failed to write " + fileName, e);
        }
    }

    /** Appends a single row to the end of the file. */
    public synchronized void appendLine(String fileName, String line) {
        Path path = Paths.get(DATA_DIR, fileName);
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            writer.write(line);
            writer.newLine();
        } catch (IOException e) {
            AppLogger.getInstance().error("Failed to append to " + fileName, e);
        }
    }
}
