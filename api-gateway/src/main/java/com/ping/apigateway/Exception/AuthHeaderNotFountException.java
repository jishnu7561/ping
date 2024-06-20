package com.ping.apigateway.Exception;

public class AuthHeaderNotFountException extends RuntimeException {
    public AuthHeaderNotFountException (String message) {
        super(message);
    }
}
