package com.ping.authservice.controller.user;

import com.ping.authservice.dto.ProfileResponse;
import com.ping.authservice.model.User;
import com.ping.authservice.repository.FollowRepository;
import com.ping.authservice.service.FollowService;
import com.ping.authservice.service.FriendRequestService;
import com.ping.authservice.service.UserService;
import com.ping.authservice.service.firebase.ImageService;
import com.ping.authservice.util.BasicResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user/api/secure")
public class ProfileController {

    @Autowired
    private UserService userService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private FollowService followService;

    @Autowired
    private FriendRequestService friendRequestService;

    @GetMapping("/profile/{id}")
    public ResponseEntity<ProfileResponse> userProfile(@PathVariable("id") int userId) {
//        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(userService.getUserDetails(userId));
    }

    @PostMapping("/update-profile/{id}")
    public ResponseEntity<BasicResponse> userProfileUpdate(@PathVariable("id") int userId,@RequestBody ProfileResponse request) {
        return ResponseEntity.ok(userService.updateUserDetails(userId,request));
    }

    @PostMapping("/uploadImage")
    public ResponseEntity<BasicResponse> uploadImage(@RequestParam("file") MultipartFile file, @RequestParam("id") Integer id) {
        String imageUrl = imageService.upload(file);
        System.out.println("called the       upload image  ");
        userService.saveImageUrl(imageUrl,id);
        return ResponseEntity.ok(BasicResponse.builder()
                .status( HttpStatus.OK.value())
                .message("Success")
                .description(imageUrl)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @GetMapping("/getUserDetails/{id}")
    public ResponseEntity<ProfileResponse> getUserDetails(@PathVariable("id") int userId) {
        return ResponseEntity.ok(userService.getUserDetails(userId));
    }

    @Autowired
    private FollowRepository followRepository;

    @PostMapping("/followUser")
    public ResponseEntity<String> follow(@RequestBody Map<String, Integer> requestBody) {
        Integer followingId = requestBody.get("followingId");
        Integer followerId = requestBody.get("followerId");
        followService.follow(followerId,followingId);
        return ResponseEntity.ok("followed successfully");
    }

    @DeleteMapping("/unfollowUser")
    public ResponseEntity<String> unfollow(@RequestBody Map<String, Integer> requestBody) {
        Integer followingId = requestBody.get("followingId");
        Integer followerId = requestBody.get("followerId");
        followService.unfollow(followerId,followingId);
        return ResponseEntity.ok("followed successfully");
    }

    @PostMapping("/isFollowing/{userId}")
    public ResponseEntity<Boolean> isFollowing(@PathVariable Integer userId, @RequestBody Map<String, Integer> requestBody) {
        System.out.println("calling isfollowing");
        Integer followingId = requestBody.get("followingId");
        return ResponseEntity.ok(followRepository.findByFollowerIdAndFollowingId(userId,followingId).isPresent());
    }

    @PostMapping("/handlePrivacy")
    public ResponseEntity<BasicResponse> handlePrivacy(@RequestHeader("Authorization") String header) {
        return ResponseEntity.ok(userService.handlePrivacy(header));
    }

    @PostMapping("/sendFollowRequest")
    public ResponseEntity<BasicResponse> sendFollowRequest(@RequestBody Map<String, Integer> requestBody){
        Integer followingId = requestBody.get("followingId");
        Integer followerId = requestBody.get("followerId");
        return ResponseEntity.ok(friendRequestService.sendFollowRequest(followerId,followingId));
    }

    @GetMapping("/getAllUsersOnSearch")
    public List<User> getAllUsers(
            @RequestParam(defaultValue = "") String search
    ) {
        return userService.getAllUsersBasedOnSearch(search);
    }

}
