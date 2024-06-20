package com.ping.authservice.GlobalExceptionHandler;

import com.ping.authservice.GlobalExceptionHandler.Exceptions.CustomBadCredentialException;
import com.ping.authservice.GlobalExceptionHandler.Exceptions.UserBlockedException;
import com.ping.authservice.GlobalExceptionHandler.Exceptions.UsernameNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = {UsernameNotFoundException.class})
    public ErrorMessage userNameNotFoundException(UsernameNotFoundException ex ) {
        return ErrorMessage.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .description("enable to find user")
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(value = {CustomBadCredentialException.class})
    public ErrorMessage customBadCredentialException (CustomBadCredentialException ex) {
        return ErrorMessage.builder()
                .status(HttpStatus.FORBIDDEN.value())
                .message(ex.getMessage())
                .description("Your credential have mismatch take a look")
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(value = {UserBlockedException.class})
    public ErrorMessage userBlockedException (UserBlockedException ex) {
        return ErrorMessage.builder()
                .status(HttpStatus.FORBIDDEN.value())
                .message(ex.getMessage())
                .description("Your account is blocked by the admin")
                .timestamp(LocalDateTime.now())
                .build();
    }

}
