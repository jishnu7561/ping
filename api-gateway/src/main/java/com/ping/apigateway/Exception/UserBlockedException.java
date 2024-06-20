package com.ping.apigateway.Exception;

public class UserBlockedException extends RuntimeException{

    public UserBlockedException (String message) {
        super(message);
    }
}
