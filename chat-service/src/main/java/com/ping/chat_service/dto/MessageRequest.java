package com.ping.chat_service.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MessageRequest {

    private Integer chatId;
    private Integer receiverId;
    private Integer senderId;
    private String content;
}
