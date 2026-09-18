package com.slms.test;

/**
 * Minimal in-house assertion helper. The project avoids external test
 * dependencies (JUnit) since the build has no internet access to Maven
 * Central; this keeps the test suite self-contained and dependency-free
 * while still exercising the same validation logic a JUnit test would.
 */
public final class Assert {

    public static int passed = 0;
    public static int failed = 0;

    private Assert() { }

    public static void assertTrue(String testName, boolean condition) {
        if (condition) {
            passed++;
            System.out.println("  [PASS] " + testName);
        } else {
            failed++;
            System.out.println("  [FAIL] " + testName);
        }
    }

    public static void assertEquals(String testName, Object expected, Object actual) {
        boolean ok = expected == null ? actual == null : expected.equals(actual);
        assertTrue(testName + " (expected=" + expected + ", actual=" + actual + ")", ok);
    }

    public static void assertThrows(String testName, RunnableWithException r) {
        try {
            r.run();
            failed++;
            System.out.println("  [FAIL] " + testName + " (expected an exception, none thrown)");
        } catch (Exception e) {
            passed++;
            System.out.println("  [PASS] " + testName + " (threw " + e.getClass().getSimpleName() + ")");
        }
    }

    @FunctionalInterface
    public interface RunnableWithException {
        void run() throws Exception;
    }
}
