package com.ping.chat_service.dto;

import com.ping.chat_service.model.NotificationType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NotificationDto {
    private Integer id;
    private Integer senderId;
    private Integer recipientId;
    private Integer requestId;
    private NotificationType typeOfNotification;
    private LocalDateTime date;
    private Integer postId;
    private Integer commentId;
}