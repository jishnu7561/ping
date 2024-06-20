package com.ping.authservice.service;

import com.ping.authservice.model.Follow;
import com.ping.authservice.model.User;
import com.ping.authservice.repository.FollowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FollowService {

    @Autowired
    private FollowRepository followRepository;
    public void follow(Integer followerId, Integer followingId) {
        Follow follow = Follow.builder()
                .followerId(followerId)
                .followingId(followingId)
                .followDate(LocalDateTime.now())
                .build();

        followRepository.save(follow);

    }


    @Transactional
    public void unfollow(Integer followerId, Integer followingId) {
        followRepository.deleteByFollowerIdAndFollowingId(followerId,followingId);
    }

//    public List<User> getFollowers(Integer userId) {
//        return followRepository.findByFollowing(userId);
//    }
//
//    public List<User> getFollowing(Integer userId) {
//        return followRepository.findByFollower(userId);
//    }
}
