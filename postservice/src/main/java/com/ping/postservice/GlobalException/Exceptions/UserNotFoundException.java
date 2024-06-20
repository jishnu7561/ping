package com.ping.postservice.GlobalException.Exceptions;

public class UserNotFoundException extends RuntimeException{

    public UserNotFoundException (String message) {
        super(message);
    }
}
