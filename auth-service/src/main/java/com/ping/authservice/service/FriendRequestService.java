package com.ping.authservice.service;

import com.google.api.Http;
import com.ping.authservice.GlobalExceptionHandler.Exceptions.UsernameNotFoundException;
import com.ping.authservice.kafka.producer.KafkaMessagePublisher;
import com.ping.authservice.model.FriendRequest;
import com.ping.authservice.model.Status;
import com.ping.authservice.model.User;
import com.ping.authservice.repository.FriendRequestRepository;
import com.ping.authservice.repository.UserRepository;
import com.ping.authservice.util.BasicResponse;
import com.ping.common.dto.Notification;
import com.ping.common.dto.TypeOfNotification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class FriendRequestService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FriendRequestRepository friendRequestRepository;

    @Autowired
    private FollowService followService;

    @Autowired
    private UserService userService;

    @Autowired
    private KafkaMessagePublisher kafkaMessagePublisher;

    public BasicResponse sendFollowRequest(Integer followerId, Integer followingId) {
        try{
            User sender = userRepository.findById(followerId).orElseThrow(() -> new UsernameNotFoundException("user not found"));
            User receiver = userRepository.findById(followingId).orElseThrow(() -> new UsernameNotFoundException("user not found"));
            FriendRequest friendRequest = FriendRequest.builder()
                    .sender(sender)
                    .receiver(receiver)
                    .status(Status.PENDING)
                    .createdAt(LocalDateTime.now())
                    .build();
            FriendRequest savedRequest  = friendRequestRepository.save(friendRequest);
            System.out.println("savedRequest: "+savedRequest);

            kafkaMessagePublisher.sendNotification("notification", Notification.builder()
                    .sender(followerId)
                    .receiver(followingId)
                    .typeOfNotification(TypeOfNotification.FRIEND_REQUEST)
                    .createdAt(LocalDateTime.now())
                    .requestId(savedRequest.getId())
                    .build());

            return BasicResponse.builder()
                    .status(HttpStatus.OK.value())
                    .message("success")
                    .description("successfully ")
                    .timestamp(LocalDateTime.now())
                    .build();
        } catch (UsernameNotFoundException e) {
            throw new UsernameNotFoundException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public BasicResponse approveFollowRequest(Integer requestId) {
        try {
            FriendRequest request = friendRequestRepository.findById(requestId).orElseThrow(() -> new UsernameNotFoundException("request not found,send again"));
            request.setStatus(Status.ACCEPTED);
            friendRequestRepository.save(request);
            followService.follow(request.getSender().getId(),request.getReceiver().getId());

            kafkaMessagePublisher.sendNotification("notification", Notification.builder()
                    .sender(request.getSender().getId())
                    .receiver(request.getReceiver().getId())
                    .typeOfNotification(TypeOfNotification.FRIEND_REQUEST_ACCEPTED)
                    .createdAt(LocalDateTime.now())
                    .build());

            return BasicResponse.builder()
                    .status(HttpStatus.OK.value())
                    .message("success")
                    .description("request approved successfully")
                    .timestamp(LocalDateTime.now())
                    .build();
        } catch (UsernameNotFoundException e) {
            throw new UsernameNotFoundException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public String followRequestStatus(String header, Integer followingId) {
        try{
            User sender = userService.findUserByHeader(header);
            User receiver = userRepository.findById(followingId).orElseThrow(()-> new UsernameNotFoundException("user not found"));
            FriendRequest friendRequest = friendRequestRepository.findBySenderAndReceiver(sender,receiver);
            if(friendRequest != null) {
                return friendRequest.getStatus().toString();
            }
            return "";
        }catch (UsernameNotFoundException e) {
            throw new UsernameNotFoundException(e.getMessage());
        } catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    public void rejectFollowRequest(Integer requestId) {
        friendRequestRepository.deleteById(requestId);
    }
}
