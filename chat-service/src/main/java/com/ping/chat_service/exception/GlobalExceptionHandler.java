package com.ping.chat_service.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = {UserNotFoundException.class})
    public ErrorMessage UserNotFoundException(UserNotFoundException ex, WebRequest req) {
        return ErrorMessage.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .description(req.getDescription(false))
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(value = {ChatNotFoundException.class})
    public ErrorMessage ChatNotFoundException(ChatNotFoundException ex, WebRequest req) {
        return ErrorMessage.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .description(req.getDescription(false))
                .timestamp(LocalDateTime.now())
                .build();
    }


}
