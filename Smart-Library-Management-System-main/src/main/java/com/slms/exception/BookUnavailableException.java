package com.slms.exception;

/** Thrown when attempting to issue a book that has no available copies. */
public class BookUnavailableException extends Exception {
    public BookUnavailableException(String message) { super(message); }
}
