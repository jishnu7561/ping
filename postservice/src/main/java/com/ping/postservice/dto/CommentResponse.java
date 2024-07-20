package com.ping.postservice.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class CommentResponse {
    private int status;
    private String message;
    private String description;
    private List<CommentRequest> commentRequest;
    private LocalDateTime timestamp;
}
