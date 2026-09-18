package com.slms.util;

import java.util.List;

/** Generates simple sequential, prefixed IDs (e.g. B001, U007, T023) from existing data. */
public final class IdGenerator {

    private IdGenerator() { }

    public static String nextId(String prefix, List<String> existingRows) {
        int max = 0;
        for (String row : existingRows) {
            String[] parts = row.split(",", -1);
            if (parts.length == 0) continue;
            String id = parts[0];
            if (id.startsWith(prefix)) {
                try {
                    int num = Integer.parseInt(id.substring(prefix.length()));
                    max = Math.max(max, num);
                } catch (NumberFormatException ignored) {
                    // skip malformed id
                }
            }
        }
        return String.format("%s%03d", prefix, max + 1);
    }
}
