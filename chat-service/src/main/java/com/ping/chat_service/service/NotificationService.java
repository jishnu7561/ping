package com.ping.chat_service.service;

import com.ping.chat_service.dto.NotificationDto;
import com.ping.chat_service.dto.NotificationResponse;
import com.ping.chat_service.dto.User;
import com.ping.chat_service.exception.UserNotFoundException;
import com.ping.chat_service.feign.PostFeign;
import com.ping.chat_service.feign.UserClient;
import com.ping.chat_service.kafka.event.TypeOfNotification;
import com.ping.chat_service.model.Notification;
import com.ping.chat_service.model.NotificationType;
import com.ping.chat_service.repository.NotificationRepository;
import com.ping.chat_service.util.BasicResponse;
import com.ping.chat_service.util.TimeAgoUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserClient userClient;

    @Autowired
    private TimeAgoUtil timeAgoUtil;

    @Autowired
    private PostFeign postFeign;


    public void saveNotification(com.ping.chat_service.kafka.event.Notification notificationDto) {
        Notification notification = Notification.builder()
                .sender(notificationDto.getSender())
                .receiver(notificationDto.getReceiver())
                .typeOfNotification(notificationDto.getTypeOfNotification())
                .requestId(notificationDto.getRequestId())
                .createdAt(notificationDto.getCreatedAt())
                .commentId(notificationDto.getCommentId())
                .postId(notificationDto.getPostId())
                .build();
        notificationRepository.save(notification);
    }

//    public List<NotificationResponse> getAllNotifications(Integer userId) {
//        try {
//            List<Notification> notifications = notificationRepository.findByReceiver(userId);
//            List<NotificationResponse> responseList = new ArrayList<>();
//            for(Notification notification : notifications) {
//                NotificationResponse response = new NotificationResponse();
//                User user = userClient.getUserIfExist(notification.getSender()).getBody();
//                response.setNotificationId(notification.getId());
//                response.setPostId(notification.getPostId());
//                response.setSender(user.getAccountName());
//                response.setType(notification.getTypeOfNotification());
//                response.setRequestId(notification.getRequestId());
//                response.setCreatedAt(timeAgoUtil.timeAgo(notification.getCreatedAt()));
//                response.setProfileImage(user.getImageUrl());
//                response.setPostImage(postFeign.getPostDetails(notification.getPostId()).getBody().getPostImage());
//                response.setSenderId(notification.getSender());
//                responseList.add(response);
//            }
//            System.out.println("notifications :" +notifications);
//            System.out.println("responseList :" +responseList);
//            Collections.reverse(responseList);
//            return responseList;
//
//        } catch (UserNotFoundException e) {
//            throw new UserNotFoundException(e.getMessage());
//        } catch (Exception e) {
//            throw new RuntimeException("Internal server error");
//        }
//
//    }
public List<NotificationResponse> getAllNotifications(Integer userId) {
    List<NotificationResponse> responseList = new ArrayList<>();
    log.warn("called the getAllNotification method");
    try {
        List<Notification> notifications = notificationRepository.findByReceiver(userId);
        if (notifications == null || notifications.isEmpty()) {
            log.warn("No notifications found for userId: {}", userId);
            return responseList; // Return an empty list if no notifications are found
        }

        for(Notification notification : notifications) {
            NotificationResponse response = new NotificationResponse();

            // Fetch user information
            User user = userClient.getUserIfExist(notification.getSender()).getBody();
            if (user == null) {
                log.error("User not found for sender ID: {}", notification.getSender());
                continue; // Skip this notification if the user is not found
            }

            // Set response details
            response.setNotificationId(notification.getId());
            response.setPostId(notification.getPostId());
            response.setSender(user.getAccountName());
            response.setType(notification.getTypeOfNotification());
            response.setRequestId(notification.getRequestId());
            response.setCreatedAt(timeAgoUtil.timeAgo(notification.getCreatedAt()));
            response.setProfileImage(user.getImageUrl());

            // Fetch post image
            String postImage = postFeign.getPostDetails(notification.getPostId()).getBody().getPostImage();
            if (postImage != null) {
                response.setPostImage(postImage);
            } else {
                log.warn("No post image found for post ID: {}", notification.getPostId());
            }

            response.setSenderId(notification.getSender());
            responseList.add(response);
        }

        Collections.reverse(responseList);
    } catch (UserNotFoundException e) {
        log.error("UserNotFoundException: {}", e.getMessage());
        throw e;
    } catch (Exception e) {
        log.error("Exception occurred while fetching notifications: {}", e.getMessage(), e);
        throw new RuntimeException("Internal server error", e);
    }

    return responseList;
}


    public NotificationResponse getNotification(com.ping.chat_service.kafka.event.Notification notification) {
        try {
            NotificationResponse response = new NotificationResponse();

                User user = userClient.getUserIfExist(notification.getSender()).getBody();
                response.setNotificationId(user.getId());
                response.setPostId(notification.getPostId());
                response.setSender(user.getAccountName());
                response.setType(notification.getTypeOfNotification());
                if(notification.getTypeOfNotification() == TypeOfNotification.FRIEND_REQUEST){
                    response.setRequestId(notification.getRequestId());
                    System.out.println(notification.getRequestId());
                }
                response.setCreatedAt(timeAgoUtil.timeAgo(notification.getCreatedAt()));
            response.setRequestId(notification.getRequestId());
//                response.setCreatedAt(notification.getCreatedAt());
                response.setProfileImage(user.getImageUrl());

//                response.setPostImage();
                response.setSenderId(notification.getSender());

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
