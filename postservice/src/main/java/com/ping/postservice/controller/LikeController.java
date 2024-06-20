package com.ping.postservice.controller;

import com.ping.postservice.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/post/like")
public class LikeController {

    @Autowired
    private LikeService likeService;

    @PostMapping("/likePost")
    public ResponseEntity<String> likePost(@RequestBody Map<String, Object> requestBody) {
        Integer postId = (Integer) requestBody.get("postId");
        Integer userId = (Integer) requestBody.get("userId");
        System.out.println("calling liked");
        System.out.println("posId="+postId+" "+" userId="+userId);
        likeService.likePost(postId, userId);
        return ResponseEntity.ok("liked");
    }

    @PostMapping("/unLikePost")
    public ResponseEntity<String> unLikePost(@RequestBody Map<String, Integer> requestBody) {
        Integer postId = requestBody.get("postId");
        Integer userId = requestBody.get("userId");
        System.out.println("calling unliked");
        System.out.println("posId="+postId+" "+" userId="+userId);
        likeService.unlikePost(postId,userId);
        return ResponseEntity.ok("unliked");
    }


}
