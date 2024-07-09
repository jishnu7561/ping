package com.ping.postservice.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentResponse {
    private int status;
    private String message;
    private String description;
    private CommentRequest commentRequest;
    private LocalDateTime timestamp;
}
