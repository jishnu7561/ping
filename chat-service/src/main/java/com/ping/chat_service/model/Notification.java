package com.ping.chat_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "notifications")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "sender_id")
    private Integer sender;
    @Column(name = "receiver_id")
    private Integer receiver;

    private TypeOfNotification typeOfNotification;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "post_id")
    private Long postId;
    @Column(name = "comment_id")
    private Long commentId;
    @Column(name = "is_read")
    private Boolean isRead;
}
