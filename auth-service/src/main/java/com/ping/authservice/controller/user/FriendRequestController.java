package com.ping.authservice.controller.user;

import com.ping.authservice.service.FollowService;
import com.ping.authservice.service.FriendRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user/api/secure/request")
public class FriendRequestController {

    @Autowired
    private FriendRequestService friendRequestService;


    @PostMapping("/approveFollowRequest")
    public ResponseEntity<String> approveFollowRequest(@RequestParam Integer requestId) {
//        Integer requestId = requestBody.get("requestId");
        friendRequestService.approveFollowRequest(requestId);
        return ResponseEntity.ok("Follow request approved");
    }

//    @PostMapping("/rejectFollowRequest")
//    public ResponseEntity<String> rejectFollowRequest(@RequestBody Map<String, Integer> requestBody) {
//        Integer requestId = requestBody.get("requestId");
//        followService.rejectFollowRequest(requestId);
//        return ResponseEntity.ok("Follow request rejected");
//    }

    @GetMapping("/followRequestStatus/{id}")
    public ResponseEntity<String> followRequestStatus (@RequestHeader("Authorization") String header,
                                                       @PathVariable("id") Integer followingId) {
//        System.out.println("header : "+header+"     "+"followingId"+requestBody.get("followingId"));
//        Integer followingId = requestBody.get("followingId");
        System.out.println("called the follow request");
        return ResponseEntity.ok(friendRequestService.followRequestStatus(header,followingId));
    }

}
