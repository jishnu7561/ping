package com.ping.postservice.GlobalException;

import com.ping.postservice.GlobalException.Exceptions.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = {UserNotFoundException.class})
    public ErrorMessage userNotFoundException (UserNotFoundException ex) {
        return ErrorMessage.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .description("enable to find user")
                .timestamp(LocalDateTime.now())
                .build();
    }
}
