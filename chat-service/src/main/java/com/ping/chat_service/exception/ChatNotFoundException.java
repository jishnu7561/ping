package com.ping.chat_service.exception;

public class ChatNotFoundException extends RuntimeException {

    public ChatNotFoundException (String message) {
        super(message);
    }
}
