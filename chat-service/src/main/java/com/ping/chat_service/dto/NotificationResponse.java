package com.ping.chat_service.dto;

import com.ping.chat_service.model.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationResponse {

    private String sender;
    private Integer senderId;
    private String profileImage;
    private String createdAt;
    private Integer requestId;
    private NotificationType type;
    private String postImage;
    private Integer notificationId;
    private Integer postId;

}
