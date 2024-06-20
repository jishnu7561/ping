package com.ping.authservice.GlobalExceptionHandler.Exceptions;

public class UsernameNotFoundException extends RuntimeException{

    public UsernameNotFoundException (String message) {
        super(message);
    }
}
