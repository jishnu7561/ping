package com.ping.chat_service.dto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class MessageRequest {

    private Integer messageId;
    private Integer userId;
    private Integer chatId;
    private Integer receiverId;
    private Integer senderId;
    private String content;
    private String header;
}
