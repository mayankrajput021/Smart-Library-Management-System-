package com.slms.exception;

/** Thrown when user-supplied input fails validation rules. */
public class InvalidInputException extends Exception {
    public InvalidInputException(String message) { super(message); }
}
