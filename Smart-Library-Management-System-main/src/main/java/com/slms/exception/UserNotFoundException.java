package com.slms.exception;

/** Thrown when a lookup by user id/username fails to find a matching record. */
public class UserNotFoundException extends Exception {
    public UserNotFoundException(String message) { super(message); }
}
