package com.slms.test;

/**
 * Entry point that runs every test class and prints a final pass/fail
 * summary. Run with:
 *   java -cp out:testout com.slms.test.TestRunner
 */
public class TestRunner {
    public static void main(String[] args) {
        System.out.println("Running SLMS test suite...\n");
        BookServiceTest.run();
        UserServiceTest.run();
        TransactionServiceTest.run();

        System.out.println("\n=======================================");
        System.out.println("Passed: " + Assert.passed + "  Failed: " + Assert.failed);
        System.out.println("=======================================");
        if (Assert.failed > 0) {
            System.exit(1);
        }
    }
}
