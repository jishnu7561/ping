package com.ping.apigateway.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler( value = {AuthHeaderNotFountException.class})
    public ResponseEntity<ErrorMessage> authHeaderNotFound(AuthHeaderNotFountException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ErrorMessage.builder()
                .message(ex.getMessage())
                .description("provide the authorization header to hit this end point")
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_ACCEPTABLE.value())
                .build());
    }

    @ExceptionHandler(value = {TokenInvalidException.class})
    public ResponseEntity<ErrorMessage> tokenInvalid(TokenInvalidException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ErrorMessage.builder()
                .message(ex.getMessage())
                .description("token expired please login to get new token")
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_ACCEPTABLE.value())
                .build());
    }

    @ExceptionHandler(value = {UserBlockedException.class})
    public ResponseEntity<ErrorMessage> userIsBlocked(UserBlockedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ErrorMessage.builder()
                .message(ex.getMessage())
                .description("User is blocked by the admin")
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_ACCEPTABLE.value())
                .build());
    }

}
