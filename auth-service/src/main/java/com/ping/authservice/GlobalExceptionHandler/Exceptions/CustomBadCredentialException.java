package com.ping.authservice.GlobalExceptionHandler.Exceptions;

public class CustomBadCredentialException extends RuntimeException {
    public CustomBadCredentialException (String message) {
        super(message);
    }
}
