package com.slms.exception;

/** Thrown when attempting to create a record that would violate a uniqueness rule. */
public class DuplicateEntryException extends Exception {
    public DuplicateEntryException(String message) { super(message); }
}
