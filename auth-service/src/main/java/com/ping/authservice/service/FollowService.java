package com.ping.authservice.service;

import com.ping.authservice.kafka.producer.KafkaMessagePublisher;

import com.ping.authservice.model.Follow;
import com.ping.authservice.repository.FollowRepository;
import com.ping.authservice.kafka.event.Notification;
import com.ping.authservice.kafka.event.TypeOfNotification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;

@Service
public class FollowService {

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private KafkaMessagePublisher kafkaMessagePublisher;

    public void follow(Integer followerId, Integer followingId) {
        Follow follow = Follow.builder()
                .followerId(followerId)
                .followingId(followingId)
                .followDate(LocalDateTime.now())
                .build();

        followRepository.save(follow);
        kafkaMessagePublisher.sendNotification("notification", Notification.builder()
                .sender(followerId)
                .receiver(followingId)
                .typeOfNotification(TypeOfNotification.FOLLOW)
                .createdAt(LocalDateTime.now())
                .build());
    }


    @Transactional
    public void unfollow(Integer followerId, Integer followingId) {
        followRepository.deleteByFollowerIdAndFollowingId(followerId,followingId);
        kafkaMessagePublisher.sendNotification("notification", Notification.builder()
                .sender(followerId)
                .receiver(followingId)
                .typeOfNotification(TypeOfNotification.UNFOLLOW)
                .createdAt(LocalDateTime.now())
                .build());
    }

//    public List<User> getFollowers(Integer userId) {
//        return followRepository.findByFollowing(userId);
//    }
//
//    public List<User> getFollowing(Integer userId) {
//        return followRepository.findByFollower(userId);
//    }
}
