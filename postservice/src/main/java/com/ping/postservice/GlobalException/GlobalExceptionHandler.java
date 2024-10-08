package com.ping.postservice.GlobalException;

import com.ping.postservice.GlobalException.Exceptions.ResourceNotFoundException;
import com.ping.postservice.GlobalException.Exceptions.UserNotFoundException;
import com.ping.postservice.dto.BasicResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

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
    @ExceptionHandler(value = {ResourceNotFoundException.class})
    public ErrorMessage resourceNotFoundException (ResourceNotFoundException ex) {
        return ErrorMessage.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .description("enable to find resource")
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<BasicResponse> handleMaxSizeException(MaxUploadSizeExceededException exc) {
        BasicResponse response = BasicResponse.builder()
                .status(HttpStatus.PAYLOAD_TOO_LARGE.value())
                .message("File size exceeds the maximum limit.")
                .description(exc.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(response);
    }
}
