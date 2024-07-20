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


    @PostMapping("/approveFollowRequest/{requestId}")
    public ResponseEntity<String> approveFollowRequest(@PathVariable Integer requestId) {
//        Integer requestId = requestBody.get("requestId");
        friendRequestService.approveFollowRequest(requestId);
        return ResponseEntity.ok("Follow request approved");
    }

    @DeleteMapping("/rejectFollowRequest/{requestId}")
    public ResponseEntity<String> rejectFollowRequest(@PathVariable Integer requestId) {
        friendRequestService.rejectFollowRequest(requestId);
        return ResponseEntity.ok("Follow request rejected");
    }

    @GetMapping("/followRequestStatus/{id}")
    public ResponseEntity<String> followRequestStatus (@RequestHeader("Authorization") String header,
                                                       @PathVariable("id") Integer followingId) {
//        System.out.println("header : "+header+"     "+"followingId"+requestBody.get("followingId"));
//        Integer followingId = requestBody.get("followingId");
        System.out.println("called the follow request");
        return ResponseEntity.ok(friendRequestService.followRequestStatus(header,followingId));
    }

}
