package com.ping.chat_service.service;

import com.ping.chat_service.dto.NotificationDto;
import com.ping.chat_service.dto.NotificationResponse;
import com.ping.chat_service.dto.User;
import com.ping.chat_service.exception.UserNotFoundException;
import com.ping.chat_service.feign.UserClient;
import com.ping.chat_service.model.Notification;
import com.ping.chat_service.model.NotificationType;
import com.ping.chat_service.repository.NotificationRepository;
import com.ping.chat_service.util.BasicResponse;
import com.ping.chat_service.util.TimeAgoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserClient userClient;

    @Autowired
    private TimeAgoUtil timeAgoUtil;


    public void saveNotification(NotificationDto notificationDto) {
        Notification notification = Notification.builder()
                .sender(notificationDto.getSenderId())
                .receiver(notificationDto.getRecipientId())
                .typeOfNotification(notificationDto.getTypeOfNotification())
                .requestId(notificationDto.getRequestId())
                .createdAt(notificationDto.getDate())
                .commentId(notificationDto.getCommentId())
                .postId(notificationDto.getPostId())
                .build();
        notificationRepository.save(notification);
    }

    public List<NotificationResponse> getAllNotifications(Integer userId) {
        try {
            List<Notification> notifications = notificationRepository.findByReceiver(userId);
            List<NotificationResponse> responseList = new ArrayList<>();
            for(Notification notification : notifications) {
                NotificationResponse response = new NotificationResponse();
                User user = userClient.getUserIfExist(notification.getSender()).getBody();
                response.setNotificationId(notification.getId());
                response.setPostId(notification.getPostId());
                response.setSender(user.getAccountName());
                response.setType(notification.getTypeOfNotification());
                response.setRequestId(notification.getRequestId());
                response.setCreatedAt(timeAgoUtil.timeAgo(notification.getCreatedAt()));
                response.setProfileImage(user.getImageUrl());
//                response.setPostImage();
                response.setSenderId(notification.getSender());
                responseList.add(response);
            }
            System.out.println("notifications :" +notifications);
            System.out.println("responseList :" +responseList);
            return responseList;

        } catch (UserNotFoundException e) {
            throw new UserNotFoundException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    public NotificationResponse getNotification(NotificationDto notification) {
        try {
            NotificationResponse response = new NotificationResponse();

                User user = userClient.getUserIfExist(notification.getSenderId()).getBody();
                response.setNotificationId(user.getId());
                response.setPostId(notification.getPostId());
                response.setSender(user.getAccountName());
                response.setType(notification.getTypeOfNotification());
                if(notification.getTypeOfNotification() == NotificationType.FRIEND_REQUEST){
                    response.setRequestId(notification.getRequestId());
                    System.out.println(notification.getRequestId());
                }
                response.setCreatedAt(timeAgoUtil.timeAgo(notification.getDate()));
            response.setRequestId(notification.getRequestId());
//                response.setCreatedAt(notification.getCreatedAt());
                response.setProfileImage(user.getImageUrl());

//                response.setPostImage();
                response.setSenderId(notification.getSenderId());

            return response;

        } catch (UserNotFoundException e) {
            throw new UserNotFoundException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Transactional
    public BasicResponse deleteNotification(Integer notificationId) {
        try{
            notificationRepository.deleteById(notificationId);
            return BasicResponse.builder()
                    .status(HttpStatus.OK.value())
                    .message("deleted successfully")
                    .description("notification deleted successfully")
                    .timestamp(LocalDateTime.now())
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Internal server error");
        }
    }

    @Transactional
    public void markNotificationAsRead(Integer userId) {
        notificationRepository.markNotificationAsRead(userId);
    }


    public Integer getNotificationCount(Integer id) {
        return notificationRepository.countUnreadNotification(id);
    }
}
