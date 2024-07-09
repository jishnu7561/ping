package com.ping.chat_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class MessageResponse {

    private Integer id;
    private Integer sender;
    private Integer receiver;
    private String content;
    private LocalDateTime createdAt;
    private Boolean isRead;
}
