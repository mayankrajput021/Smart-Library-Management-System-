package com.slms.exception;

/** Thrown when a lookup by book id/ISBN fails to find a matching record. */
public class BookNotFoundException extends Exception {
    public BookNotFoundException(String message) { super(message); }
}
