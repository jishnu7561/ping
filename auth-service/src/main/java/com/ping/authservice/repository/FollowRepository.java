package com.ping.authservice.repository;

import com.ping.authservice.model.Follow;
import com.ping.authservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow,Integer> {

    void deleteByFollowerIdAndFollowingId(Integer followerId, Integer followingId);

//    List<User> findFollowing(Integer userId);
//
//    List<User> findFollowers(Integer userId);

    Optional<Follow> findByFollowerIdAndFollowingId(Integer userId, Integer followingId);

    List<Follow> findByFollowingId(Integer userId);

    List<Follow> findByFollowerId(Integer userId);
}
