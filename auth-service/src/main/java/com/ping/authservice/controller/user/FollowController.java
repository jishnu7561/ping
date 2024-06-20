//package com.ping.authservice.controller.user;
//
//import com.ping.authservice.model.User;
//import com.ping.authservice.repository.FollowRepository;
//import com.ping.authservice.service.FollowService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/user/api/secure/follow")
//public class FollowController {
//
//    @Autowired
//    private FollowService followService;
//
//    @Autowired
//    private FollowRepository followRepository;
//
//    @PostMapping("/followUser")
//    public ResponseEntity<String> follow(@RequestParam Integer followerId,@RequestParam Integer followingId) {
//        followService.follow(followerId,followingId);
//        return ResponseEntity.ok("followed successfully");
//    }
//
//    @DeleteMapping("/unfollowUser")
//    public ResponseEntity<String> unfollow(@RequestParam Integer followerId,@RequestParam Integer followingId) {
//        followService.unfollow(followerId,followingId);
//        return ResponseEntity.ok("followed successfully");
//    }
//
//    @PostMapping("/isFollowing/{userId}")
//    public ResponseEntity<Boolean> isFollowing(@PathVariable Integer userId,@RequestParam Integer followingId) {
//        return ResponseEntity.ok(followRepository.findByFollowerIdAndFollowingId(userId,followingId).isPresent());
//    }
//}
