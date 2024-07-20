package com.ping.chat_service.kafka.consumer;

import com.ping.chat_service.dto.NotificationDto;
import com.ping.chat_service.dto.NotificationResponse;
import com.ping.chat_service.model.NotificationType;
import com.ping.chat_service.service.NotificationService;
import com.ping.common.dto.Notification;
import com.ping.common.dto.TypeOfNotification;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaMessageListener {

    @Autowired
    private NotificationService notificationService;

    private final SimpMessagingTemplate messagingTemplate;

    Logger log = LoggerFactory.getLogger(KafkaMessageListener.class);

    @KafkaListener(topics = "notification", groupId = "ping_notification")
    public void consume(Notification notification) {

        log.info("consumer consume the message {} ", notification);
        notificationService.saveNotification(messageToDTO(notification));

//        messagingTemplate.convertAndSendToUser(
//                String.valueOf(notification.getReceiver()),
//                "/queue/notification",
//                notification
//        );
        System.out.println("notificationId on websocket is :   "+notification.getReceiver());
        NotificationResponse response = notificationService.getNotification(messageToDTO(notification));
        messagingTemplate.convertAndSend("/chat/notification/"+notification.getReceiver(),response);

    }

//    @KafkaListener(topics = "ping-demo", groupId = "ping-1")
//    public void consumeEvent(Customer customer) {
//        log.info("consumer consume the message {} ", customer.toString());
//    }

    private NotificationDto messageToDTO(Notification message) {
        NotificationType typeOfNotification;
        if ( message.getTypeOfNotification() == TypeOfNotification.LIKE ) {
            typeOfNotification = NotificationType.LIKE;
        } else if ( message.getTypeOfNotification() == TypeOfNotification.COMMENT ) {
            typeOfNotification = NotificationType.COMMENT;
        } else if ( message.getTypeOfNotification() == TypeOfNotification.FRIEND_REQUEST ) {
            typeOfNotification = NotificationType.FRIEND_REQUEST;
        } else if ( message.getTypeOfNotification() == TypeOfNotification.FOLLOW) {
            typeOfNotification = NotificationType.FOLLOW;
        } else if ( message.getTypeOfNotification() == TypeOfNotification.UNFOLLOW) {
            typeOfNotification = NotificationType.UNFOLLOW;
        } else if ( message.getTypeOfNotification() == TypeOfNotification.FRIEND_REQUEST_ACCEPTED ) {
            typeOfNotification = NotificationType.FRIEND_REQUEST_ACCEPTED;
        } else {
            throw new RuntimeException("Invalid NotificationType");
        }
        return NotificationDto.builder()
                .id(message.getId())
                .senderId(message.getSender())
                .recipientId(message.getReceiver())
                .requestId(message.getRequestId())
                .typeOfNotification(typeOfNotification)
                .date(message.getCreatedAt())
                .postId(message.getPostId())
                .commentId(message.getCommentId())
                .build();
    }

}
