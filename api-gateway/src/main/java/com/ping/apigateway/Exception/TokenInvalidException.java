package com.ping.apigateway.Exception;

public class TokenInvalidException extends RuntimeException {

    public TokenInvalidException (String message) {
        super(message);
    }

}
