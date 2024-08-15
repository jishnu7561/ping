package com.ping.authservice.kafka.event;


import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Notification {


    private Integer id;
    private Integer sender;
    private Integer receiver;
    private Integer requestId;

    private TypeOfNotification typeOfNotification;
    private LocalDateTime createdAt;
    private Integer postId;
    private Integer commentId;
    private Boolean isRead;
}