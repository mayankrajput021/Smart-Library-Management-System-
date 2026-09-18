package com.slms.exception;

/** Thrown when login credentials are invalid. */
public class AuthenticationException extends Exception {
    public AuthenticationException(String message) { super(message); }
}
