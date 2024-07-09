package com.ping.chat_service.exception;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@Builder
public class ErrorMessage {

    private int status;
    private LocalDateTime timestamp;
    private String message;
    private String description;
}
