package com.ping.chat_service.dto;

import com.ping.chat_service.model.Chat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChattingUserResponse {

    private Integer userId;
    private String accountName;
    private String email;
    private String bio;
    private String imageUrl;

    private Integer chatId;
    private Integer user1Id;
    private Integer user2Id;
    private String lastMessage;
    private String lastMessageDate;
    private boolean isRead;
    private Integer unreadMessage;
}
